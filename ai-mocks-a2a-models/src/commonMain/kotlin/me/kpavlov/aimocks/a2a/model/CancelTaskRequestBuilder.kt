package me.kpavlov.aimocks.a2a.model

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

    public fun params(init: TaskIdParamsBuilder.() -> Unit) {
        params = TaskIdParamsBuilder().apply(init).build()
    }

    public fun build(): CancelTaskRequest =
        CancelTaskRequest(
            id = id,
            params = requireNotNull(params) { "CancelTaskRequest.params must be provided" },
        )
}

/**
 * DSL top-level function.
 */
public inline fun cancelTaskRequest(init: CancelTaskRequestBuilder.() -> Unit): CancelTaskRequest =
    CancelTaskRequestBuilder().apply(init).build()

/**
 * DSL extension for [CancelTaskRequest.Companion].
 */
public fun CancelTaskRequest.Companion.create(
    init: CancelTaskRequestBuilder.() -> Unit,
): CancelTaskRequest = CancelTaskRequestBuilder().apply(init).build()
