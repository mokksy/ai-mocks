package me.kpavlov.aimocks.a2a.model

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
     * Configures the task send params using a DSL.
     *
     * @param init The lambda to configure the task send params.
     */
    public fun params(init: TaskSendParamsBuilder.() -> Unit) {
        this.params = TaskSendParams.build(init)
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
 * Creates a new instance of a SendTaskRequest using the provided configuration block.
 *
 * @param block A configuration block for building a SendTaskRequest instance using the SendTaskRequestBuilder.
 * @return A newly created SendTaskRequest instance.
 */
public fun SendTaskRequest.Companion.create(
    block: SendTaskRequestBuilder.() -> Unit,
): SendTaskRequest = create(block)
