package me.kpavlov.aimocks.a2a.model

/**
 * Builder class for creating [PushNotificationConfig] instances.
 *
 * This builder provides a fluent API for creating PushNotificationConfig objects,
 * making it easier to configure push notification settings.
 *
 * Example usage:
 * ```kotlin
 * val config = PushNotificationConfigBuilder()
 *     .url("https://example.org/notifications")
 *     .token("auth-token")
 *     .build()
 * ```
 */
public class PushNotificationConfigBuilder {
    public var url: String? = null
    public var token: String? = null
    public var authentication: AuthenticationInfo? = null

    /**
     * Builds a [PushNotificationConfig] instance with the configured parameters.
     *
     * @return A new [PushNotificationConfig] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): PushNotificationConfig {
        requireNotNull(url) { "URL is required" }

        return PushNotificationConfig(
            url = url!!,
            token = token,
            authentication = authentication,
        )
    }
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
): PushNotificationConfig = build(block)
