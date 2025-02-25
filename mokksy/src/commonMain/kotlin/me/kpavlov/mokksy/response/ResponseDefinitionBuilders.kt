package me.kpavlov.mokksy.response

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.ResponseHeaders
import kotlinx.coroutines.flow.Flow
import me.kpavlov.mokksy.CapturedRequest
import java.util.Collections

/**
 * Represents a base abstraction for defining the attributes of an HTTP response in the context of
 * request-to-response mappings. This class allows customization of the HTTP status code and headers
 * and provides a mechanism for building concrete response definitions.
 *
 * @param P The type of the request body. This determines the input type for which the response is being defined.
 * @param T The type of the response data, which is returned to the client.
 * @property httpStatus The HTTP status code to be associated with the response.
 * @property headers A mutable list of header key-value pairs to be included in the response.
 */
public abstract class AbstractResponseDefinitionBuilder<P, T>(
    public var httpStatus: HttpStatusCode,
    public val headers: MutableList<Pair<String, String>>,
) {
    /**
     * A lambda function for configuring additional response headers.
     * This function can define custom headers or override existing ones.
     */
    protected var headersLambda: (ResponseHeaders.() -> Unit)? = null

    /**
     * Configures additional headers for the response using the specified lambda block.
     *
     * @param block A lambda function applied to the [ResponseHeaders] object to configure headers.
     */
    public fun headers(block: ResponseHeaders.() -> Unit) {
        this.headersLambda = block
    }

    /**
     * Abstract method to build a concrete response definition.
     *
     * @return An instance of [AbstractResponseDefinition].
     */
    protected abstract fun build(): AbstractResponseDefinition<P, T>
}

/**
 * Builder for constructing a definition of an HTTP response with configurable attributes.
 *
 * @param P The type of the request body.
 * @param T The type of the response body.
 * @property contentType Optional MIME type of the response.
 * Defaults to `ContentType.Application.Json` if not specified.
 * @property body The body of the response. Can be null.
 * @param httpStatus The HTTP status code of the response. Defaults to `HttpStatusCode.OK`.
 * @param headers A mutable list of additional custom headers for the response.
 *
 * Inherits functionality from [AbstractResponseDefinitionBuilder] to allow additional header manipulations
 * and provides a concrete implementation of the response building process.
 */
public open class ResponseDefinitionBuilder<P : Any, T : Any>(
    public val request: CapturedRequest<P>,
    public var contentType: ContentType? = null,
    public var body: T? = null,
    httpStatus: HttpStatusCode = HttpStatusCode.OK,
    headers: MutableList<Pair<String, String>> = mutableListOf(),
) : AbstractResponseDefinitionBuilder<P, T>(httpStatus = httpStatus, headers = headers) {
    public override fun build(): ResponseDefinition<P, T> =
        ResponseDefinition<P, T>(
            body = body,
            contentType = contentType ?: ContentType.Application.Json,
            httpStatus = httpStatus,
            headers = headersLambda,
            headerList = Collections.unmodifiableList(headers),
        )
}

/**
 * A builder for constructing streaming response definitions.
 *
 * This class is responsible for building instances of `StreamResponseDefinition`,
 * which define responses capable of streaming data either as chunks or via a flow.
 *
 * @param P The type of the request body.
 * @param T The type of data being streamed.
 * @property flow A [Flow] representing streaming data content.
 * @property chunks A list of data chunks to be sent as part of the stream,
 *          if [flow] is not provided.
 */
public open class StreamingResponseDefinitionBuilder<P : Any, T>(
    public val request: CapturedRequest<P>,
    public var flow: Flow<T>? = null,
    public var chunks: MutableList<T> = mutableListOf(),
    httpStatus: HttpStatusCode = HttpStatusCode.OK,
    headers: MutableList<Pair<String, String>> = mutableListOf(),
) : AbstractResponseDefinitionBuilder<P, T>(httpStatus = httpStatus, headers = headers) {
    /**
     * Builds an instance of `StreamResponseDefinition`.
     *
     * This method finalizes the construction of a `StreamResponseDefinition` by encapsulating
     * the data flow, chunked list, HTTP status code, and headers defined in the current instance
     * of the builder. The resulting `StreamResponseDefinition` can then be used to represent
     * a streaming response.
     *
     * @param P The type of the request body.
     * @param T The type of data being streamed.
     * @return A fully constructed `StreamResponseDefinition` instance containing the configured response details.
     */
    public override fun build(): StreamResponseDefinition<P, T> =
        StreamResponseDefinition<P, T>(
            chunkFlow = flow,
            chunks = chunks.toList(),
            httpStatus = httpStatus,
            headers = headersLambda,
            headerList = Collections.unmodifiableList(headers),
        )
}
