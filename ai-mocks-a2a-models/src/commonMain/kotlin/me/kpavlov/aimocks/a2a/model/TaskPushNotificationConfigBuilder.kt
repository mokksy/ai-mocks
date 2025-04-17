package me.kpavlov.aimocks.a2a.model

/**
 * Builder class for creating [TaskPushNotificationConfig] instances.
 *
 * This builder provides a fluent API for creating TaskPushNotificationConfig objects,
 * making it easier to configure task push notification settings.
 *
 * Example usage:
 * ```kotlin
 * val config = TaskPushNotificationConfigBuilder()
 *     .id("task-123")
 *     .pushNotificationConfig {
 *         url = "https://example.org/notifications"
 *         token = "auth-token"
 *     }
 *     .build()
 * ```
 */
public class TaskPushNotificationConfigBuilder {
    public var id: String? = null
    public var pushNotificationConfig: PushNotificationConfig? = null

    /**
     * Configures the push notification config using a DSL.
     *
     * @param init The lambda to configure the push notification config.
     */
    public fun pushNotificationConfig(init: PushNotificationConfigBuilder.() -> Unit) {
        this.pushNotificationConfig = PushNotificationConfig.build(init)
    }

    /**
     * Builds a [TaskPushNotificationConfig] instance with the configured parameters.
     *
     * @return A new [TaskPushNotificationConfig] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): TaskPushNotificationConfig {
        requireNotNull(id) { "Task ID is required" }
        requireNotNull(pushNotificationConfig) { "Push notification config is required" }

        return TaskPushNotificationConfig(
            id = id!!,
            pushNotificationConfig = pushNotificationConfig!!,
        )
    }
}

/**
 * Creates a new instance of a TaskPushNotificationConfig using the provided configuration block.
 *
 * @param block A configuration block for building a TaskPushNotificationConfig instance
 * using the TaskPushNotificationConfigBuilder.
 * @return A newly created TaskPushNotificationConfig instance.
 */
public fun TaskPushNotificationConfig.Companion.create(
    block: TaskPushNotificationConfigBuilder.() -> Unit,
): TaskPushNotificationConfig = build(block)
