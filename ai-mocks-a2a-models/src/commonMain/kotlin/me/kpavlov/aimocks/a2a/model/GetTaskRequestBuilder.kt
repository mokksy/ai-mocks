package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [GetTaskRequest].
 *
 * Example usage:
 * ```
 * val request = getTaskRequest {
 *     id = myRequestId
 *     params {
 *         id = "task-123"
 *         historyLength = 10
 *     }
 * }
 * ```
 */
public class GetTaskRequestBuilder {
    public var id: RequestId? = null
    public var params: TaskQueryParams? = null

    /**
     * Sets the ID of the request.
     *
     * @param id The ID of the request.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): GetTaskRequestBuilder =
        apply {
            this.id = id
        }

    /**
     * Configures the task query params using a lambda with receiver.
     *
     * @param init The lambda to configure the task query params.
     * @return This builder instance for method chaining.
     */
    public fun params(init: TaskQueryParamsBuilder.() -> Unit): GetTaskRequestBuilder =
        apply {
            params = TaskQueryParams.create(init)
        }

    /**
     * Configures the task query params using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task query params.
     * @return This builder instance for method chaining.
     */
    public fun params(init: Consumer<TaskQueryParamsBuilder>): GetTaskRequestBuilder =
        apply {
            val builder = TaskQueryParamsBuilder()
            init.accept(builder)
            params = builder.build()
        }

    /**
     * Builds a [GetTaskRequest] instance with the configured parameters.
     *
     * @return A new [GetTaskRequest] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): GetTaskRequest =
        GetTaskRequest(
            id = id,
            params = requireNotNull(params) { "GetTaskRequest.params must be provided" },
        )
}

/**
 * Top-level DSL function for creating [GetTaskRequest].
 *
 * @param init The lambda to configure the get task request.
 * @return A new [GetTaskRequest] instance.
 */
public inline fun getTaskRequest(init: GetTaskRequestBuilder.() -> Unit): GetTaskRequest =
    GetTaskRequestBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [GetTaskRequest].
 *
 * @param init The consumer to configure the get task request.
 * @return A new [GetTaskRequest] instance.
 */
public fun getTaskRequest(init: Consumer<GetTaskRequestBuilder>): GetTaskRequest {
    val builder = GetTaskRequestBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [GetTaskRequest.Companion].
 *
 * @param init The lambda to configure the get task request.
 * @return A new [GetTaskRequest] instance.
 */
public fun GetTaskRequest.Companion.create(init: GetTaskRequestBuilder.() -> Unit): GetTaskRequest =
    GetTaskRequestBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [GetTaskRequest.Companion].
 *
 * @param init The consumer to configure the get task request.
 * @return A new [GetTaskRequest] instance.
 */
public fun GetTaskRequest.Companion.create(init: Consumer<GetTaskRequestBuilder>): GetTaskRequest {
    val builder = GetTaskRequestBuilder()
    init.accept(builder)
    return builder.build()
}
