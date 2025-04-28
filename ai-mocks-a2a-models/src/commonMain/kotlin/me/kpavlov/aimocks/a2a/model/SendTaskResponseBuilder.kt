package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [SendTaskResponse].
 *
 * Example usage:
 * ```
 * val response = sendTaskResponse {
 *     id = myRequestId
 *     result = myTask
 * }
 * ```
 */
public class SendTaskResponseBuilder {
    public var id: RequestId? = null
    public var result: Task? = null
    public var error: JSONRPCError? = null

    /**
     * Sets the ID of the response.
     *
     * @param id The ID of the response.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): SendTaskResponseBuilder =
        apply {
            this.id = id
        }

    /**
     * Sets the result task.
     *
     * @param result The task result.
     * @return This builder instance for method chaining.
     */
    public fun result(result: Task): SendTaskResponseBuilder =
        apply {
            this.result = result
        }

    /**
     * Configures the result task using a lambda with receiver.
     *
     * @param init The lambda to configure the task.
     * @return This builder instance for method chaining.
     */
    public fun result(init: TaskBuilder.() -> Unit): SendTaskResponseBuilder =
        apply {
            result = TaskBuilder().apply(init).build()
        }

    /**
     * Configures the result task using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task.
     * @return This builder instance for method chaining.
     */
    public fun result(init: Consumer<TaskBuilder>): SendTaskResponseBuilder =
        apply {
            val builder = TaskBuilder()
            init.accept(builder)
            result = builder.build()
        }

    /**
     * Sets the error.
     *
     * @param error The error.
     * @return This builder instance for method chaining.
     */
    public fun error(error: JSONRPCError): SendTaskResponseBuilder =
        apply {
            this.error = error
        }

    /**
     * Builds a [SendTaskResponse] instance with the configured parameters.
     *
     * @return A new [SendTaskResponse] instance.
     */
    public fun build(): SendTaskResponse =
        SendTaskResponse(
            id = id,
            result = result,
            error = error,
        )
}

/**
 * Top-level DSL function for creating [SendTaskResponse].
 *
 * @param init The lambda to configure the send task response.
 * @return A new [SendTaskResponse] instance.
 */
public inline fun sendTaskResponse(init: SendTaskResponseBuilder.() -> Unit): SendTaskResponse =
    SendTaskResponseBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [SendTaskResponse].
 *
 * @param init The consumer to configure the send task response.
 * @return A new [SendTaskResponse] instance.
 */
public fun sendTaskResponse(init: Consumer<SendTaskResponseBuilder>): SendTaskResponse {
    val builder = SendTaskResponseBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [SendTaskResponse.Companion].
 *
 * @param init The lambda to configure the send task response.
 * @return A new [SendTaskResponse] instance.
 */
public fun SendTaskResponse.Companion.create(
    init: SendTaskResponseBuilder.() -> Unit,
): SendTaskResponse = SendTaskResponseBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [SendTaskResponse.Companion].
 *
 * @param init The consumer to configure the send task response.
 * @return A new [SendTaskResponse] instance.
 */
public fun SendTaskResponse.Companion.create(
    init: Consumer<SendTaskResponseBuilder>,
): SendTaskResponse {
    val builder = SendTaskResponseBuilder()
    init.accept(builder)
    return builder.build()
}
