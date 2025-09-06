package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [GetTaskPushNotificationResponse].
 *
 * Example usage:
 * ```
 * val response = getTaskPushNotificationResponse {
 *     id = myRequestId
 *     result = myTaskPushNotificationConfig
 * }
 * ```
 */
public class GetTaskPushNotificationResponseBuilder {
    public var id: RequestId? = null
    public var result: TaskPushNotificationConfig? = null
    public var error: JSONRPCError? = null

    /**
     * Sets the ID of the response.
     *
     * @param id The ID of the response.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): GetTaskPushNotificationResponseBuilder =
        apply {
            this.id = id
        }

    /**
     * Sets the result task push notification config.
     *
     * @param result The task push notification config result.
     * @return This builder instance for method chaining.
     */
    public fun result(result: TaskPushNotificationConfig): GetTaskPushNotificationResponseBuilder =
        apply {
            this.result = result
        }

    /**
     * Configures the result task push notification config using a lambda with receiver.
     *
     * @param init The lambda to configure the task push notification config.
     * @return This builder instance for method chaining.
     */
    public fun result(init: TaskPushNotificationConfigBuilder.() -> Unit): GetTaskPushNotificationResponseBuilder =
        apply {
            result = TaskPushNotificationConfig.create(init)
        }

    /**
     * Configures the result task push notification config using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task push notification config.
     * @return This builder instance for method chaining.
     */
    public fun result(init: Consumer<TaskPushNotificationConfigBuilder>): GetTaskPushNotificationResponseBuilder =
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
    public fun error(error: JSONRPCError): GetTaskPushNotificationResponseBuilder =
        apply {
            this.error = error
        }

    /**
     * Builds a [GetTaskPushNotificationResponse] instance with the configured parameters.
     *
     * @return A new [GetTaskPushNotificationResponse] instance.
     */
    public fun build(): GetTaskPushNotificationResponse =
        GetTaskPushNotificationResponse(
            id = id,
            result = result,
            error = error,
        )
}

/**
 * Top-level DSL function for creating [GetTaskPushNotificationResponse].
 *
 * @param init The lambda to configure the get task push notification response.
 * @return A new [GetTaskPushNotificationResponse] instance.
 */
public inline fun getTaskPushNotificationResponse(
    init: GetTaskPushNotificationResponseBuilder.() -> Unit,
): GetTaskPushNotificationResponse = GetTaskPushNotificationResponseBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [GetTaskPushNotificationResponse].
 *
 * @param init The consumer to configure the get task push notification response.
 * @return A new [GetTaskPushNotificationResponse] instance.
 */
public fun getTaskPushNotificationResponse(
    init: Consumer<GetTaskPushNotificationResponseBuilder>,
): GetTaskPushNotificationResponse {
    val builder = GetTaskPushNotificationResponseBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [GetTaskPushNotificationResponse.Companion].
 *
 * @param init The lambda to configure the get task push notification response.
 * @return A new [GetTaskPushNotificationResponse] instance.
 */
public fun GetTaskPushNotificationResponse.Companion.create(
    init: GetTaskPushNotificationResponseBuilder.() -> Unit,
): GetTaskPushNotificationResponse = GetTaskPushNotificationResponseBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [GetTaskPushNotificationResponse.Companion].
 *
 * @param init The consumer to configure the get task push notification response.
 * @return A new [GetTaskPushNotificationResponse] instance.
 */
public fun GetTaskPushNotificationResponse.Companion.create(
    init: Consumer<GetTaskPushNotificationResponseBuilder>,
): GetTaskPushNotificationResponse {
    val builder = GetTaskPushNotificationResponseBuilder()
    init.accept(builder)
    return builder.build()
}
