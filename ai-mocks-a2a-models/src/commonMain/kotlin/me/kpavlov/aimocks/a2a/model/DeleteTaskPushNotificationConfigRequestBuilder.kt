package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [DeleteTaskPushNotificationConfigRequest].
 *
 * Example usage:
 * ```
 * val request = deleteTaskPushNotificationConfigRequest {
 *     id = "1"
 *     params {
 *         id = "task_12345"
 *     }
 * }
 * ```
 */
public class DeleteTaskPushNotificationConfigRequestBuilder {
    public var id: RequestId? = null
    public var params: DeleteTaskPushNotificationConfigParams? = null

    /**
     * Sets the ID of the request.
     *
     * @param id The ID of the request.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): DeleteTaskPushNotificationConfigRequestBuilder =
        apply {
            this.id = id
        }

    /**
     * Configures the delete parameters using a lambda with receiver.
     *
     * @param init The lambda to configure the delete parameters.
     * @return This builder instance for method chaining.
     */
    public fun params(
        init: DeleteTaskPushNotificationConfigParamsBuilder.() -> Unit,
    ): DeleteTaskPushNotificationConfigRequestBuilder =
        apply {
            params = DeleteTaskPushNotificationConfigParamsBuilder().apply(init).build()
        }

    /**
     * Configures the delete parameters using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the delete parameters.
     * @return This builder instance for method chaining.
     */
    public fun params(
        init: Consumer<DeleteTaskPushNotificationConfigParamsBuilder>,
    ): DeleteTaskPushNotificationConfigRequestBuilder =
        apply {
            val builder = DeleteTaskPushNotificationConfigParamsBuilder()
            init.accept(builder)
            params = builder.build()
        }

    /**
     * Builds a [DeleteTaskPushNotificationConfigRequest] instance with the configured parameters.
     *
     * @return A new [DeleteTaskPushNotificationConfigRequest] instance.
     */
    public fun build(): DeleteTaskPushNotificationConfigRequest =
        DeleteTaskPushNotificationConfigRequest(
            id = id,
            params = params,
        )
}

/**
 * Top-level DSL function for creating [DeleteTaskPushNotificationConfigRequest].
 *
 * @param init The lambda to configure the delete task push notification config request.
 * @return A new [DeleteTaskPushNotificationConfigRequest] instance.
 */
public inline fun deleteTaskPushNotificationConfigRequest(
    init: DeleteTaskPushNotificationConfigRequestBuilder.() -> Unit,
): DeleteTaskPushNotificationConfigRequest =
    DeleteTaskPushNotificationConfigRequestBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [DeleteTaskPushNotificationConfigRequest].
 *
 * @param init The consumer to configure the delete task push notification config request.
 * @return A new [DeleteTaskPushNotificationConfigRequest] instance.
 */
public fun deleteTaskPushNotificationConfigRequest(
    init: Consumer<DeleteTaskPushNotificationConfigRequestBuilder>,
): DeleteTaskPushNotificationConfigRequest {
    val builder = DeleteTaskPushNotificationConfigRequestBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [DeleteTaskPushNotificationConfigRequest.Companion].
 *
 * @param init The lambda to configure the delete task push notification config request.
 * @return A new [DeleteTaskPushNotificationConfigRequest] instance.
 */
public fun DeleteTaskPushNotificationConfigRequest.Companion.create(
    init: DeleteTaskPushNotificationConfigRequestBuilder.() -> Unit,
): DeleteTaskPushNotificationConfigRequest =
    DeleteTaskPushNotificationConfigRequestBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [DeleteTaskPushNotificationConfigRequest.Companion].
 *
 * @param init The consumer to configure the delete task push notification config request.
 * @return A new [DeleteTaskPushNotificationConfigRequest] instance.
 */
public fun DeleteTaskPushNotificationConfigRequest.Companion.create(
    init: Consumer<DeleteTaskPushNotificationConfigRequestBuilder>,
): DeleteTaskPushNotificationConfigRequest {
    val builder = DeleteTaskPushNotificationConfigRequestBuilder()
    init.accept(builder)
    return builder.build()
}
