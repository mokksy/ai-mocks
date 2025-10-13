package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [GetTaskPushNotificationRequest].
 *
 * Example usage:
 * ```
 * val request = getTaskPushNotificationRequest {
 *     id = myRequestId
 *     params {
 *         id = myTaskId
 *     }
 * }
 * ```
 */
public class GetTaskPushNotificationRequestBuilder {
    public var id: RequestId? = null
    public var params: TaskIdParams? = null

    /**
     * Sets the ID of the request.
     *
     * @param id The ID of the request.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): GetTaskPushNotificationRequestBuilder =
        apply {
            this.id = id
        }

    /**
     * Configures the task ID parameters using a lambda with receiver.
     *
     * @param init The lambda to configure the task ID parameters.
     * @return This builder instance for method chaining.
     */
    public fun params(init: TaskIdParamsBuilder.() -> Unit): GetTaskPushNotificationRequestBuilder =
        apply {
            params = TaskIdParamsBuilder().apply(init).build()
        }

    /**
     * Configures the task ID parameters using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task ID parameters.
     * @return This builder instance for method chaining.
     */
    public fun params(init: Consumer<TaskIdParamsBuilder>): GetTaskPushNotificationRequestBuilder =
        apply {
            val builder = TaskIdParamsBuilder()
            init.accept(builder)
            params = builder.build()
        }

    /**
     * Builds a [GetTaskPushNotificationRequest] instance with the configured parameters.
     *
     * @return A new [GetTaskPushNotificationRequest] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): GetTaskPushNotificationRequest =
        GetTaskPushNotificationRequest(
            id = id,
            params =
                requireNotNull(
                    params,
                ) { "GetTaskPushNotificationRequest.params must be provided" },
        )
}

/**
 * Top-level DSL function for creating [GetTaskPushNotificationRequest].
 *
 * @param init The lambda to configure the get task push notification request.
 * @return A new [GetTaskPushNotificationRequest] instance.
 */
public inline fun getTaskPushNotificationRequest(
    init: GetTaskPushNotificationRequestBuilder.() -> Unit,
): GetTaskPushNotificationRequest = GetTaskPushNotificationRequestBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [GetTaskPushNotificationRequest].
 *
 * @param init The consumer to configure the get task push notification request.
 * @return A new [GetTaskPushNotificationRequest] instance.
 */
public fun getTaskPushNotificationRequest(
    init: Consumer<GetTaskPushNotificationRequestBuilder>,
): GetTaskPushNotificationRequest {
    val builder = GetTaskPushNotificationRequestBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [GetTaskPushNotificationRequest.Companion].
 *
 * @param init The lambda to configure the get task push notification request.
 * @return A new [GetTaskPushNotificationRequest] instance.
 */
public fun GetTaskPushNotificationRequest.Companion.create(
    init: GetTaskPushNotificationRequestBuilder.() -> Unit,
): GetTaskPushNotificationRequest = GetTaskPushNotificationRequestBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [GetTaskPushNotificationRequest.Companion].
 *
 * @param init The consumer to configure the get task push notification request.
 * @return A new [GetTaskPushNotificationRequest] instance.
 */
public fun GetTaskPushNotificationRequest.Companion.create(
    init: Consumer<GetTaskPushNotificationRequestBuilder>,
): GetTaskPushNotificationRequest {
    val builder = GetTaskPushNotificationRequestBuilder()
    init.accept(builder)
    return builder.build()
}
