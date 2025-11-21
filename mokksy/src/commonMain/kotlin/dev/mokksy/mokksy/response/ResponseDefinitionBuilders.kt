package dev.mokksy.mokksy.response

import dev.mokksy.mokksy.CapturedRequest
import dev.mokksy.mokksy.utils.logger.HttpFormatter
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.ResponseHeaders
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Represents a base abstraction for defining the attributes of an HTTP response in the context of
 * request-to-response mappings. This class allows customization of the HTTP status code and headers
 * and provides a mechanism for building concrete response definitions.
 *
 * @param P The type of the request body. This determines the input type for which the response is being defined.
 * @param T The type of the response data, which is returned to the client.
 * @property httpStatus The HTTP status code to be associated with the response.
 * @property headers A mutable list of header key-value pairs to be included in the response.
 * @author Konstantin Pavlov
 */
public abstract class AbstractResponseDefinitionBuilder<P, T>(
    public var httpStatusCode: Int = 200,
    public var httpStatus: HttpStatusCode = HttpStatusCode.fromValue(httpStatusCode),
    public val headers: MutableList<Pair<String, String>>,
    public var delay: Duration = Duration.ZERO,
) {
    /**
     * A lambda function for configuring additional response headers.
     * This function can define custom headers or override existing ones.
     */
    protected var headersLambda: (ResponseHeaders.() -> Unit)? = null

    /**
     * Configures additional headers for the response using the specified lambda block.
     *
     * @param block A lambda function applied to the ResponseHeaders object to configure headers.
     */
    public fun headers(block: ResponseHeaders.() -> Unit) {
        this.headersLambda = block
    }

    /**
     * Sets a delay for the response in milliseconds.
     *
     * @param millis The delay duration in milliseconds before the response is sent.
     */
    public fun delayMillis(millis: Long) {
        this.delay = millis.milliseconds
    }

    public fun httpStatus(status: Int) {
        this.httpStatusCode = status
        this.httpStatus = HttpStatusCode.fromValue(status)
    }

    /**
     * Abstract method to build a concrete response definition.
     *
     * @return An instance of [AbstractResponseDefinition].
     */
    protected abstract fun build(): AbstractResponseDefinition<T>
}

/**
 * Builder for constructing a definition of an HTTP response with configurable attributes.
 *
 * @param P The type of the request body.
 * @param T The type of the response body.
 * @property request The [CapturedRequest] being processed.
 * @property contentType Optional MIME type of the response. Defaults to `null` if not specified.
 * @property body The body of the response. Can be null.
 * @property httpStatusCode The HTTP status code of the response as Int, defaulting to 200.
 * @property httpStatus The HTTP status code of the response, defaulting to HttpStatusCode.OK.
 * @param headers A mutable list of additional custom headers for the response.
 *
 * Inherits functionality from [AbstractResponseDefinitionBuilder] to allow additional header manipulations
 * and provides a concrete implementation of the response building process.
 */
public open class ResponseDefinitionBuilder<P : Any, T : Any>(
    public val request: CapturedRequest<P>,
    public var contentType: ContentType? = null,
    public var body: T? = null,
    httpStatusCode: Int = 200,
    httpStatus: HttpStatusCode = HttpStatusCode.fromValue(httpStatusCode),
    headers: MutableList<Pair<String, String>> = mutableListOf(),
    private val formatter: HttpFormatter,
) : AbstractResponseDefinitionBuilder<P, T>(
        httpStatusCode = httpStatusCode,
        httpStatus = httpStatus,
        headers = headers,
    ) {
    public override fun build(): ResponseDefinition<P, T> =
        ResponseDefinition(
            body = body,
            contentType = contentType ?: ContentType.Application.Json,
            httpStatusCode = httpStatusCode,
            httpStatus = httpStatus,
            headers = headersLambda,
            headerList = headers.toList(),
            delay = delay,
            formatter = formatter,
        )
}

/**
 * A builder for constructing streaming response definitions.
 *
 * This class is responsible for building instances of [StreamResponseDefinition],
 * which define responses capable of streaming data either as chunks or via a flow.
 *
 * @param P The type of the request body.
 * @param T The type of data being streamed.
 * @property request The [CapturedRequest] being processed.
 * @property flow A Flow representing streaming data content.
 * @property chunks A list of data chunks to be sent as part of the stream,
 *          if [flow] is not provided.
 */
@Suppress("LongParameterList")
public open class StreamingResponseDefinitionBuilder<P : Any, T>(
    public val request: CapturedRequest<P>,
    public var flow: Flow<T>? = null,
    public var chunks: MutableList<T> = mutableListOf(),
    public var delayBetweenChunks: Duration = Duration.ZERO,
    httpStatus: HttpStatusCode = HttpStatusCode.OK,
    headers: MutableList<Pair<String, String>> = mutableListOf(),
    public val chunkContentType: ContentType? = null,
    private val formatter: HttpFormatter,
) : AbstractResponseDefinitionBuilder<P, T>(
        httpStatus = httpStatus,
        headers = headers,
    ) {
    /**
     * Builds an instance of [StreamResponseDefinition].
     *
     * This method finalizes the construction of a [StreamResponseDefinition] by encapsulating
     * the data flow, chunked list, HTTP status code, and headers defined in the current instance
     * of the builder. The resulting [StreamResponseDefinition] can then be used to represent
     * a streaming response.
     *
     * @param P The type of the request body.
     * @param T The type of data being streamed.
     * @return A fully constructed [StreamResponseDefinition] instance containing the configured response details.
     */
    public override fun build(): StreamResponseDefinition<P, T> =
        StreamResponseDefinition(
            chunkFlow = flow,
            chunks = chunks.toList(),
            httpStatus = httpStatus,
            headers = headersLambda,
            headerList = headers.toList(),
            delayBetweenChunks = delayBetweenChunks,
            delay = delay,
            formatter = formatter,
            chunkContentType = chunkContentType,
        )
}
