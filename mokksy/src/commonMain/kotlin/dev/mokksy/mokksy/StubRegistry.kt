package dev.mokksy.mokksy

import dev.mokksy.mokksy.utils.logger.HttpFormatter
import io.ktor.server.routing.RoutingRequest
import io.ktor.util.logging.Logger
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Thread-safe, multiplatform stub registry with atomic operations.
 *
 * Uses kotlinx.atomicfu for lock-free operations where possible,
 * and kotlinx.coroutines Mutex for complex multistep operations that may suspend.
 * @author Konstantin Pavlov
 */
internal class StubRegistry {
    // Atomic reference to an immutable sorted list
    private val stubs: AtomicRef<PersistentList<Stub<*, *>>> = atomic(persistentListOf())

    // Lock for operations requiring atomicity across multiple steps
    private val mutex = Mutex()

    /**
     * Atomically adds a stub to the registry.
     *
     * Uses lock-free update operation that retries until successful.
     *
     * @throws IllegalArgumentException if stub is already registered
     */
    fun add(stub: Stub<*, *>) {
        stubs.update { currentList ->
            val index = currentList.binarySearch(stub, StubComparator)
            require(index < 0) { "Duplicate stub detected: ${stub.toLogString()}" }
            val insertionIndex = -(index + 1)
            currentList.add(insertionIndex, stub)
        }
    }

    /**
     * Atomically finds and optionally removes the best matching stub.
     *
     * This operation is atomic to prevent TOCTOU race conditions:
     * - Match and remove happen in a single critical section
     * - Match count is incremented atomically
     * - No other thread can interfere between match and remove
     *
     * @param request The incoming HTTP request to match
     * @return The matched stub, or null if no match found
     */
    suspend fun findMatchingStub(
        request: RoutingRequest,
        verbose: Boolean,
        logger: Logger,
        formatter: HttpFormatter,
    ): Stub<*, *>? {
        val formattedRequest =
            if (verbose) {
                @Suppress("TooGenericExceptionCaught")
                try {
                    formatter.formatRequest(request)
                } catch (e: Exception) {
                    "<Unable to format request: ${e.message}>"
                }
            } else {
                ""
            }

        return mutex.withLock {
            val currentSnapshot = stubs.value
            var match: Stub<*, *>? = null

            for (stub in currentSnapshot) {
                val result = stub.requestSpecification.matches(request)

                if (result.isFailure && verbose) {
                    logger.warn(
                        "Failed to evaluate condition for stub: ${stub.toLogString()}. " +
                            "Request: $formattedRequest",
                        result.exceptionOrNull(),
                    )
                }

                if (result.getOrNull() == true) {
                    match = stub
                    break
                }
            }

            if (match == null) return@withLock null

            // 2. Increment match count
            match.incrementMatchCount()

            // 3. Conditional locking for removal
            if (match.configuration.removeAfterMatch) {
                // Already under lock
                stubs.update { it.remove(match) }
                if (verbose) {
                    logger.debug("Removed used stub: ${match.toLogString()}")
                }
            }

            match
        }
    }

    /**
     * Atomically removes a specific stub.
     *
     * @return true if stub was removed, false if it wasn't present
     */
    fun remove(stub: Stub<*, *>): Boolean {
        var removed = false
        stubs.update { currentList ->
            val index = currentList.indexOf(stub)
            if (index != -1) {
                removed = true
                currentList.removeAt(index)
            } else {
                removed = false
                currentList
            }
        }
        return removed
    }

    /**
     * Returns a snapshot of all registered stubs.
     *
     * This is a consistent snapshot at a point in time.
     */
    fun getAll(): List<Stub<*, *>> = stubs.value
}
