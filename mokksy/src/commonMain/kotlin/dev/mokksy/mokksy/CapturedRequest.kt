package dev.mokksy.mokksy

import io.ktor.server.application.log
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.request.receiveNullable
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass

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
    public val request: ApplicationRequest,
    private val type: KClass<P>,
) {
    private val bodyCache: AtomicRef<P?> = atomic(null)
    private val bodyStringCache: AtomicRef<String?> = atomic(null)

    // Ensure only one coroutine performs the initial receive for each cache
    private val bodyMutex: Mutex = Mutex()
    private val bodyStringMutex: Mutex = Mutex()

    public val body: P
        get() =
            bodyCache.value ?: runBlocking {
                bodyMutex.withLock {
                    bodyCache.value ?: run {
                        val received =
                            try {
                                request.call.receive(type)
                            } catch (e: ContentTransformationException) {
                                request.call.application.log.debug("Failed to parse request body to $type", e)
                                throw e
                            }
                        bodyCache.value = received
                        received
                    }
                }
            }

    /**
     * Provides the request body as a string.
     * Caches the result after the first call.
     */
    public val bodyAsString: String?
        get() =
            bodyStringCache.value ?: runBlocking {
                bodyStringMutex.withLock {
                    bodyStringCache.value ?: run {
                        val received = request.call.receiveNullable<String>()
                        bodyStringCache.value = received
                        received
                    }
                }
            }
}
