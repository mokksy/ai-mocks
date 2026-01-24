package dev.mokksy.mokksy.response

import dev.mokksy.mokksy.utils.logger.HttpFormatter
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.log
import io.ktor.server.request.httpVersion
import io.ktor.server.response.ResponseHeaders
import io.ktor.server.response.cacheControl
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.sse.ServerSSESession
import io.ktor.util.logging.Logger
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.charsets.Charsets
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
 * @property chunkFlow A Flow of chunks to be streamed as part of the response.
 * @property chunks A list of chunks representing the response data to be sent.
 * @property delayBetweenChunks Delay between the transmission of each chunk.
 * @property httpStatusCode The HTTP status code of the response as Int, defaulting to 200.
 * @property httpStatus The HTTP status code of the response, defaulting to HttpStatusCode.OK.
 * @constructor Initializes a streaming response definition with the specified flow, chunk list, content type,
 *              HTTP status code, and headers.
 *
 * @see AbstractResponseDefinition
 *
 * @author Konstantin Pavlov
 */
@Suppress("LongParameterList")
public open class StreamResponseDefinition<P, T>(
    public open val chunkFlow: Flow<T>? = null,
    public val chunks: List<T>? = null,
    public val delayBetweenChunks: Duration = Duration.ZERO,
    contentType: ContentType = ContentType.Text.EventStream.withCharset(Charsets.UTF_8),
    private val chunkContentType: ContentType? = null,
    httpStatusCode: Int = 200,
    httpStatus: HttpStatusCode = HttpStatusCode.fromValue(httpStatusCode),
    headers: (ResponseHeaders.() -> Unit)? = null,
    headerList: List<Pair<String, String>> = emptyList<Pair<String, String>>(),
    delay: Duration,
    private val formatter: HttpFormatter,
) : AbstractResponseDefinition<T>(
        contentType = contentType,
        httpStatusCode = httpStatusCode,
        httpStatus = httpStatus,
        headers = headers,
        headerList = headerList,
        delay = delay,
    ) {
    internal suspend fun writeChunksFromFlow(
        logger: Logger,
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
            )?.catch { logger.warn("Error while sending chunks: $it") }
            ?.collect {
                writeChunk(
                    writer = writer,
                    value = it,
                    verbose = verbose,
                    logger = logger,
                    chunkContentTypeOverride = chunkContentType,
                )
            }
    }

    private suspend fun writeChunk(
        writer: ByteWriteChannel,
        value: T,
        verbose: Boolean,
        logger: Logger,
        chunkContentTypeOverride: ContentType? = null,
        serialize: (T) -> String = { "$it" },
    ) {
        val serializedValue = serialize(value)
        if (verbose) {
            val type =
                chunkContentTypeOverride
                    ?: chunkContentType
                    ?: when (value) {
                        is CharSequence -> ContentType.Text.Plain
                        else -> ContentType.Application.Json
                    }
            logger.debug(
                "Writing chunk:\n ${
                    formatter.formatResponseChunk(
                        chunk = serializedValue,
                        contentType = type,
                    )
                }",
            )
        }
        writer.writeStringUtf8(serializedValue)
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
        logger: Logger,
        chunkContentType: ContentType?,
    ) {
        if (this.delay.isPositive()) {
            delay(delay)
        }
        chunks?.forEach {
            writeChunk(
                writer = writer,
                value = it,
                verbose = verbose,
                logger = logger,
                chunkContentTypeOverride = chunkContentType,
            )
        }
    }

    override suspend fun writeResponse(
        call: ApplicationCall,
        verbose: Boolean,
    ) {
        // Apply configured headers
        headers?.invoke(call.response.headers)
        for ((name, value) in headerList) {
            call.response.headers.append(name, value)
        }
        when {
            chunkFlow != null -> {
                call.response.cacheControl(CacheControl.NoCache(null))
                call.respondBytesWriter(
                    status = this.httpStatus,
                    contentType = this.contentType,
                ) {
                    if (verbose) {
                        call.application.log.debug(
                            "Sending:\n---\n${
                                formatter.formatResponseHeader(
                                    httpVersion = call.request.httpVersion,
                                    headers = call.response.headers,
                                    status = httpStatus,
                                )
                            }",
                        )
                    }
                    writeChunksFromFlow(
                        writer = this,
                        verbose = verbose,
                        logger = call.application.log,
                    )
                }
            }

            else -> {
                call.response.cacheControl(CacheControl.NoCache(null))
                call.respondBytesWriter(
                    status = this.httpStatus,
                    contentType = this.contentType,
                ) {
                    writeChunksFromList(
                        writer = this,
                        verbose = verbose,
                        logger = call.application.log,
                        chunkContentType = chunkContentType,
                    )
                }
            }
        }
    }
}
