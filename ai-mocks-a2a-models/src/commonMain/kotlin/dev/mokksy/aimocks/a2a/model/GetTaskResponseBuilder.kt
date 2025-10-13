package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [GetTaskResponse].
 *
 * Example usage:
 * ```
 * val response = getTaskResponse {
 *     id = myRequestId
 *     result = myTask
 * }
 * ```
 */
public class GetTaskResponseBuilder {
    public var id: RequestId? = null
    public var result: Task? = null
    public var error: JSONRPCError? = null

    /**
     * Sets the ID of the response.
     *
     * @param id The ID of the response.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): GetTaskResponseBuilder =
        apply {
            this.id = id
        }

    /**
     * Sets the result task.
     *
     * @param result The task result.
     * @return This builder instance for method chaining.
     */
    public fun result(result: Task): GetTaskResponseBuilder =
        apply {
            this.result = result
        }

    /**
     * Configures the result task using a lambda with receiver.
     *
     * @param init The lambda to configure the task.
     * @return This builder instance for method chaining.
     */
    public fun result(init: TaskBuilder.() -> Unit): GetTaskResponseBuilder =
        apply {
            result = TaskBuilder().apply(init).build()
        }

    /**
     * Configures the result task using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task.
     * @return This builder instance for method chaining.
     */
    public fun result(init: Consumer<TaskBuilder>): GetTaskResponseBuilder =
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
    public fun error(error: JSONRPCError): GetTaskResponseBuilder =
        apply {
            this.error = error
        }

    /**
     * Builds a [GetTaskResponse] instance with the configured parameters.
     *
     * @return A new [GetTaskResponse] instance.
     */
    public fun build(): GetTaskResponse =
        GetTaskResponse(
            id = id,
            result = result,
            error = error,
        )
}

/**
 * Top-level DSL function for creating [GetTaskResponse].
 *
 * @param init The lambda to configure the get task response.
 * @return A new [GetTaskResponse] instance.
 */
public inline fun getTaskResponse(init: GetTaskResponseBuilder.() -> Unit): GetTaskResponse =
    GetTaskResponseBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [GetTaskResponse].
 *
 * @param init The consumer to configure the get task response.
 * @return A new [GetTaskResponse] instance.
 */
public fun getTaskResponse(init: Consumer<GetTaskResponseBuilder>): GetTaskResponse {
    val builder = GetTaskResponseBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [GetTaskResponse.Companion].
 *
 * @param init The lambda to configure the get task response.
 * @return A new [GetTaskResponse] instance.
 */
public fun GetTaskResponse.Companion.create(
    init: GetTaskResponseBuilder.() -> Unit,
): GetTaskResponse = GetTaskResponseBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [GetTaskResponse.Companion].
 *
 * @param init The consumer to configure the get task response.
 * @return A new [GetTaskResponse] instance.
 */
public fun GetTaskResponse.Companion.create(
    init: Consumer<GetTaskResponseBuilder>,
): GetTaskResponse {
    val builder = GetTaskResponseBuilder()
    init.accept(builder)
    return builder.build()
}
