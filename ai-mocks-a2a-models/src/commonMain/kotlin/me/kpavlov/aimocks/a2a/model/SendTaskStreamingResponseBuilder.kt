package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [SendTaskStreamingResponse].
 *
 * Example usage:
 * ```
 * val response = sendTaskStreamingResponse {
 *     id = myRequestId
 *     statusUpdateEvent {
 *         id = "task-123"
 *         status = TaskStatus.COMPLETED
 *     }
 * }
 * ```
 */
public class SendTaskStreamingResponseBuilder {
    public var id: RequestId? = null
    public var result: TaskUpdateEvent? = null
    public var error: JSONRPCError? = null

    /**
     * Sets the ID of the response.
     *
     * @param id The ID of the response.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): SendTaskStreamingResponseBuilder = apply {
        this.id = id
    }

    /**
     * Sets the result task update event.
     *
     * @param result The task update event result.
     * @return This builder instance for method chaining.
     */
    public fun result(result: TaskUpdateEvent): SendTaskStreamingResponseBuilder = apply {
        this.result = result
    }

    /**
     * Configures the result as a task status update event using a lambda with receiver.
     *
     * @param init The lambda to configure the task status update event.
     * @return This builder instance for method chaining.
     */
    public fun statusUpdateEvent(init: TaskStatusUpdateEventBuilder.() -> Unit): SendTaskStreamingResponseBuilder =
        apply {
            result = TaskStatusUpdateEventBuilder().apply(init).build()
        }

    /**
     * Configures the result as a task status update event using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task status update event.
     * @return This builder instance for method chaining.
     */
    public fun statusUpdateEvent(init: Consumer<TaskStatusUpdateEventBuilder>): SendTaskStreamingResponseBuilder =
        apply {
            val builder = TaskStatusUpdateEventBuilder()
            init.accept(builder)
            result = builder.build()
        }

    /**
     * Configures the result as a task artifact update event using a lambda with receiver.
     *
     * @param init The lambda to configure the task artifact update event.
     * @return This builder instance for method chaining.
     */
    public fun artifactUpdateEvent(init: TaskArtifactUpdateEventBuilder.() -> Unit): SendTaskStreamingResponseBuilder =
        apply {
            result = TaskArtifactUpdateEventBuilder().apply(init).build()
        }

    /**
     * Configures the result as a task artifact update event using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task artifact update event.
     * @return This builder instance for method chaining.
     */
    public fun artifactUpdateEvent(init: Consumer<TaskArtifactUpdateEventBuilder>): SendTaskStreamingResponseBuilder =
        apply {
            val builder = TaskArtifactUpdateEventBuilder()
            init.accept(builder)
            result = builder.build()
        }

    /**
     * Sets the error.
     *
     * @param error The error.
     * @return This builder instance for method chaining.
     */
    public fun error(error: JSONRPCError): SendTaskStreamingResponseBuilder = apply {
        this.error = error
    }



    /**
     * Builds a [SendTaskStreamingResponse] instance with the configured parameters.
     *
     * @return A new [SendTaskStreamingResponse] instance.
     */
    public fun build(): SendTaskStreamingResponse =
        SendTaskStreamingResponse(
            id = id,
            result = result,
            error = error,
        )
}

/**
 * Top-level DSL function for creating [SendTaskStreamingResponse].
 *
 * @param init The lambda to configure the send task streaming response.
 * @return A new [SendTaskStreamingResponse] instance.
 */
public inline fun sendTaskStreamingResponse(
    init: SendTaskStreamingResponseBuilder.() -> Unit
): SendTaskStreamingResponse =
    SendTaskStreamingResponseBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [SendTaskStreamingResponse].
 *
 * @param init The consumer to configure the send task streaming response.
 * @return A new [SendTaskStreamingResponse] instance.
 */
public fun sendTaskStreamingResponse(
    init: Consumer<SendTaskStreamingResponseBuilder>
): SendTaskStreamingResponse {
    val builder = SendTaskStreamingResponseBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [SendTaskStreamingResponse.Companion].
 *
 * @param init The lambda to configure the send task streaming response.
 * @return A new [SendTaskStreamingResponse] instance.
 */
public fun SendTaskStreamingResponse.Companion.create(
    init: SendTaskStreamingResponseBuilder.() -> Unit
): SendTaskStreamingResponse = SendTaskStreamingResponseBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [SendTaskStreamingResponse.Companion].
 *
 * @param init The consumer to configure the send task streaming response.
 * @return A new [SendTaskStreamingResponse] instance.
 */
public fun SendTaskStreamingResponse.Companion.create(
    init: Consumer<SendTaskStreamingResponseBuilder>
): SendTaskStreamingResponse {
    val builder = SendTaskStreamingResponseBuilder()
    init.accept(builder)
    return builder.build()
}
