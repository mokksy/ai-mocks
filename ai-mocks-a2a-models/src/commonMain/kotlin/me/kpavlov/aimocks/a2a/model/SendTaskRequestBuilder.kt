package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder class for creating [SendTaskRequest] instances.
 *
 * This builder provides a fluent API for creating SendTaskRequest objects,
 * making it easier to configure send task requests.
 *
 * Example usage:
 * ```kotlin
 * val request = SendTaskRequestBuilder()
 *     .id("request-123")
 *     .params {
 *         id = "task-123"
 *         message {
 *             role = Message.Role.user
 *             textPart("Hello, how can I help you?")
 *         }
 *     }
 *     .create()
 * ```
 */
public class SendTaskRequestBuilder {
    public var id: String? = null
    public var params: TaskSendParams? = null

    /**
     * Sets the ID of the request.
     *
     * @param id The ID of the request.
     * @return This builder instance for method chaining.
     */
    public fun id(id: String): SendTaskRequestBuilder =
        apply {
            this.id = id
        }

    /**
     * Configures the task send params using a lambda with receiver.
     *
     * @param init The lambda to configure the task send params.
     * @return This builder instance for method chaining.
     */
    public fun params(init: TaskSendParamsBuilder.() -> Unit): SendTaskRequestBuilder =
        apply {
            this.params = TaskSendParams.create(init)
        }

    /**
     * Configures the task send params using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task send params.
     * @return This builder instance for method chaining.
     */
    public fun params(init: Consumer<TaskSendParamsBuilder>): SendTaskRequestBuilder =
        apply {
            val builder = TaskSendParamsBuilder()
            init.accept(builder)
            this.params = builder.build()
        }

    /**
     * Builds a [SendTaskRequest] instance with the configured parameters.
     *
     * @return A new [SendTaskRequest] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): SendTaskRequest {
        requireNotNull(params) { "Params are required" }

        return SendTaskRequest(
            id = id,
            params = params!!,
        )
    }
}

/**
 * Top-level DSL function for creating [SendTaskRequest].
 *
 * @param init The lambda to configure the send task request.
 * @return A new [SendTaskRequest] instance.
 */
public inline fun sendTaskRequest(init: SendTaskRequestBuilder.() -> Unit): SendTaskRequest =
    SendTaskRequestBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [SendTaskRequest].
 *
 * @param init The consumer to configure the send task request.
 * @return A new [SendTaskRequest] instance.
 */
public fun sendTaskRequest(init: Consumer<SendTaskRequestBuilder>): SendTaskRequest {
    val builder = SendTaskRequestBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new instance of a SendTaskRequest using the provided configuration block.
 *
 * @param block A configuration block for building a SendTaskRequest instance using the SendTaskRequestBuilder.
 * @return A newly created SendTaskRequest instance.
 */
public fun SendTaskRequest.Companion.create(
    block: SendTaskRequestBuilder.() -> Unit,
): SendTaskRequest = SendTaskRequestBuilder().apply(block).build()

/**
 * Creates a new instance of a SendTaskRequest using the provided Java-friendly Consumer.
 *
 * @param block A consumer for building a SendTaskRequest instance using the SendTaskRequestBuilder.
 * @return A newly created SendTaskRequest instance.
 */
public fun SendTaskRequest.Companion.create(
    block: Consumer<SendTaskRequestBuilder>,
): SendTaskRequest {
    val builder = SendTaskRequestBuilder()
    block.accept(builder)
    return builder.build()
}
