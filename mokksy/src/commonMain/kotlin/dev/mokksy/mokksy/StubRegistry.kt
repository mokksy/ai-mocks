package dev.mokksy.mokksy

import dev.mokksy.mokksy.utils.logger.HttpFormatter
import io.ktor.server.routing.RoutingRequest
import io.ktor.util.logging.Logger
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
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
    // Atomic reference to an immutable sorted set
    private val stubs: AtomicRef<Set<Stub<*, *>>> = atomic(emptySet())

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
        stubs.update { currentSet ->
            require(stub !in currentSet) { "Duplicate stub detected: ${stub.toLogString()}" }
            (currentSet + stub).toSortedSet(StubComparator)
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
    ): Stub<*, *>? =
        mutex.withLock {
            val currentSet = stubs.value

            // Find best match using priority and creation order
            val match =
                currentSet
                    .filter { stub ->
                        stub.requestSpecification
                            .matches(request)
                            .onFailure {
                                if (verbose) {
                                    logger.warn(
                                        "Failed to evaluate condition for stub:\n---\n${
                                            stub.toLogString()
                                        }\n---" +
                                            "\nand request:\n---\n${
                                                formatter.formatRequest(request)
                                            }---",
                                        it,
                                    )
                                }
                            }.getOrNull() == true
                    }.minWithOrNull(StubComparator)

            if (match != null) {
                // Atomically increment match count
                match.incrementMatchCount()

                // Atomically remove if configured
                if (match.configuration.removeAfterMatch) {
                    stubs.update { (currentSet - match).toSortedSet(StubComparator) }
                    if (verbose) {
                        logger.debug(
                            "Removed used stub: ${match.toLogString()}",
                        )
                    }
                }
            }

            match
        }

    /**
     * Atomically removes a specific stub.
     *
     * @return true if stub was removed, false if it wasn't present
     */
    fun remove(stub: Stub<*, *>): Boolean {
        var removed = false
        stubs.update { currentSet ->
            removed = stub in currentSet
            currentSet - stub
        }
        return removed
    }

    /**
     * Returns a snapshot of all registered stubs.
     *
     * This is a consistent snapshot at a point in time.
     */
    fun getAll(): Set<Stub<*, *>> = stubs.value
}

/**
 * Atomic update operation that retries until successful.
 *
 * This is a lock-free operation that:
 * 1. Reads current value
 * 2. Computes new value
 * 3. Attempts compareAndSet
 * 4. Retries if another thread modified the value
 */
private inline fun <T> AtomicRef<T>.update(function: (T) -> T) {
    while (true) {
        val current = value
        val updated = function(current)
        if (compareAndSet(current, updated)) {
            return
        }
        // Another thread modified the value, retry
    }
}
