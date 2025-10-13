package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

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
 *     .create()
 * ```
 */
public class TaskPushNotificationConfigBuilder {
    public var id: String? = null
    public var pushNotificationConfig: PushNotificationConfig? = null

    /**
     * Sets the ID of the task.
     *
     * @param id The unique identifier for the task.
     * @return This builder instance for method chaining.
     */
    public fun id(id: String): TaskPushNotificationConfigBuilder =
        apply {
            this.id = id
        }

    /**
     * Configures the push notification config using a lambda with receiver.
     *
     * @param init The lambda to configure the push notification config.
     * @return This builder instance for method chaining.
     */
    public fun pushNotificationConfig(
        init: PushNotificationConfigBuilder.() -> Unit,
    ): TaskPushNotificationConfigBuilder =
        apply {
            this.pushNotificationConfig = PushNotificationConfig.create(init)
        }

    /**
     * Configures the push notification config using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the push notification config.
     * @return This builder instance for method chaining.
     */
    public fun pushNotificationConfig(
        init: Consumer<PushNotificationConfigBuilder>,
    ): TaskPushNotificationConfigBuilder =
        apply {
            val builder = PushNotificationConfigBuilder()
            init.accept(builder)
            this.pushNotificationConfig = builder.build()
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
 * Top-level DSL function for creating [TaskPushNotificationConfig].
 *
 * @param init The lambda to configure the task push notification config.
 * @return A new [TaskPushNotificationConfig] instance.
 */
public inline fun taskPushNotificationConfig(
    init: TaskPushNotificationConfigBuilder.() -> Unit,
): TaskPushNotificationConfig = TaskPushNotificationConfigBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [TaskPushNotificationConfig].
 *
 * @param init The consumer to configure the task push notification config.
 * @return A new [TaskPushNotificationConfig] instance.
 */
public fun taskPushNotificationConfig(
    init: Consumer<TaskPushNotificationConfigBuilder>,
): TaskPushNotificationConfig {
    val builder = TaskPushNotificationConfigBuilder()
    init.accept(builder)
    return builder.build()
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
): TaskPushNotificationConfig = TaskPushNotificationConfigBuilder().apply(block).build()

/**
 * Creates a new instance of a TaskPushNotificationConfig using the provided Java-friendly Consumer.
 *
 * @param block A consumer for building a TaskPushNotificationConfig instance
 * using the TaskPushNotificationConfigBuilder.
 * @return A newly created TaskPushNotificationConfig instance.
 */
public fun TaskPushNotificationConfig.Companion.create(
    block: Consumer<TaskPushNotificationConfigBuilder>,
): TaskPushNotificationConfig {
    val builder = TaskPushNotificationConfigBuilder()
    block.accept(builder)
    return builder.build()
}
