package me.kpavlov.mokksy

import io.ktor.sse.ServerSentEventMetadata
import me.kpavlov.mokksy.request.RequestSpecification
import me.kpavlov.mokksy.response.ResponseDefinitionBuilder
import me.kpavlov.mokksy.response.StreamingResponseDefinitionBuilder
import kotlin.reflect.KClass

/**
 * Defines the building step for associating an inbound request specification with its corresponding
 * response definition.
 * This class is part of a fluent API used to define mappings between request specifications
 * and their respective responses.
 *
 * @param P The type of the request payload.
 * @property requestType The type of the request that this step is processing.
 * @property configuration Configuration options for the stub, such as name and behavior flags.
 * @property requestSpecification Specification of the request criteria that this step handles.
 * @property registerStub A callback for registering the stub with the main server or system.
 */
public class BuildingStep<P : Any> internal constructor(
    private val requestType: KClass<P>,
    private val configuration: StubConfiguration,
    private val requestSpecification: RequestSpecification<P>,
    private val registerStub: (Stub<*, *>) -> Unit,
) {
    /**
     * @param P The type of the request payload.
     * @param name An optional name assigned to the Stub for identification or debugging purposes.
     * @property registerStub Callback function to be called to register new [Stub] to [MokksyServer]
     * @property requestSpecification The request specification currently being processed.
     */
    internal constructor(
        requestType: KClass<P>,
        name: String?,
        requestSpecification: RequestSpecification<P>,
        registerStub: (Stub<*, *>) -> Unit,
    ) : this(
        requestType = requestType,
        configuration = StubConfiguration(name),
        requestSpecification = requestSpecification,
        registerStub = registerStub,
    )

    /**
     * Associates the current request specification with a response definition.
     * This method is part of a fluent API for defining mappings between requests and responses.
     *
     * @param P The type of the request payload.
     * @param T The type of the response body.
     * @param block A lambda function applied to a [me.kpavlov.mokksy.response.ResponseDefinitionBuilder],
     * used to configure the response definition.
     */
    public infix fun <T : Any> respondsWith(block: ResponseDefinitionBuilder<P, T>.() -> Unit) {
        val stub =
            Stub<P, T>(
                configuration = configuration,
                requestSpecification = requestSpecification,
            ) { call ->
                val req = CapturedRequest<P>(call.request, requestType)
                val responseDefinition =
                    ResponseDefinitionBuilder<P, T>(request = req)
                        .apply(block)
                        .build()
                responseDefinition
            }
        registerStub(stub)
    }

    /**
     * Associates the current request specification with a streaming response definition.
     * This method is part of a fluent API for defining mappings between requests and streaming responses.
     *
     * @param P The type of the request payload.
     * @param T The type of the elements in the streaming response data.
     * @param block A lambda function applied to a [me.kpavlov.mokksy.response.StreamingResponseDefinitionBuilder],
     * used to configure the streaming response definition.
     */
    public infix fun <T : Any> respondsWithStream(
        block: StreamingResponseDefinitionBuilder<P, T>.() -> Unit,
    ) {
        val stub =
            Stub<P, T>(
                configuration = configuration,
                requestSpecification = requestSpecification,
            ) { call ->
                val req = CapturedRequest<P>(call.request, requestType)
                StreamingResponseDefinitionBuilder<P, T>(request = req)
                    .apply(block)
                    .build()
            }

        registerStub(stub)
    }

    /**
     * Associates the current request specification with a server-sent events (SSE) streaming response definition.
     * This method is part of a fluent API for defining mappings between requests and SSE streaming responses.
     *
     * @param P The type of the request payload.
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
