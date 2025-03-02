package me.kpavlov.mokksy.response

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.log
import io.ktor.server.response.ResponseHeaders
import io.ktor.server.response.respond
import io.ktor.server.response.respondNullable
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.delay
import kotlin.reflect.KClass
import kotlin.time.Duration

/**
 * Represents a concrete implementation of an HTTP response definition with a specific response body.
 * This class builds on the `AbstractResponseDefinition` to provide additional configuration and behavior.
 *
 * @param P The type of the request body.
 * @param T The type of the response body.
 * @property contentType The MIME type of the response content with a default to [ContentType.Application.Json].
 * @property body The body of the response, which can be null.
 * @property httpStatus The HTTP status code of the response, defaulting to [HttpStatusCode.OK].
 * @property headers A lambda function for configuring additional response headers using [ResponseHeaders].
 * Defaults to null.
 * @property headerList A list of additional header key-value pairs. Defaults to an empty list.
 * @property delay Delay before response is sent. Default value is zero.
 */
@Suppress("LongParameterList")
public open class ResponseDefinition<P, T : Any>(
    contentType: ContentType = ContentType.Application.Json,
    responseType: KClass<T>? = null,
    public val body: T? = null,
    httpStatus: HttpStatusCode = HttpStatusCode.OK,
    headers: (ResponseHeaders.() -> Unit)? = null,
    headerList: List<Pair<String, String>> = emptyList<Pair<String, String>>(),
    delay: Duration,
) : AbstractResponseDefinition<T>(
        contentType,
        responseType,
        httpStatus,
        headers,
        headerList,
        delay,
    ) {
    override suspend fun writeResponse(
        call: ApplicationCall,
        verbose: Boolean,
    ) {
        if (this.delay.isPositive()) {
            delay(delay)
        }
        contentType?.let {
            call.response.headers.append(
                HttpHeaders.ContentType,
                it.toString(),
            )
        }
        if (body == null) {
            if (verbose) {
                call.application.log.debug("Sending {} with empty response", httpStatus)
            }
            call.respondNullable(
                status = httpStatus,
                message = body,
            )
        } else {
            // detect body type
            val type = responseType ?: body::class as KClass<T>
            if (verbose) {
                call.application.log.debug(
                    "Sending {} with ({}): {}",
                    httpStatus,
                    type,
                    body,
                )
            }
            call.respond(
                status = httpStatus,
                message = body,
                messageType = TypeInfo(type),
            )
        }
    }
}
