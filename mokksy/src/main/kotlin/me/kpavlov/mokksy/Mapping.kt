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
) : Comparable<Mapping<*>> {
    override fun compareTo(other: Mapping<*>): Int = MappingComparator.compare(this, other)

    private val matchCount = AtomicInteger(0)

    suspend fun respond(call: ApplicationCall) {
        call.response.headers.let {
            responseDefinition.headers?.invoke(it)
            it.apply {
                responseDefinition.headerList.forEach { (name, value) -> this.append(name, value) }
            }
        }
        call.response.status(responseDefinition.httpStatus)

        when (responseDefinition) {
            is SseStreamResponseDefinition -> {
                respondWithSseStream(responseDefinition, call)
            }

            is StreamResponseDefinition -> {
                respondWithStream(responseDefinition, call)
            }

            is ResponseDefinition<T> -> {
                call.respond(
                    status = responseDefinition.httpStatus,
                    message = responseDefinition.body as Any,
                )
            }
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

/**
 * Comparator implementation for `Mapping` objects.
 *
 * This comparator is used to compare `Mapping` instances based on the priority
 * defined in their `requestSpecification`. Higher priority values are considered greater.
 *
 * Used internally for sorting or ordering `Mapping` objects when multiple mappings need
 * to be evaluated or prioritized.
 */
internal object MappingComparator : Comparator<Mapping<*>> {
    override fun compare(
        o1: Mapping<*>,
        o2: Mapping<*>,
    ): Int = o1.requestSpecification.priority.compareTo(o2.requestSpecification.priority)
}
