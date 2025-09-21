package me.kpavlov.mokksy.response

import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.ResponseHeaders
import io.ktor.server.response.cacheControl
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.sse.ServerSSESession
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.yield
import kotlin.time.Duration

internal const val SEND_BUFFER_CAPACITY = 256

/**
 * Represents a definition for streaming responses, supporting chunked data and flow-based content streaming.
 * This class extends the base `AbstractResponseDefinition` to provide additional functionality specific
 * to chunked or streamed responses. It can handle flow-based content delivery, manage chunk-wise delays,
 * and supports various output formats such as `OutputStream`, `Writer`, or `ServerSSESession`.
 *
 * @param P The type of the request body.
 * @param T The type of the response data being streamed.
 * @property chunkFlow A `Flow` of chunks to be streamed as part of the response.
 * @property chunks A list of chunks representing the response data to be sent.
 * @property delayBetweenChunks Delay between the transmission of each chunk.
 * @property httpStatusCode The HTTP status code of the response as Int, defaulting to 200.
 * @property httpStatus The HTTP status code of the response, defaulting to [HttpStatusCode.OK].
 * @constructor Initializes a streaming response definition with the specified flow, chunk list, content type,
 *              HTTP status code, and headers.
 *
 * Extends:
 * @see AbstractResponseDefinition
 */
@Suppress("LongParameterList")
public open class StreamResponseDefinition<P, T>(
    public open val chunkFlow: Flow<T>? = null,
    public val chunks: List<T>? = null,
    public val delayBetweenChunks: Duration = Duration.ZERO,
    contentType: ContentType = ContentType.Text.EventStream.withCharset(Charsets.UTF_8),
    httpStatusCode: Int = 200,
    httpStatus: HttpStatusCode = HttpStatusCode.fromValue(httpStatusCode),
    headers: (ResponseHeaders.() -> Unit)? = null,
    headerList: List<Pair<String, String>> = emptyList<Pair<String, String>>(),
    delay: Duration,
) : AbstractResponseDefinition<T>(
        contentType = contentType,
        httpStatusCode = httpStatusCode,
        httpStatus = httpStatus,
        headers = headers,
        headerList = headerList,
        delay = delay,
    ) {
    internal suspend fun writeChunksFromFlow(
        writer: ByteWriteChannel,
        verbose: Boolean,
    ) {
        if (this.delay.isPositive()) {
            delay(delay)
        }
        chunkFlow
            ?.filterNotNull()
            ?.cancellable()
            ?.buffer(
                capacity = SEND_BUFFER_CAPACITY,
                onBufferOverflow = BufferOverflow.SUSPEND,
            )?.catch { print("Error while sending chunks: $it") }
            ?.collect {
                writeChunk(writer, it, verbose)
            }
    }

    private suspend fun writeChunk(
        writer: ByteWriteChannel,
        value: T,
        verbose: Boolean,
        serialize: (T) -> String = { "$it" },
    ) {
        if (verbose) {
            print("$value")
        }
        writer.writeStringUtf8(serialize(value))
        writer.flush()
        yield()
        if (delayBetweenChunks.isPositive()) {
            delay(delayBetweenChunks)
        }
    }

    @Suppress("unused")
    internal suspend fun writeChunksFromFlow(session: ServerSSESession) {
        if (this.delay.isPositive()) {
            delay(delay)
        }
        chunkFlow
            ?.filterNotNull()
            ?.cancellable()
            ?.buffer(
                capacity = SEND_BUFFER_CAPACITY,
                onBufferOverflow = BufferOverflow.SUSPEND,
            )?.catch { print("Error while sending chunks: $it") }
            ?.collect {
                val chunk = "$it"
                session.send(
                    data = chunk,
                )
                yield()
                if (delayBetweenChunks.isPositive()) {
                    delay(delayBetweenChunks)
                }
            }
    }

    internal suspend fun writeChunksFromList(
        writer: ByteWriteChannel,
        verbose: Boolean,
    ) {
        if (this.delay.isPositive()) {
            delay(delay)
        }
        chunks?.forEach {
            writeChunk(writer, it, verbose)
        }
    }

    override suspend fun writeResponse(
        call: ApplicationCall,
        verbose: Boolean,
    ) {
        when {
            chunkFlow != null -> {
                call.response.cacheControl(CacheControl.NoCache(null))
                call.respondBytesWriter(
                    status = this.httpStatus,
                    contentType = this.contentType,
                ) {
                    writeChunksFromFlow(writer = this, verbose)
                }
            }

            else -> {
                call.response.cacheControl(CacheControl.NoCache(null))
                call.respondBytesWriter(
                    status = this.httpStatus,
                    contentType = this.contentType,
                ) {
                    writeChunksFromList(this, verbose)
                }
            }
        }
    }
}
