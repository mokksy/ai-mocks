package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [TaskResubscriptionRequest].
 *
 * Example usage:
 * ```
 * val request = taskResubscriptionRequest {
 *     id = myRequestId
 *     params {
 *         id = "task-123"
 *         historyLength = 10
 *     }
 * }
 * ```
 */
public class TaskResubscriptionRequestBuilder {
    public var id: RequestId? = null
    public var params: TaskQueryParams? = null

    /**
     * Sets the ID of the request.
     *
     * @param id The ID of the request.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): TaskResubscriptionRequestBuilder =
        apply {
            this.id = id
        }

    /**
     * Configures the task query params using a lambda with receiver.
     *
     * @param init The lambda to configure the task query params.
     * @return This builder instance for method chaining.
     */
    public fun params(init: TaskQueryParamsBuilder.() -> Unit): TaskResubscriptionRequestBuilder =
        apply {
            params = TaskQueryParams.create(init)
        }

    /**
     * Configures the task query params using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task query params.
     * @return This builder instance for method chaining.
     */
    public fun params(init: Consumer<TaskQueryParamsBuilder>): TaskResubscriptionRequestBuilder =
        apply {
            val builder = TaskQueryParamsBuilder()
            init.accept(builder)
            params = builder.build()
        }

    /**
     * Builds a [TaskResubscriptionRequest] instance with the configured parameters.
     *
     * @return A new [TaskResubscriptionRequest] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): TaskResubscriptionRequest =
        TaskResubscriptionRequest(
            id = id,
            params = requireNotNull(params) { "TaskResubscriptionRequest.params must be provided" },
        )
}

/**
 * Top-level DSL function for creating [TaskResubscriptionRequest].
 *
 * @param init The lambda to configure the task resubscription request.
 * @return A new [TaskResubscriptionRequest] instance.
 */
public inline fun taskResubscriptionRequest(
    init: TaskResubscriptionRequestBuilder.() -> Unit,
): TaskResubscriptionRequest = TaskResubscriptionRequestBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [TaskResubscriptionRequest].
 *
 * @param init The consumer to configure the task resubscription request.
 * @return A new [TaskResubscriptionRequest] instance.
 */
public fun taskResubscriptionRequest(
    init: Consumer<TaskResubscriptionRequestBuilder>,
): TaskResubscriptionRequest {
    val builder = TaskResubscriptionRequestBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [TaskResubscriptionRequest.Companion].
 *
 * @param init The lambda to configure the task resubscription request.
 * @return A new [TaskResubscriptionRequest] instance.
 */
public fun TaskResubscriptionRequest.Companion.create(
    init: TaskResubscriptionRequestBuilder.() -> Unit,
): TaskResubscriptionRequest = TaskResubscriptionRequestBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [TaskResubscriptionRequest.Companion].
 *
 * @param init The consumer to configure the task resubscription request.
 * @return A new [TaskResubscriptionRequest] instance.
 */
public fun TaskResubscriptionRequest.Companion.create(
    init: Consumer<TaskResubscriptionRequestBuilder>,
): TaskResubscriptionRequest {
    val builder = TaskResubscriptionRequestBuilder()
    init.accept(builder)
    return builder.build()
}
