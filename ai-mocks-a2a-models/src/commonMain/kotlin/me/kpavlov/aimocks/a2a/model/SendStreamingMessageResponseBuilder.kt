/*
 * SendStreamingMessageResponseBuilder.kt
 *
 * Builder for SendStreamingMessageResponse according to the A2A protocol.
 * See: https://a2a-protocol.org/latest/specification/
 */
package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder class for creating [SendStreamingMessageResponse] instances.
 *
 * This builder provides a fluent API for creating SendStreamingMessageResponse objects,
 * making it easier to configure streaming message responses according to the A2A protocol.
 *
 * The result field accepts polymorphic TaskUpdateEvent instances (TaskStatusUpdateEvent or TaskArtifactUpdateEvent).
 *
 * Example usage:
 * ```
 * val response = sendStreamingMessageResponse {
 *     id = "request-789"
 *     taskStatusUpdate {
 *         id("task-streaming-123")
 *         status {
 *             state = TaskState.working
 *             timestamp = System.currentTimeMillis()
 *         }
 *         final = false
 *     }
 * }
 * ```
 *
 * @see [A2A Protocol - Streaming Messages](https://a2a-protocol.org/latest/specification/)
 */
public class SendStreamingMessageResponseBuilder {
    public var id: String? = null
    public var result: TaskUpdateEvent? = null
    public var error: JSONRPCError? = null

    /**
     * Sets the ID of the response.
     *
     * @param id The ID of the response.
     * @return This builder instance for method chaining.
     */
    public fun id(id: String): SendStreamingMessageResponseBuilder =
        apply {
            this.id = id
        }

    /**
     * Sets the result task update event.
     *
     * @param result The task update event result.
     * @return This builder instance for method chaining.
     */
    public fun result(result: TaskUpdateEvent): SendStreamingMessageResponseBuilder =
        apply {
            this.result = result
        }

    /**
     * Configures a task status update event using a lambda with receiver.
     *
     * @param init The lambda to configure the task status update event.
     * @return This builder instance for method chaining.
     */
    public fun taskStatusUpdate(
        init: TaskStatusUpdateEventBuilder.() -> Unit,
    ): SendStreamingMessageResponseBuilder =
        apply {
            this.result = TaskStatusUpdateEvent.create(init)
        }

    /**
     * Configures a task status update event using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task status update event.
     * @return This builder instance for method chaining.
     */
    public fun taskStatusUpdate(
        init: Consumer<TaskStatusUpdateEventBuilder>,
    ): SendStreamingMessageResponseBuilder =
        apply {
            val builder = TaskStatusUpdateEventBuilder()
            init.accept(builder)
            this.result = builder.build()
        }

    /**
     * Configures a task artifact update event using a lambda with receiver.
     *
     * @param init The lambda to configure the task artifact update event.
     * @return This builder instance for method chaining.
     */
    public fun taskArtifactUpdate(
        init: TaskArtifactUpdateEventBuilder.() -> Unit,
    ): SendStreamingMessageResponseBuilder =
        apply {
            this.result = TaskArtifactUpdateEvent.create(init)
        }

    /**
     * Configures a task artifact update event using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task artifact update event.
     * @return This builder instance for method chaining.
     */
    public fun taskArtifactUpdate(
        init: Consumer<TaskArtifactUpdateEventBuilder>,
    ): SendStreamingMessageResponseBuilder =
        apply {
            val builder = TaskArtifactUpdateEventBuilder()
            init.accept(builder)
            this.result = builder.build()
        }

    /**
     * Sets the error for the response.
     *
     * @param error The JSON-RPC error.
     * @return This builder instance for method chaining.
     */
    public fun error(error: JSONRPCError): SendStreamingMessageResponseBuilder =
        apply {
            this.error = error
        }

    /**
     * Builds a [SendStreamingMessageResponse] instance with the configured parameters.
     *
     * @return A new [SendStreamingMessageResponse] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): SendStreamingMessageResponse {
        require(result != null || error != null) { "Either result or error must be provided" }
        require(!(result != null && error != null)) {
            "Cannot have both result and error in the same response"
        }

        return SendStreamingMessageResponse(
            id = id,
            result = result,
            error = error,
        )
    }
}

/**
 * Top-level DSL function for creating [SendStreamingMessageResponse].
 *
 * @param init The lambda to configure the send streaming message response.
 * @return A new [SendStreamingMessageResponse] instance.
 */
public inline fun sendStreamingMessageResponse(
    init: SendStreamingMessageResponseBuilder.() -> Unit,
): SendStreamingMessageResponse = SendStreamingMessageResponseBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [SendStreamingMessageResponse].
 *
 * @param init The consumer to configure the send streaming message response.
 * @return A new [SendStreamingMessageResponse] instance.
 */
public fun sendStreamingMessageResponse(
    init: Consumer<SendStreamingMessageResponseBuilder>,
): SendStreamingMessageResponse {
    val builder = SendStreamingMessageResponseBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new instance of a SendStreamingMessageResponse using the provided configuration block.
 *
 * @param block A configuration block for building a SendStreamingMessageResponse instance using the SendStreamingMessageResponseBuilder.
 * @return A newly created SendStreamingMessageResponse instance.
 */
public fun SendStreamingMessageResponse.Companion.create(
    block: SendStreamingMessageResponseBuilder.() -> Unit,
): SendStreamingMessageResponse = SendStreamingMessageResponseBuilder().apply(block).build()

/**
 * Creates a new instance of a SendStreamingMessageResponse using the provided Java-friendly Consumer.
 *
 * @param block A consumer for building a SendStreamingMessageResponse instance using the SendStreamingMessageResponseBuilder.
 * @return A newly created SendStreamingMessageResponse instance.
 */
public fun SendStreamingMessageResponse.Companion.create(
    block: Consumer<SendStreamingMessageResponseBuilder>,
): SendStreamingMessageResponse {
    val builder = SendStreamingMessageResponseBuilder()
    block.accept(builder)
    return builder.build()
}
