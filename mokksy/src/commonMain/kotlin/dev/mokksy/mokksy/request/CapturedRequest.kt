package dev.mokksy.mokksy.request

import io.ktor.server.application.log
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.request.receiveNullable
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass

private sealed interface BodyStringCache

private data object Unset : BodyStringCache

private class Cached(
    val value: String?,
) : BodyStringCache

/**
 * Represents an HTTP request that has been captured and provides utilities to access
 * the request's body and its string representation.
 *
 * @param P The type of the request payload.
 * @property request The raw incoming HTTP request being captured.
 * @property type The class type of the expected request payload.
 *
 * The class caches the parsed body and its string representation for reuse across multiple invocations,
 * ensuring that the body content is read and transformed only once.
 *
 * @author Konstantin Pavlov
 */
public class CapturedRequest<P : Any>(
    internal val request: ApplicationRequest,
    private val type: KClass<P>,
) {
    private val bodyCache: AtomicRef<P?> = atomic(null)
    private val bodyStringCache: AtomicRef<BodyStringCache> = atomic(Unset)

    // Single mutex guards both body and bodyAsString to prevent concurrent reads
    // of the same one-shot HTTP body stream.
    private val mutex: Mutex = Mutex()

    /**
     * Returns the parsed request body as [P], suspending if the body has not yet been read.
     *
     * The result is cached after the first call; subsequent calls return immediately without I/O.
     *
     * @throws [ContentTransformationException] if the body cannot be deserialized to [P].
     */
    public suspend fun body(): P {
        bodyCache.value?.let { return it }
        return mutex.withLock {
            bodyCache.value ?: run {
                val received =
                    try {
                        request.call.receive(type)
                    } catch (e: ContentTransformationException) {
                        request.call.application.log
                            .debug(
                                "Failed to parse request body to $type",
                                e,
                            )
                        throw e
                    }
                bodyCache.value = received
                received
            }
        }
    }

    /**
     * Returns the raw request body as a [String], suspending if the body has not yet been read.
     *
     * The result is cached after the first call; later calls return immediately without I/O.
     * Returns `null` if the request has no body.
     */
    public suspend fun bodyAsString(): String? {
        val cached = bodyStringCache.value
        if (cached is Cached) return cached.value
        return mutex.withLock {
            val local = bodyStringCache.value
            if (local is Cached) return@withLock local.value
            val received = request.call.receiveNullable<String>()
            bodyStringCache.value = Cached(received)
            received
        }
    }
}
