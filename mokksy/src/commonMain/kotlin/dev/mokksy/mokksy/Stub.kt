package dev.mokksy.mokksy

import dev.mokksy.mokksy.request.RequestSpecification
import dev.mokksy.mokksy.response.ResponseDefinitionSupplier
import io.ktor.server.application.ApplicationCall
import kotlinx.atomicfu.atomic

/**
 * Represents a mapping between an inbound [RequestSpecification] and an outbound response definition.
 *
 * This class encapsulates the logic needed to handle HTTP requests and responses, including
 * matching request specifications, sending responses, and handling response data of various types.
 * Resembles the [WireMock Stub](https://wiremock.org/docs/stubbing/) abstraction.
 *
 * @param P The type of the request payload.
 * @param T The type of the response data.
 * @param configuration Defines the behavior and attributes of the stub, including its name
 *          and logging verbosity settings.
 * @property requestSpecification Defines the criteria used to match incoming requests.
 * @property responseDefinitionSupplier Supplies the [ResponseDefinitionSupplier] for a matched request.
 *          This includes headers, body, and HTTP status code, which are applied to the HTTP response.
 * @author Konstantin Pavlov
 */
internal data class Stub<P : Any, T : Any>(
    val configuration: StubConfiguration,
    val requestSpecification: RequestSpecification<P>,
    val responseDefinitionSupplier: ResponseDefinitionSupplier<T>,
) : Comparable<Stub<*, *>> {
    private companion object {
        // Multiplatform atomic counter for creation order
        private val COUNTER = atomic(0L)
    }

    /**
     * Represents the order of creation for an instance of the containing class.
     * This property is initialized with an incrementing value to ensure each instance
     * can be distinctly ordered based on the sequence of their creation.
     *
     * Used by [StubComparator].
     */
    internal val creationOrder = COUNTER.incrementAndGet()

    /**
     * Tracks the number of times a particular stub has been matched with incoming requests.
     * This counter is used to record the match frequency and can be incremented or reset
     * through corresponding methods in the class.
     */
    private val matchCount = atomic(0)

    /**
     * Compares this [Stub] instance to another [Stub] instance for order.
     *
     * The comparison is based primarily on the priority of the [requestSpecification].
     * If the priorities are equal, the [creationOrder] of the stubs is used as a tiebreaker.
     *
     * @param other The [Stub] instance to compare with this one.
     * @return A negative integer, zero, or a positive integer if this [Stub] is less than,
     * equal to, or greater than the specified [Stub], respectively.
     */
    override fun compareTo(other: Stub<*, *>): Int = StubComparator.compare(this, other)

    suspend fun respond(
        call: ApplicationCall,
        verbose: Boolean,
    ) {
        val responseDefinition = responseDefinitionSupplier.invoke(call)
        call.response.headers.let {
            responseDefinition.headers?.invoke(it)
            it.apply {
                responseDefinition.headerList.forEach { (name, value) -> this.append(name, value) }
            }
        }
        call.response.status(responseDefinition.httpStatus)

        responseDefinition.writeResponse(call, verbose)
    }

    fun incrementMatchCount() {
        matchCount.incrementAndGet()
    }

    fun resetMatchCount() {
        matchCount.value = 0
    }

    fun matchCount(): Int = matchCount.value

    fun toLogString(): String =
        if (configuration.name?.isNotBlank() == true) {
            "Stub('${configuration.name}')[config=$configuration requestSpec=${requestSpecification.toLogString()}]"
        } else {
            "Stub[config=$configuration requestSpec=${requestSpecification.toLogString()}]"
        }
}

/**
 * Comparator implementation for [Stub] objects.
 *
 * This comparator is used to compare [Stub] instances based on the priority
 * defined in their [RequestSpecification].
 * Higher priority values are considered greater.
 *
 * If priorities are equal, then [Stub]s are compared by [Stub.creationOrder].
 *
 * Used internally for sorting or ordering [Stub] objects when multiple mappings need
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
