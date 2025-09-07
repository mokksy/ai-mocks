package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [SetTaskPushNotificationRequest].
 *
 * Example usage:
 * ```
 * val request = setTaskPushNotificationRequest {
 *     id = myRequestId
 *     params {
 *         id = "task-123"
 *         pushNotificationConfig {
 *             url = "https://example.org/notifications"
 *             token = "auth-token"
 *         }
 *     }
 * }
 * ```
 */
public class SetTaskPushNotificationRequestBuilder {
    public var id: RequestId? = null
    public var params: TaskPushNotificationConfig? = null

    /**
     * Sets the ID of the request.
     *
     * @param id The ID of the request.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): SetTaskPushNotificationRequestBuilder =
        apply {
            this.id = id
        }

    /**
     * Configures the task push notification config using a lambda with receiver.
     *
     * @param init The lambda to configure the task push notification config.
     * @return This builder instance for method chaining.
     */
    public fun params(
        init: TaskPushNotificationConfigBuilder.() -> Unit,
    ): SetTaskPushNotificationRequestBuilder =
        apply {
            params = TaskPushNotificationConfig.create(init)
        }

    /**
     * Configures the task push notification config using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task push notification config.
     * @return This builder instance for method chaining.
     */
    public fun params(
        init: Consumer<TaskPushNotificationConfigBuilder>,
    ): SetTaskPushNotificationRequestBuilder =
        apply {
            val builder = TaskPushNotificationConfigBuilder()
            init.accept(builder)
            params = builder.build()
        }

    /**
     * Builds a [SetTaskPushNotificationRequest] instance with the configured parameters.
     *
     * @return A new [SetTaskPushNotificationRequest] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): SetTaskPushNotificationRequest =
        SetTaskPushNotificationRequest(
            id = id,
            params =
                requireNotNull(
                    params,
                ) { "SetTaskPushNotificationRequest.params must be provided" },
        )
}

/**
 * Top-level DSL function for creating [SetTaskPushNotificationRequest].
 *
 * @param init The lambda to configure the set task push notification request.
 * @return A new [SetTaskPushNotificationRequest] instance.
 */
public inline fun setTaskPushNotificationRequest(
    init: SetTaskPushNotificationRequestBuilder.() -> Unit,
): SetTaskPushNotificationRequest = SetTaskPushNotificationRequestBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [SetTaskPushNotificationRequest].
 *
 * @param init The consumer to configure the set task push notification request.
 * @return A new [SetTaskPushNotificationRequest] instance.
 */
public fun setTaskPushNotificationRequest(
    init: Consumer<SetTaskPushNotificationRequestBuilder>,
): SetTaskPushNotificationRequest {
    val builder = SetTaskPushNotificationRequestBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [SetTaskPushNotificationRequest.Companion].
 *
 * @param init The lambda to configure the set task push notification request.
 * @return A new [SetTaskPushNotificationRequest] instance.
 */
public fun SetTaskPushNotificationRequest.Companion.create(
    init: SetTaskPushNotificationRequestBuilder.() -> Unit,
): SetTaskPushNotificationRequest = SetTaskPushNotificationRequestBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [SetTaskPushNotificationRequest.Companion].
 *
 * @param init The consumer to configure the set task push notification request.
 * @return A new [SetTaskPushNotificationRequest] instance.
 */
public fun SetTaskPushNotificationRequest.Companion.create(
    init: Consumer<SetTaskPushNotificationRequestBuilder>,
): SetTaskPushNotificationRequest {
    val builder = SetTaskPushNotificationRequestBuilder()
    init.accept(builder)
    return builder.build()
}
