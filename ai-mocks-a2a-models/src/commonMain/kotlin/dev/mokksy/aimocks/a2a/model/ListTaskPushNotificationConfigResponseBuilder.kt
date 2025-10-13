package dev.mokksy.aimocks.a2a.model

/**
 * DSL builder for [ListTaskPushNotificationConfigResponse].
 *
 * Example usage:
 * ```
 * val response = listTaskPushNotificationConfigResponse {
 *     id = "1"
 *     result = listOf(taskConfig1, taskConfig2)
 * }
 * ```
 */
public class ListTaskPushNotificationConfigResponseBuilder {
    public var id: RequestId? = null
    public var result: List<TaskPushNotificationConfig>? = null
    public var error: JSONRPCError? = null

    /**
     * Sets the ID of the response.
     *
     * @param id The ID of the response.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): ListTaskPushNotificationConfigResponseBuilder =
        apply {
            this.id = id
        }

    /**
     * Sets the result list of task push notification configs.
     *
     * @param result The list of task push notification config results.
     * @return This builder instance for method chaining.
     */
    public fun result(
        result: List<TaskPushNotificationConfig>,
    ): ListTaskPushNotificationConfigResponseBuilder =
        apply {
            this.result = result
        }

    /**
     * Sets the error for the response.
     *
     * @param error The JSON-RPC error.
     * @return This builder instance for method chaining.
     */
    public fun error(error: JSONRPCError): ListTaskPushNotificationConfigResponseBuilder =
        apply {
            this.error = error
        }

    /**
     * Builds a [ListTaskPushNotificationConfigResponse] instance with the configured parameters.
     *
     * @return A new [ListTaskPushNotificationConfigResponse] instance.
     */
    public fun build(): ListTaskPushNotificationConfigResponse =
        ListTaskPushNotificationConfigResponse(
            id = id,
            result = result,
            error = error,
        )
}

/**
 * Top-level DSL function for creating [ListTaskPushNotificationConfigResponse].
 *
 * @param init The lambda to configure the list task push notification config response.
 * @return A new [ListTaskPushNotificationConfigResponse] instance.
 */
public inline fun listTaskPushNotificationConfigResponse(
    init: ListTaskPushNotificationConfigResponseBuilder.() -> Unit,
): ListTaskPushNotificationConfigResponse =
    ListTaskPushNotificationConfigResponseBuilder().apply(init).build()

/**
 * DSL extension for [ListTaskPushNotificationConfigResponse.Companion].
 *
 * @param init The lambda to configure the list task push notification config response.
 * @return A new [ListTaskPushNotificationConfigResponse] instance.
 */
public fun ListTaskPushNotificationConfigResponse.Companion.create(
    init: ListTaskPushNotificationConfigResponseBuilder.() -> Unit,
): ListTaskPushNotificationConfigResponse =
    ListTaskPushNotificationConfigResponseBuilder().apply(init).build()
