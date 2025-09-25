package me.kpavlov.mokksy.response

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.ResponseHeaders
import kotlin.time.Duration

internal typealias ResponseDefinitionSupplier<T> = (
    ApplicationCall,
) -> AbstractResponseDefinition<T>
/**
 * Represents the base definition of an HTTP response in a mapping between a request and its corresponding response.
 * Provides the required attributes and behavior for configuring HTTP responses, including status code, headers,
 * and content type. This class serves as the foundation for more specialized response definitions.
 *
 * @param T The type of the response data.
 * @property contentType The MIME type of the response content.
 * @property httpStatusCode The HTTP status code of the response as Int, defaulting to 200.
 * @property httpStatus The HTTP status code of the response. Defaults to [HttpStatusCode.OK].
 * @property headers A lambda function for configuring the response headers. Defaults to `null`.
 * @property headerList A list of header key-value pairs to populate the response headers. Defaults to an empty list.
 */
public abstract class AbstractResponseDefinition<T>(
    public val contentType: ContentType,
    public val httpStatusCode: Int = 200,
    public val httpStatus: HttpStatusCode = HttpStatusCode.fromValue(httpStatusCode),
    public val headers: (ResponseHeaders.() -> Unit)? = null,
    public val headerList: List<Pair<String, String>> = emptyList(),
    public open val delay: Duration = Duration.ZERO,
    public var responseBody: T? = null,
) {
    internal abstract suspend fun writeResponse(
        call: ApplicationCall,
        verbose: Boolean,
    )

    /**
     * Modifies the response body of this response definition using the provided transformation logic.
     *
     * @param block A lambda function that takes the current response body (or `null`) as an input
     * and returns the modified response body or `null`.
     */
    public fun withResponseBody(block: T?.() -> T?) {
        this.responseBody = block(responseBody)
    }
}
