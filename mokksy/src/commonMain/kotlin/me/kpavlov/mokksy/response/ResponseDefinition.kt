package me.kpavlov.mokksy.response

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.ResponseHeaders
import io.ktor.server.sse.ServerSSESession
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.yield
import java.io.Writer

internal typealias ResponseDefinitionSupplier<P, T> = (
    ApplicationCall,
) -> AbstractResponseDefinition<P, T>

/**
 * Represents the base definition of an HTTP response in a mapping between a request and its corresponding response.
 * Provides the required attributes and behavior for configuring HTTP responses, including status code, headers,
 * and content type. This class serves as the foundation for more specialized response definitions.
 *
 * @param T The type of the response data.
 * @property contentType The MIME type of the response content. Defaults to `null`.
 * @property httpStatus The HTTP status code of the response. Defaults to `HttpStatusCode.OK`.
 * @property headers A lambda function for configuring the response headers. Defaults to `null`.
 * @property headerList A list of header key-value pairs to populate the response headers. Defaults to an empty list.
 */
public abstract class AbstractResponseDefinition<P, T>(
    public val contentType: ContentType? = null,
    public val httpStatus: HttpStatusCode = HttpStatusCode.OK,
    public val headers: (ResponseHeaders.() -> Unit)? = null,
    public val headerList: List<Pair<String, String>> = emptyList<Pair<String, String>>(),
)

/**
 * Represents a concrete implementation of an HTTP response definition with a specific response body.
 * This class builds on the `AbstractResponseDefinition` to provide additional configuration and behavior.
 *
 * @param P The type of the request body.
 * @param T The type of the response body.
 * @property contentType The MIME type of the response content with a default to `ContentType.Application.Json`.
 * @property body The body of the response, which can be null.
 * @property httpStatus The HTTP status code of the response, defaulting to `HttpStatusCode.OK`.
 * @property headers A lambda function for configuring additional response headers using `ResponseHeaders`.
 * Defaults to null.
 * @property headerList A list of additional header key-value pairs. Defaults to an empty list.
 */
public open class ResponseDefinition<P, T>(
    contentType: ContentType? = ContentType.Application.Json,
    public val body: T? = null,
    httpStatus: HttpStatusCode = HttpStatusCode.OK,
    headers: (ResponseHeaders.() -> Unit)? = null,
    headerList: List<Pair<String, String>> = emptyList<Pair<String, String>>(),
) : AbstractResponseDefinition<P, T>(
        contentType,
        httpStatus,
        headers,
        headerList,
    )

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
 * @property delayBetweenChunksMs Delay in milliseconds between the transmission of each chunk.
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
    public val delayBetweenChunksMs: Long = 0L,
    contentType: ContentType = ContentType.Text.EventStream.withCharset(Charsets.UTF_8),
    httpStatus: HttpStatusCode = HttpStatusCode.OK,
    headers: (ResponseHeaders.() -> Unit)? = null,
    headerList: List<Pair<String, String>> = emptyList<Pair<String, String>>(),
) : AbstractResponseDefinition<P, T>(
        contentType,
        httpStatus,
        headers,
        headerList,
    ) {
    internal suspend fun writeChunksFromFlow(
        writer: Writer,
        verbose: Boolean,
    ) {
        chunkFlow
            ?.filterNotNull()
            ?.collect {
                writeChunk(writer, it, verbose)
            }
    }

    private suspend fun writeChunk(
        writer: Writer,
        value: T,
        verbose: Boolean,
    ) {
        if (verbose) {
            print("$value")
        }
        writer.write("$value")
        writer.flush()
        yield()
        if (delayBetweenChunksMs > 0) {
            delay(delayBetweenChunksMs)
        }
    }

    @Suppress("unused")
    internal suspend fun writeChunksFromFlow(session: ServerSSESession) {
        chunkFlow
            ?.filterNotNull()
            ?.collect {
                val chunk = "$it"
                session.send(
                    data = chunk,
                )
                yield()
                if (delayBetweenChunksMs > 0) {
                    delay(delayBetweenChunksMs)
                }
            }
    }

    internal suspend fun writeChunksFromList(
        writer: Writer,
        verbose: Boolean,
    ) {
        chunks?.forEach {
            writeChunk(writer, it, verbose)
        }
    }
}

public open class SseStreamResponseDefinition<P>(
    override val chunkFlow: Flow<ServerSentEvent>? = null,
) : StreamResponseDefinition<P, ServerSentEvent>()
