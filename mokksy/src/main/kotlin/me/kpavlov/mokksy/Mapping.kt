package me.kpavlov.mokksy

import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import java.util.concurrent.atomic.AtomicInteger

/**
 * Represents a mapping between an inbound request specification and an outbound response definition.
 * This class encapsulates the logic needed to handle HTTP requests and responses, including
 * matching request specifications, sending responses, and handling response data of various types.
 *
 * @param T The type of the response data.
 * @property requestSpecification Defines the criteria used to match incoming requests.
 * @property responseDefinition Specifies the response to send for matched requests.
 */
internal data class Mapping<T>(
    val requestSpecification: RequestSpecification,
    val responseDefinition: AbstractResponseDefinition<T>,
) {
    private val matchCount = AtomicInteger(0)

    suspend fun respond(call: ApplicationCall) {
        call.response.headers.let {
            responseDefinition.headers?.invoke(it)
            it.apply {
                responseDefinition.headerList.forEach { (name, value) -> this.append(name, value) }
            }
        }
        call.response.status(responseDefinition.httpStatus)

        if (responseDefinition is StreamResponseDefinition) {
            respondWithStream(responseDefinition, call)
        } else if (responseDefinition is ResponseDefinition<T>) {
            call.respond(
                status = responseDefinition.httpStatus,
                message = responseDefinition.body as Any,
            )
        }
    }

    fun incrementMatchCount() {
        matchCount.incrementAndGet()
    }

    fun resetMatchCount() {
        matchCount.set(0)
    }

    fun matchCount(): Int = matchCount.toInt()
}
