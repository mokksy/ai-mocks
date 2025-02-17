package me.kpavlov.mokksy

import io.ktor.sse.ServerSentEvent

/**
 * Defines the building step for associating an inbound request specification with its corresponding
 * response definition.
 * This class is part of a fluent API used to define mappings between request specifications
 * and their respective responses.
 *
 * @param R The type of the request specification.
 * @param name An optional name assigned to the Stub for identification or debugging purposes.
 * @property stubs A mutable collection of mappings that associate request specifications with response definitions.
 * @property requestSpecification The request specification currently being processed.
 */
public open class BuildingStep<R : RequestSpecification> internal constructor(
    private val name: String? = null,
    private val stubs: MutableCollection<Stub<*>>,
    protected val requestSpecification: R,
) {
    /**
     * Associates the current request specification with a response definition.
     * This method is part of a fluent API for defining mappings between requests and responses.
     *
     * @param T The type of the response body.
     * @param block A lambda function applied to a [ResponseDefinitionBuilder],
     * used to configure the response definition.
     */
    public open infix fun <T> respondsWith(block: ResponseDefinitionBuilder<T>.() -> Unit) {
        val responseDefinition = ResponseDefinitionBuilder<T>().apply(block).build()
        val stub =
            Stub(
                name,
                requestSpecification,
                responseDefinition,
            )
        addStub(stub)
    }

    /**
     * Associates the current request specification with a streaming response definition.
     * This method is part of a fluent API for defining mappings between requests and streaming responses.
     *
     * @param T The type of the elements in the streaming response data.
     * @param block A lambda function applied to a [StreamingResponseDefinitionBuilder],
     * used to configure the streaming response definition.
     */
    public open infix fun <T> respondsWithStream(
        block: StreamingResponseDefinitionBuilder<T>.() -> Unit,
    ) {
        val responseDefinition = StreamingResponseDefinitionBuilder<T>().apply(block).build()
        val stub =
            Stub(
                name,
                requestSpecification,
                responseDefinition,
            )
        addStub(stub)
    }

    /**
     * Associates the current request specification with a server-sent events (SSE) streaming response definition.
     * This method is part of a fluent API for defining mappings between requests and SSE streaming responses.
     *
     * @param block A lambda function applied to a [StreamingResponseDefinitionBuilder] specifically for
     * configuring the response as a stream of server-sent events.
     */
    public open infix fun respondsWithSseStream(
        block: StreamingResponseDefinitionBuilder<ServerSentEvent>.() -> Unit,
    ): Unit =
        respondsWithStream<ServerSentEvent>(
            block,
        )

    private fun addStub(stub: Stub<*>) {
        val added = stubs.add(stub)
        assert(added) { "Duplicate stub detected: $stub" }
    }
}
