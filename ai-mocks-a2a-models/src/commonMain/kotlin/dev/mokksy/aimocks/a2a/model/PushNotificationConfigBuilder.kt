package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder class for creating [PushNotificationConfig] instances.
 *
 * This builder provides a fluent API for creating PushNotificationConfig objects,
 * making it easier to configure push notification settings.
 *
 * Example usage:
 * ```
 * val config = PushNotificationConfig.build {
 *     url("https://example.org/notifications")
 *     token("auth-token")
 *     authentication {
 *         type = AuthenticationInfo.Type.bearer
 *         token = "bearer-token"
 *     }
 * }
 * ```
 */
public class PushNotificationConfigBuilder {
    public var url: String? = null
    public var token: String? = null
    public var authentication: AuthenticationInfo? = null

    /**
     * Sets the URL for push notifications.
     *
     * @param url The URL to send push notifications to.
     * @return This builder instance for method chaining.
     */
    public fun url(url: String): PushNotificationConfigBuilder =
        apply {
            this.url = url
        }

    /**
     * Sets the authentication token for push notifications.
     *
     * @param token The authentication token.
     * @return This builder instance for method chaining.
     */
    public fun token(token: String): PushNotificationConfigBuilder =
        apply {
            this.token = token
        }

    /**
     * Configures the authentication information using a lambda with receiver.
     *
     * @param block The lambda to configure the authentication information.
     */
    public fun authentication(block: AuthenticationInfoBuilder.() -> Unit) {
        authentication = AuthenticationInfoBuilder().apply(block).build()
    }

    /**
     * Configures the authentication information using a Java-friendly Consumer.
     *
     * @param block The consumer to configure the authentication information.
     */
    public fun authentication(block: Consumer<AuthenticationInfoBuilder>) {
        val builder = AuthenticationInfoBuilder()
        block.accept(builder)
        authentication = builder.build()
    }

    /**
     * Builds a [PushNotificationConfig] instance with the configured parameters.
     *
     * @return A new [PushNotificationConfig] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): PushNotificationConfig =
        PushNotificationConfig(
            url = requireNotNull(url) { "URL is required" },
            token = token,
            authentication = authentication,
        )
}

/**
 * Top-level DSL function for creating [PushNotificationConfig].
 *
 * @param init The lambda to configure the push notification config.
 * @return A new [PushNotificationConfig] instance.
 */
public inline fun pushNotificationConfig(
    init: PushNotificationConfigBuilder.() -> Unit,
): PushNotificationConfig = PushNotificationConfigBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [PushNotificationConfig].
 *
 * @param init The consumer to configure the push notification config.
 * @return A new [PushNotificationConfig] instance.
 */
public fun pushNotificationConfig(
    init: Consumer<PushNotificationConfigBuilder>,
): PushNotificationConfig {
    val builder = PushNotificationConfigBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new instance of a PushNotificationConfig using the provided configuration block.
 *
 * @param block A configuration block for building a PushNotificationConfig instance
 * using the PushNotificationConfigBuilder.
 * @return A newly created PushNotificationConfig instance.
 */
public fun PushNotificationConfig.Companion.create(
    block: PushNotificationConfigBuilder.() -> Unit,
): PushNotificationConfig = PushNotificationConfigBuilder().apply(block).build()

/**
 * Creates a new instance of a PushNotificationConfig using the provided Java-friendly Consumer.
 *
 * @param block A consumer for building a PushNotificationConfig instance using the PushNotificationConfigBuilder.
 * @return A newly created PushNotificationConfig instance.
 */
public fun PushNotificationConfig.Companion.create(
    block: Consumer<PushNotificationConfigBuilder>,
): PushNotificationConfig {
    val builder = PushNotificationConfigBuilder()
    block.accept(builder)
    return builder.build()
}
