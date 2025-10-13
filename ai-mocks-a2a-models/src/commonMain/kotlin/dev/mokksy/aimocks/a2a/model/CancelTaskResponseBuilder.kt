package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [CancelTaskResponse].
 *
 * Example usage:
 * ```
 * val response = cancelTaskResponse {
 *     id = myRequestId
 *     result = myTask
 * }
 * ```
 */
public class CancelTaskResponseBuilder {
    public var id: RequestId? = null
    public var result: Task? = null
    public var error: JSONRPCError? = null

    /**
     * Sets the ID of the response.
     *
     * @param id The ID of the response.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): CancelTaskResponseBuilder =
        apply {
            this.id = id
        }

    /**
     * Sets the result task.
     *
     * @param result The task result.
     * @return This builder instance for method chaining.
     */
    public fun result(result: Task): CancelTaskResponseBuilder =
        apply {
            this.result = result
        }

    /**
     * Configures the result task using a lambda with receiver.
     *
     * @param init The lambda to configure the task.
     * @return This builder instance for method chaining.
     */
    public fun result(init: TaskBuilder.() -> Unit): CancelTaskResponseBuilder =
        apply {
            result = TaskBuilder().apply(init).build()
        }

    /**
     * Configures the result task using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task.
     * @return This builder instance for method chaining.
     */
    public fun result(init: Consumer<TaskBuilder>): CancelTaskResponseBuilder =
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
    public fun error(error: JSONRPCError): CancelTaskResponseBuilder =
        apply {
            this.error = error
        }

    /**
     * Builds a [CancelTaskResponse] instance with the configured parameters.
     *
     * @return A new [CancelTaskResponse] instance.
     */
    public fun build(): CancelTaskResponse =
        CancelTaskResponse(
            id = id,
            result = result,
            error = error,
        )
}

/**
 * Top-level DSL function for creating [CancelTaskResponse].
 *
 * @param init The lambda to configure the cancel task response.
 * @return A new [CancelTaskResponse] instance.
 */
public inline fun cancelTaskResponse(
    init: CancelTaskResponseBuilder.() -> Unit,
): CancelTaskResponse = CancelTaskResponseBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [CancelTaskResponse].
 *
 * @param init The consumer to configure the cancel task response.
 * @return A new [CancelTaskResponse] instance.
 */
public fun cancelTaskResponse(init: Consumer<CancelTaskResponseBuilder>): CancelTaskResponse {
    val builder = CancelTaskResponseBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [CancelTaskResponse.Companion].
 *
 * @param init The lambda to configure the cancel task response.
 * @return A new [CancelTaskResponse] instance.
 */
public fun CancelTaskResponse.Companion.create(
    init: CancelTaskResponseBuilder.() -> Unit,
): CancelTaskResponse = CancelTaskResponseBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [CancelTaskResponse.Companion].
 *
 * @param init The consumer to configure the cancel task response.
 * @return A new [CancelTaskResponse] instance.
 */
public fun CancelTaskResponse.Companion.create(
    init: Consumer<CancelTaskResponseBuilder>,
): CancelTaskResponse {
    val builder = CancelTaskResponseBuilder()
    init.accept(builder)
    return builder.build()
}
