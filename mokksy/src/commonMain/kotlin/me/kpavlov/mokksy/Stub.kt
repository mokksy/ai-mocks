package me.kpavlov.mokksy

import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import me.kpavlov.mokksy.request.RequestSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import me.kpavlov.mokksy.response.ResponseDefinition
import me.kpavlov.mokksy.response.SseStreamResponseDefinition
import me.kpavlov.mokksy.response.StreamResponseDefinition
import me.kpavlov.mokksy.response.respondWithSseStream
import me.kpavlov.mokksy.response.respondWithStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

private val counter = AtomicLong()

/**
 * Represents a mapping between an inbound request specification and an outbound response definition.
 *
 * This class encapsulates the logic needed to handle HTTP requests and responses, including
 * matching request specifications, sending responses, and handling response data of various types.
 * Resembles the [WireMock Stub](https://wiremock.org/docs/stubbing/) abstraction.
 *
 * @param P The type of the request payload.
 * @param T The type of the response data.
 * @param name An optional name assigned to the Stub for identification or debugging purposes.
 * @property requestSpecification Defines the criteria used to match incoming requests.
 * @property responseDefinition Specifies the response to send for matched requests.
 */
internal data class Stub<P, T>(
    val name: String? = null,
    val requestSpecification: RequestSpecification<P>,
    val responseDefinition: AbstractResponseDefinition<T>,
) : Comparable<Stub<*, *>> {
    /**
     * Represents the order of creation for an instance of the containing class.
     * This property is initialized with an incrementing value to ensure each instance
     * can be distinctly ordered based on the sequence of their creation.
     *
     * Used for by [StubComparator].
     */
    internal val creationOrder = counter.incrementAndGet()

    /**
     * Tracks the number of times a particular stub has been matched with incoming requests.
     * This counter is used to record the match frequency and can be incremented or reset
     * through corresponding methods in the class.
     */
    private val matchCount = AtomicInteger(0)

    /**
     * Compares this Stub instance to another Stub instance for order.
     *
     * The comparison is based primarily on the priority of the request specification.
     * If the priorities are equal, the creation order of the stubs is used as a tiebreaker.
     *
     * @param other The Stub instance to compare with this one.
     * @return A negative integer, zero, or a positive integer if this Stub is less than,
     * equal to, or greater than the specified Stub, respectively.
     */
    override fun compareTo(other: Stub<*, *>): Int = StubComparator.compare(this, other)

    suspend fun respond(
        call: ApplicationCall,
        verbose: Boolean,
    ) {
        call.response.headers.let {
            responseDefinition.headers?.invoke(it)
            it.apply {
                responseDefinition.headerList.forEach { (name, value) -> this.append(name, value) }
            }
        }
        call.response.status(responseDefinition.httpStatus)

        when (responseDefinition) {
            is SseStreamResponseDefinition -> {
                respondWithSseStream(responseDefinition, call, verbose)
            }

            is StreamResponseDefinition -> {
                respondWithStream(responseDefinition, call, verbose)
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

    fun toLogString(): String =
        if (name?.isNotBlank() == true) {
            "Stub('$name')[requestSpec=${requestSpecification.toLogString()}, responseDef=$responseDefinition]"
        } else {
            "Stub[requestSpec=${requestSpecification.toLogString()}, responseDef=$responseDefinition]"
        }
}

/**
 * Comparator implementation for `Stub` objects.
 *
 * This comparator is used to compare `Stub` instances based on the priority
 * defined in their `requestSpecification`.
 * Higher priority values are considered greater.
 *
 * If priorities are equal, then [Stub]s are compared by [Stub.creationOrder].
 *
 * Used internally for sorting or ordering `Stub` objects when multiple mappings need
 * to be evaluated or prioritized.
 */
internal object StubComparator : Comparator<Stub<*, *>> {
    override fun compare(
        o1: Stub<*, *>,
        o2: Stub<*, *>,
    ): Int {
        val result =
            o1.requestSpecification.priority().compareTo(
                o2.requestSpecification.priority(),
            )
        return if (result != 0) {
            result
        } else {
            compareValues(o1.creationOrder, o2.creationOrder)
        }
    }
}
