package dev.mokksy.mokksy.response

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.log
import io.ktor.server.request.httpVersion
import io.ktor.server.response.ResponseHeaders
import io.ktor.server.response.respond
import io.ktor.util.cio.ChannelWriteException
import kotlinx.coroutines.delay
import dev.mokksy.mokksy.utils.logger.HttpFormatter
import kotlin.time.Duration

/**
 * Represents a concrete implementation of an HTTP response definition with a specific response body.
 * This class builds on the `AbstractResponseDefinition` to provide additional configuration and behavior.
 *
 * @param P The type of the request body.
 * @param T The type of the response body.
 * @property contentType The MIME type of the response content with a default to [ContentType.Application.Json].
 * @property body The body of the response, which can be null.
 * @property httpStatusCode The HTTP status code of the response as Int, defaulting to 200.
 * @property httpStatus The HTTP status code of the response, defaulting to [HttpStatusCode.OK].
 * @property headers A lambda function for configuring additional response headers using [ResponseHeaders].
 * Defaults to null.
 * @property headerList A list of additional header key-value pairs. Defaults to an empty list.
 * @property delay Delay before the response is sent. The default value is zero.
 */
public open class ResponseDefinition<P, T>(
    contentType: ContentType = ContentType.Application.Json,
    public val body: T? = null,
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
    override suspend fun writeResponse(
        call: ApplicationCall,
        verbose: Boolean,
    ) {
        if (this.delay.isPositive()) {
            delay(delay)
        }
        val effectiveBody = responseBody ?: body
        if (verbose) {
            call.application.log.debug(
                "Sending:\n---\n${
                    formatter.formatResponse(
                        httpVersion = call.request.httpVersion,
                        headers = call.response.headers,
                        contentType = this.contentType,
                        status = httpStatus,
                        body = effectiveBody?.toString(),
                    )
                }---\n",
            )
        }
        try {
            val payload: Any = effectiveBody ?: ""
            call.respond(
                status = httpStatus,
                message = payload,
            )
        } catch (e: ChannelWriteException) {
            // We can't do anything about it
            call.application.log.debug(e.message, e)
        }
    }
}
