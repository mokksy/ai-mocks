package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [SetTaskPushNotificationResponse].
 *
 * Example usage:
 * ```
 * val response = setTaskPushNotificationResponse {
 *     id = myRequestId
 *     result {
 *         id = "task-123"
 *         pushNotificationConfig {
 *             url = "https://example.org/notifications"
 *             token = "auth-token"
 *         }
 *     }
 * }
 * ```
 */
public class SetTaskPushNotificationResponseBuilder {
    public var id: RequestId? = null
    public var result: TaskPushNotificationConfig? = null
    public var error: JSONRPCError? = null

    /**
     * Sets the ID of the response.
     *
     * @param id The ID of the response.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): SetTaskPushNotificationResponseBuilder =
        apply {
            this.id = id
        }

    /**
     * Sets the result task push notification config.
     *
     * @param result The task push notification config result.
     * @return This builder instance for method chaining.
     */
    public fun result(result: TaskPushNotificationConfig): SetTaskPushNotificationResponseBuilder =
        apply {
            this.result = result
        }

    /**
     * Configures the result task push notification config using a lambda with receiver.
     *
     * @param init The lambda to configure the task push notification config.
     * @return This builder instance for method chaining.
     */
    public fun result(init: TaskPushNotificationConfigBuilder.() -> Unit): SetTaskPushNotificationResponseBuilder =
        apply {
            result = TaskPushNotificationConfig.create(init)
        }

    /**
     * Configures the result task push notification config using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task push notification config.
     * @return This builder instance for method chaining.
     */
    public fun result(init: Consumer<TaskPushNotificationConfigBuilder>): SetTaskPushNotificationResponseBuilder =
        apply {
            val builder = TaskPushNotificationConfigBuilder()
            init.accept(builder)
            result = builder.build()
        }

    /**
     * Sets the error.
     *
     * @param error The error.
     * @return This builder instance for method chaining.
     */
    public fun error(error: JSONRPCError): SetTaskPushNotificationResponseBuilder =
        apply {
            this.error = error
        }

    /**
     * Builds a [SetTaskPushNotificationResponse] instance with the configured parameters.
     *
     * @return A new [SetTaskPushNotificationResponse] instance.
     */
    public fun build(): SetTaskPushNotificationResponse =
        SetTaskPushNotificationResponse(
            id = id,
            result = result,
            error = error,
        )
}

/**
 * Top-level DSL function for creating [SetTaskPushNotificationResponse].
 *
 * @param init The lambda to configure the set task push notification response.
 * @return A new [SetTaskPushNotificationResponse] instance.
 */
public inline fun setTaskPushNotificationResponse(
    init: SetTaskPushNotificationResponseBuilder.() -> Unit,
): SetTaskPushNotificationResponse = SetTaskPushNotificationResponseBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [SetTaskPushNotificationResponse].
 *
 * @param init The consumer to configure the set task push notification response.
 * @return A new [SetTaskPushNotificationResponse] instance.
 */
public fun setTaskPushNotificationResponse(
    init: Consumer<SetTaskPushNotificationResponseBuilder>,
): SetTaskPushNotificationResponse {
    val builder = SetTaskPushNotificationResponseBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [SetTaskPushNotificationResponse.Companion].
 *
 * @param init The lambda to configure the set task push notification response.
 * @return A new [SetTaskPushNotificationResponse] instance.
 */
public fun SetTaskPushNotificationResponse.Companion.create(
    init: SetTaskPushNotificationResponseBuilder.() -> Unit,
): SetTaskPushNotificationResponse = SetTaskPushNotificationResponseBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [SetTaskPushNotificationResponse.Companion].
 *
 * @param init The consumer to configure the set task push notification response.
 * @return A new [SetTaskPushNotificationResponse] instance.
 */
public fun SetTaskPushNotificationResponse.Companion.create(
    init: Consumer<SetTaskPushNotificationResponseBuilder>,
): SetTaskPushNotificationResponse {
    val builder = SetTaskPushNotificationResponseBuilder()
    init.accept(builder)
    return builder.build()
}
