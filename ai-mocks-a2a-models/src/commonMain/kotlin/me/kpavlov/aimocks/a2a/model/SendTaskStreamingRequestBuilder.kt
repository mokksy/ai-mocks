package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [SendTaskStreamingRequest].
 *
 * Example usage:
 * ```
 * val request = sendTaskStreamingRequest {
 *     id = myRequestId
 *     params {
 *         id = "task-123"
 *         message {
 *             role = Message.Role.user
 *             textPart("Hello, how can I help you?")
 *         }
 *     }
 * }
 * ```
 */
public class SendTaskStreamingRequestBuilder {
    public var id: RequestId? = null
    public var params: TaskSendParams? = null

    /**
     * Sets the ID of the request.
     *
     * @param id The ID of the request.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): SendTaskStreamingRequestBuilder =
        apply {
            this.id = id
        }

    /**
     * Configures the task send params using a lambda with receiver.
     *
     * @param init The lambda to configure the task send params.
     * @return This builder instance for method chaining.
     */
    public fun params(init: TaskSendParamsBuilder.() -> Unit): SendTaskStreamingRequestBuilder =
        apply {
            params = TaskSendParams.create(init)
        }

    /**
     * Configures the task send params using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task send params.
     * @return This builder instance for method chaining.
     */
    public fun params(init: Consumer<TaskSendParamsBuilder>): SendTaskStreamingRequestBuilder =
        apply {
            val builder = TaskSendParamsBuilder()
            init.accept(builder)
            params = builder.build()
        }

    /**
     * Builds a [SendTaskStreamingRequest] instance with the configured parameters.
     *
     * @return A new [SendTaskStreamingRequest] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): SendTaskStreamingRequest =
        SendTaskStreamingRequest(
            id = id,
            params = requireNotNull(params) { "SendTaskStreamingRequest.params must be provided" },
        )
}

/**
 * Top-level DSL function for creating [SendTaskStreamingRequest].
 *
 * @param init The lambda to configure the send task streaming request.
 * @return A new [SendTaskStreamingRequest] instance.
 */
public inline fun sendTaskStreamingRequest(
    init: SendTaskStreamingRequestBuilder.() -> Unit,
): SendTaskStreamingRequest = SendTaskStreamingRequestBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [SendTaskStreamingRequest].
 *
 * @param init The consumer to configure the send task streaming request.
 * @return A new [SendTaskStreamingRequest] instance.
 */
public fun sendTaskStreamingRequest(
    init: Consumer<SendTaskStreamingRequestBuilder>,
): SendTaskStreamingRequest {
    val builder = SendTaskStreamingRequestBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [SendTaskStreamingRequest.Companion].
 *
 * @param init The lambda to configure the send task streaming request.
 * @return A new [SendTaskStreamingRequest] instance.
 */
public fun SendTaskStreamingRequest.Companion.create(
    init: SendTaskStreamingRequestBuilder.() -> Unit,
): SendTaskStreamingRequest = SendTaskStreamingRequestBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [SendTaskStreamingRequest.Companion].
 *
 * @param init The consumer to configure the send task streaming request.
 * @return A new [SendTaskStreamingRequest] instance.
 */
public fun SendTaskStreamingRequest.Companion.create(
    init: Consumer<SendTaskStreamingRequestBuilder>,
): SendTaskStreamingRequest {
    val builder = SendTaskStreamingRequestBuilder()
    init.accept(builder)
    return builder.build()
}
