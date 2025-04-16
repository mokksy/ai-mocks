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
 * @property contentType The MIME type of the response content. Defaults to `null`.
 * @property httpStatus The HTTP status code of the response. Defaults to [HttpStatusCode.OK].
 * @property headers A lambda function for configuring the response headers. Defaults to `null`.
 * @property headerList A list of header key-value pairs to populate the response headers. Defaults to an empty list.
 */
public abstract class AbstractResponseDefinition<T>(
    public val contentType: ContentType? = null,
    public val httpStatus: HttpStatusCode = HttpStatusCode.OK,
    public val headers: (ResponseHeaders.() -> Unit)? = null,
    public val headerList: List<Pair<String, String>> = emptyList<Pair<String, String>>(),
    public open val delay: Duration = Duration.ZERO,
) {
    internal abstract suspend fun writeResponse(
        call: ApplicationCall,
        verbose: Boolean,
    )
}
