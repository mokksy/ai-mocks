package dev.mokksy.mokksy

import dev.mokksy.mokksy.request.RequestSpecification
import dev.mokksy.mokksy.response.ResponseDefinitionBuilder
import dev.mokksy.mokksy.response.StreamingResponseDefinitionBuilder
import dev.mokksy.mokksy.utils.logger.HttpFormatter
import io.ktor.server.application.log
import io.ktor.sse.ServerSentEventMetadata
import java.io.IOException
import kotlin.reflect.KClass

/**
 * Defines the building step for associating an inbound [RequestSpecification] with its corresponding
 * response definition.
 * This class is part of a fluent API used to define mappings between request specifications
 * and their respective responses.
 *
 * @param P The type of the request payload.
 * @property requestType The type of the request that this step is processing.
 * @property configuration Configuration options for the stub, such as name and behavior flags.
 * @property requestSpecification [RequestSpecification] of the request criteria that this step handles.
 * @property registerStub A callback for registering the [Stub] with the main server or system.
 * @author Konstantin Pavlov
 */
public class BuildingStep<P : Any> internal constructor(
    private val requestType: KClass<P>,
    private val configuration: StubConfiguration,
    private val requestSpecification: RequestSpecification<P>,
    private val registerStub: (Stub<*, *>) -> Unit,
    private val formatter: HttpFormatter,
) {
    /**
     * @param P The type of the request payload.
     * @param name An optional name assigned to the [Stub] for identification or debugging purposes.
     * @property registerStub Callback function to be called to register new [Stub] to [MokksyServer]
     * @property requestSpecification The [RequestSpecification] currently being processed.
     */
    internal constructor(
        requestType: KClass<P>,
        name: String?,
        requestSpecification: RequestSpecification<P>,
        registerStub: (Stub<*, *>) -> Unit,
        formatter: HttpFormatter,
    ) : this(
        requestType = requestType,
        configuration = StubConfiguration(name),
        requestSpecification = requestSpecification,
        registerStub = registerStub,
        formatter = formatter,
    )

    /**
     * Associates the current [RequestSpecification] with a response definition.
     * This method is part of a fluent API for defining mappings between requests and responses.
     *
     * @param P The type of the request payload.
     * @param T The type of the response body.
     * @param block A lambda function applied to a [ResponseDefinitionBuilder],
     * used to configure the response definition.
     */
    public infix fun <T : Any> respondsWith(block: ResponseDefinitionBuilder<P, T>.() -> Unit) {
        val stub =
            Stub(
                configuration = configuration,
                requestSpecification = requestSpecification,
            ) { call ->
                val req = CapturedRequest(call.request, requestType)
                @SuppressWarnings("TooGenericExceptionCaught")
                try {
                    ResponseDefinitionBuilder<P, T>(
                        request = req,
                        formatter = formatter,
                    ).apply(block)
                        .build()
                } catch (e: Exception) {
                    if (e as? IOException == null) {
                        call.application.log.error("Failed to build response for request: $req", e)
                    }
                    throw e
                }
            }
        registerStub(stub)
    }

    /**
     * Associates the current [RequestSpecification] with a response definition.
     * This method is part of a fluent API for defining mappings between requests and responses.
     *
     * @param T The type of the response body.
     * @param responseType The class of the response type, used to infer type information.
     * @param block A lambda function applied to a [ResponseDefinitionBuilder],
     * used to configure the response definition.
     */
    public fun <T : Any> respondsWith(
        @Suppress("unused") responseType: KClass<T>,
        block: ResponseDefinitionBuilder<P, T>.() -> Unit,
    ) {
        respondsWith(block)
    }

    /**
     * Associates the current [RequestSpecification] with a streaming response definition.
     * This method is part of a fluent API for defining mappings between requests and streaming responses.
     *
     * @param P The type of the request payload.
     * @param T The type of the elements in the streaming response data.
     * @param block A lambda function applied to a [StreamingResponseDefinitionBuilder],
     * used to configure the streaming response definition.
     */
    public infix fun <T : Any> respondsWithStream(
        block: StreamingResponseDefinitionBuilder<P, T>.() -> Unit,
    ) {
        val stub =
            Stub(
                configuration = configuration,
                requestSpecification = requestSpecification,
            ) { call ->
                val req = CapturedRequest(call.request, requestType)
                StreamingResponseDefinitionBuilder<P, T>(
                    request = req,
                    formatter = formatter,
                ).apply(block)
                    .build()
            }

        registerStub(stub)
    }

    /**
     * Associates the current [RequestSpecification] with a streaming response definition.
     * This method is part of a fluent API for defining mappings between requests and streaming responses.
     *
     * @param T The type of the elements in the streaming response data.
     * @param responseType The class of the response type, used to infer type information
     * for the response data.
     * @param block A lambda function applied to a [StreamingResponseDefinitionBuilder],
     * used to configure the streaming response definition.
     */
    public fun <T : Any> respondsWithStream(
        @Suppress("unused") responseType: KClass<T>,
        block: StreamingResponseDefinitionBuilder<P, T>.() -> Unit,
    ) {
        respondsWithStream(block)
    }

    /**
     * Associates the current [RequestSpecification] with a server-sent events (SSE) streaming response definition.
     * This method is part of a fluent API for defining mappings between requests and SSE streaming responses.
     *
     * @param P The type of the request payload.
     * @param T The type of `data` field in the ServerSentEventMetadata.
     * @param block A lambda function applied to a [StreamingResponseDefinitionBuilder] specifically for
     * configuring the response as a stream of server-sent events.
     */
    public infix fun <T : Any> respondsWithSseStream(
        block: StreamingResponseDefinitionBuilder<P, ServerSentEventMetadata<T>>.() -> Unit,
    ): Unit =
        respondsWithStream<ServerSentEventMetadata<T>>(
            block,
        )

    public fun <T : Any> respondsWithSseStream(
        @Suppress("unused") responseType: KClass<T>,
        block: StreamingResponseDefinitionBuilder<P, ServerSentEventMetadata<T>>.() -> Unit,
    ): Unit = respondsWithSseStream(block)
}
