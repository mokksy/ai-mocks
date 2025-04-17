package me.kpavlov.aimocks.a2a.model

/**
 * Builder class for creating [TaskSendParams] instances.
 *
 * This builder provides a fluent API for creating TaskSendParams objects,
 * making it easier to configure task send parameters.
 *
 * Example usage:
 * ```kotlin
 * val params = TaskSendParamsBuilder()
 *     .id("task-123")
 *     .sessionId("session-456")
 *     .message {
 *         role = Message.Role.user
 *         textPart("Hello, how can I help you?")
 *     }
 *     .pushNotification {
 *         url = "https://example.org/notifications"
 *         token = "auth-token"
 *     }
 *     .historyLength(10)
 *     .build()
 * ```
 */
public class TaskSendParamsBuilder {
    public var id: String? = null
    public var sessionId: String? = null
    public var message: Message? = null
    public var pushNotification: PushNotificationConfig? = null
    public var historyLength: Long? = null
    public var metadata: Metadata? = null

    /**
     * Configures the message using a DSL.
     *
     * @param init The lambda to configure the message.
     */
    public fun message(init: MessageBuilder.() -> Unit) {
        this.message = Message.build(init)
    }

    /**
     * Configures the push notification config using a DSL.
     *
     * @param init The lambda to configure the push notification config.
     */
    public fun pushNotification(init: PushNotificationConfigBuilder.() -> Unit) {
        this.pushNotification = PushNotificationConfig.build(init)
    }

    /**
     * Builds a [TaskSendParams] instance with the configured parameters.
     *
     * @return A new [TaskSendParams] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): TaskSendParams {
        requireNotNull(id) { "Task ID is required" }
        requireNotNull(message) { "Message is required" }

        return TaskSendParams(
            id = id!!,
            sessionId = sessionId,
            message = message!!,
            pushNotification = pushNotification,
            historyLength = historyLength,
            metadata = metadata,
        )
    }
}

/**
 * Creates a new instance of a TaskSendParams using the provided configuration block.
 *
 * @param block A configuration block for building a TaskSendParams instance using the TaskSendParamsBuilder.
 * @return A newly created TaskSendParams instance.
 */
public fun TaskSendParams.Companion.create(
    block: TaskSendParamsBuilder.() -> Unit,
): TaskSendParams = TaskSendParamsBuilder().apply(block).build()
