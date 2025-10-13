package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [CancelTaskRequest].
 *
 * Example usage:
 * ```
 * val request = cancelTaskRequest {
 *     id = myRequestId
 *     params = myTaskIdParams
 * }
 * ```
 */
public class CancelTaskRequestBuilder {
    public var id: RequestId? = null
    public var params: TaskIdParams? = null

    /**
     * Sets the ID of the request.
     *
     * @param id The ID of the request.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): CancelTaskRequestBuilder =
        apply {
            this.id = id
        }

    /**
     * Configures the task ID parameters using a lambda with receiver.
     *
     * @param init The lambda to configure the task ID parameters.
     */
    public fun params(init: TaskIdParamsBuilder.() -> Unit) {
        params = TaskIdParamsBuilder().apply(init).build()
    }

    /**
     * Configures the task ID parameters using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task ID parameters.
     */
    public fun params(init: Consumer<TaskIdParamsBuilder>) {
        val builder = TaskIdParamsBuilder()
        init.accept(builder)
        params = builder.build()
    }

    /**
     * Builds a [CancelTaskRequest] instance with the configured parameters.
     *
     * @return A new [CancelTaskRequest] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): CancelTaskRequest =
        CancelTaskRequest(
            id = id,
            params = requireNotNull(params) { "CancelTaskRequest.params must be provided" },
        )
}

/**
 * DSL top-level function.
 *
 * @param init The lambda to configure the cancel task request.
 * @return A new [CancelTaskRequest] instance.
 */
public inline fun cancelTaskRequest(init: CancelTaskRequestBuilder.() -> Unit): CancelTaskRequest =
    CancelTaskRequestBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [CancelTaskRequest].
 *
 * @param init The consumer to configure the cancel task request.
 * @return A new [CancelTaskRequest] instance.
 */
public fun cancelTaskRequest(init: Consumer<CancelTaskRequestBuilder>): CancelTaskRequest {
    val builder = CancelTaskRequestBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [CancelTaskRequest.Companion].
 *
 * @param init The lambda to configure the cancel task request.
 * @return A new [CancelTaskRequest] instance.
 */
public fun CancelTaskRequest.Companion.create(
    init: CancelTaskRequestBuilder.() -> Unit,
): CancelTaskRequest = CancelTaskRequestBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [CancelTaskRequest.Companion].
 *
 * @param init The consumer to configure the cancel task request.
 * @return A new [CancelTaskRequest] instance.
 */
public fun CancelTaskRequest.Companion.create(
    init: Consumer<CancelTaskRequestBuilder>,
): CancelTaskRequest {
    val builder = CancelTaskRequestBuilder()
    init.accept(builder)
    return builder.build()
}
