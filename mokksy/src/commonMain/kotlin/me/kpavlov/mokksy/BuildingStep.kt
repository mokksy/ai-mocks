package me.kpavlov.mokksy

import io.ktor.sse.ServerSentEventMetadata
import me.kpavlov.mokksy.request.RequestSpecification
import me.kpavlov.mokksy.response.ResponseDefinitionBuilder
import me.kpavlov.mokksy.response.StreamingResponseDefinitionBuilder

/**
 * Defines the building step for associating an inbound request specification with its corresponding
 * response definition.
 * This class is part of a fluent API used to define mappings between request specifications
 * and their respective responses.
 *
 * @param P The type of the request payload.
 * @param name An optional name assigned to the Stub for identification or debugging purposes.
 * @property registerStub Callback function to be called to register new [Stub] to [MokksyServer]
 * @property requestSpecification The request specification currently being processed.
 */
public class BuildingStep<P> internal constructor(
    private val name: String? = null,
    private val requestSpecification: RequestSpecification<P>,
    private val registerStub: (Stub<*, *>) -> Unit,
) {
    /**
     * Associates the current request specification with a response definition.
     * This method is part of a fluent API for defining mappings between requests and responses.
     *
     * @param T The type of the response body.
     * @param block A lambda function applied to a [me.kpavlov.mokksy.response.ResponseDefinitionBuilder],
     * used to configure the response definition.
     */
    public infix fun <T : Any> respondsWith(block: ResponseDefinitionBuilder<P, T>.() -> Unit) {
        val stub =
            Stub<P, T>(
                name = name,
                requestSpecification = requestSpecification,
            ) { call ->
                val req = CapturedRequest<P>(call.request)
                ResponseDefinitionBuilder<P, T>(request = req)
                    .apply(block)
                    .build()
            }
        registerStub(stub)
    }

    /**
     * Associates the current request specification with a streaming response definition.
     * This method is part of a fluent API for defining mappings between requests and streaming responses.
     *
     * @param T The type of the elements in the streaming response data.
     * @param block A lambda function applied to a [me.kpavlov.mokksy.response.StreamingResponseDefinitionBuilder],
     * used to configure the streaming response definition.
     */
    public infix fun <T> respondsWithStream(
        block: StreamingResponseDefinitionBuilder<P, T>.() -> Unit,
    ) {
        val stub =
            Stub<P, T>(
                name = name,
                requestSpecification = requestSpecification,
            ) { call ->
                val req = CapturedRequest<P>(call.request)
                StreamingResponseDefinitionBuilder<P, T>()
                    .apply(block)
                    .build()
            }

        registerStub(stub)
    }

    /**
     * Associates the current request specification with a server-sent events (SSE) streaming response definition.
     * This method is part of a fluent API for defining mappings between requests and SSE streaming responses.
     *
     * @param T The type of `data` field in the [ServerSentEventMetadata].
     * @param block A lambda function applied to a [StreamingResponseDefinitionBuilder] specifically for
     * configuring the response as a stream of server-sent events.
     */
    public infix fun <T : Any> respondsWithSseStream(
        block: StreamingResponseDefinitionBuilder<P, ServerSentEventMetadata<T>>.() -> Unit,
    ): Unit =
        respondsWithStream<ServerSentEventMetadata<T>>(
            block,
        )
}
