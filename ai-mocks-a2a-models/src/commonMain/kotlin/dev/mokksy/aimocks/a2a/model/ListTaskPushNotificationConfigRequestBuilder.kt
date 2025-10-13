package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [ListTaskPushNotificationConfigRequest].
 *
 * Example usage:
 * ```
 * val request = listTaskPushNotificationConfigRequest {
 *     id = "1"
 *     params {
 *         limit = 10
 *         offset = 0
 *     }
 * }
 * ```
 */
public class ListTaskPushNotificationConfigRequestBuilder {
    public var id: RequestId? = null
    public var params: ListTaskPushNotificationConfigParams? = null

    /**
     * Sets the ID of the request.
     *
     * @param id The ID of the request.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): ListTaskPushNotificationConfigRequestBuilder =
        apply {
            this.id = id
        }

    /**
     * Configures the list parameters using a lambda with receiver.
     *
     * @param init The lambda to configure the list parameters.
     * @return This builder instance for method chaining.
     */
    public fun params(
        init: ListTaskPushNotificationConfigParamsBuilder.() -> Unit,
    ): ListTaskPushNotificationConfigRequestBuilder =
        apply {
            params = ListTaskPushNotificationConfigParamsBuilder().apply(init).build()
        }

    /**
     * Configures the list parameters using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the list parameters.
     * @return This builder instance for method chaining.
     */
    public fun params(
        init: Consumer<ListTaskPushNotificationConfigParamsBuilder>,
    ): ListTaskPushNotificationConfigRequestBuilder =
        apply {
            val builder = ListTaskPushNotificationConfigParamsBuilder()
            init.accept(builder)
            params = builder.build()
        }

    /**
     * Builds a [ListTaskPushNotificationConfigRequest] instance with the configured parameters.
     *
     * @return A new [ListTaskPushNotificationConfigRequest] instance.
     */
    public fun build(): ListTaskPushNotificationConfigRequest =
        ListTaskPushNotificationConfigRequest(
            id = id,
            params = params,
        )
}

/**
 * Top-level DSL function for creating [ListTaskPushNotificationConfigRequest].
 *
 * @param init The lambda to configure the list task push notification config request.
 * @return A new [ListTaskPushNotificationConfigRequest] instance.
 */
public inline fun listTaskPushNotificationConfigRequest(
    init: ListTaskPushNotificationConfigRequestBuilder.() -> Unit,
): ListTaskPushNotificationConfigRequest =
    ListTaskPushNotificationConfigRequestBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [ListTaskPushNotificationConfigRequest].
 *
 * @param init The consumer to configure the list task push notification config request.
 * @return A new [ListTaskPushNotificationConfigRequest] instance.
 */
public fun listTaskPushNotificationConfigRequest(
    init: Consumer<ListTaskPushNotificationConfigRequestBuilder>,
): ListTaskPushNotificationConfigRequest {
    val builder = ListTaskPushNotificationConfigRequestBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [ListTaskPushNotificationConfigRequest.Companion].
 *
 * @param init The lambda to configure the list task push notification config request.
 * @return A new [ListTaskPushNotificationConfigRequest] instance.
 */
public fun ListTaskPushNotificationConfigRequest.Companion.create(
    init: ListTaskPushNotificationConfigRequestBuilder.() -> Unit,
): ListTaskPushNotificationConfigRequest =
    ListTaskPushNotificationConfigRequestBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [ListTaskPushNotificationConfigRequest.Companion].
 *
 * @param init The consumer to configure the list task push notification config request.
 * @return A new [ListTaskPushNotificationConfigRequest] instance.
 */
public fun ListTaskPushNotificationConfigRequest.Companion.create(
    init: Consumer<ListTaskPushNotificationConfigRequestBuilder>,
): ListTaskPushNotificationConfigRequest {
    val builder = ListTaskPushNotificationConfigRequestBuilder()
    init.accept(builder)
    return builder.build()
}
