package me.kpavlov.aimocks.a2a.model

/**
 * DSL builder for [ListTaskPushNotificationConfigParams].
 *
 * Example usage:
 * ```
 * val params = listTaskPushNotificationConfigParams {
 *     limit = 10
 *     offset = 0
 * }
 * ```
 */
public class ListTaskPushNotificationConfigParamsBuilder {
    public var limit: Int? = null
    public var offset: Int? = null

    /**
     * Sets the limit for the number of configurations to return.
     *
     * @param limit The maximum number of configurations to return.
     * @return This builder instance for method chaining.
     */
    public fun limit(limit: Int): ListTaskPushNotificationConfigParamsBuilder =
        apply {
            this.limit = limit
        }

    /**
     * Sets the offset for pagination.
     *
     * @param offset The number of configurations to skip.
     * @return This builder instance for method chaining.
     */
    public fun offset(offset: Int): ListTaskPushNotificationConfigParamsBuilder =
        apply {
            this.offset = offset
        }

    /**
     * Builds a [ListTaskPushNotificationConfigParams] instance with the configured parameters.
     *
     * @return A new [ListTaskPushNotificationConfigParams] instance.
     */
    public fun build(): ListTaskPushNotificationConfigParams =
        ListTaskPushNotificationConfigParams(
            limit = limit,
            offset = offset,
        )
}

/**
 * Top-level DSL function for creating [ListTaskPushNotificationConfigParams].
 *
 * @param init The lambda to configure the list task push notification config params.
 * @return A new [ListTaskPushNotificationConfigParams] instance.
 */
public inline fun listTaskPushNotificationConfigParams(
    init: ListTaskPushNotificationConfigParamsBuilder.() -> Unit,
): ListTaskPushNotificationConfigParams =
    ListTaskPushNotificationConfigParamsBuilder()
        .apply(init)
        .build()

/**
 * DSL extension for [ListTaskPushNotificationConfigParams.Companion].
 *
 * @param init The lambda to configure the list task push notification config params.
 * @return A new [ListTaskPushNotificationConfigParams] instance.
 */
public fun ListTaskPushNotificationConfigParams.Companion.create(
    init: ListTaskPushNotificationConfigParamsBuilder.() -> Unit,
): ListTaskPushNotificationConfigParams =
    ListTaskPushNotificationConfigParamsBuilder()
        .apply(init)
        .build()
