package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

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
 *     .create()
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
     * Sets the ID parameter for the task.
     *
     * @param id The unique identifier for the task.
     * @return The updated [TaskSendParamsBuilder] instance for chaining.
     */
    public fun id(id: String): TaskSendParamsBuilder =
        apply {
            this.id = id
        }

    /**
     * Sets the session ID parameter for the task.
     *
     * @param sessionId The session identifier associated with the task.
     * @return The updated [TaskSendParamsBuilder] instance for method chaining.
     */
    public fun sessionId(sessionId: String): TaskSendParamsBuilder =
        apply {
            this.sessionId = sessionId
        }

    /**
     * Configures the message for the task.
     *
     * @param init The lambda to configure the message using the [MessageBuilder].
     * @return The updated [TaskSendParamsBuilder] instance for method chaining.
     */
    public fun message(init: MessageBuilder.() -> Unit): TaskSendParamsBuilder =
        apply {
            this.message = Message.create(init)
        }

    /**
     * Configures the message for the task using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the message.
     * @return The updated [TaskSendParamsBuilder] instance for method chaining.
     */
    public fun message(init: Consumer<MessageBuilder>): TaskSendParamsBuilder =
        apply {
            val builder = MessageBuilder()
            init.accept(builder)
            this.message = builder.build()
        }

    /**
     * Configures the push notification config.
     *
     * @param init The lambda to configure the push notification config.
     * @return The updated [TaskSendParamsBuilder] instance for method chaining.
     */
    public fun pushNotification(
        init: PushNotificationConfigBuilder.() -> Unit,
    ): TaskSendParamsBuilder =
        apply {
            this.pushNotification = PushNotificationConfig.build(init)
        }

    /**
     * Configures the push notification config using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the push notification config.
     * @return The updated [TaskSendParamsBuilder] instance for method chaining.
     */
    public fun pushNotification(
        init: Consumer<PushNotificationConfigBuilder>,
    ): TaskSendParamsBuilder =
        apply {
            val builder = PushNotificationConfigBuilder()
            init.accept(builder)
            this.pushNotification = builder.build()
        }

    public fun historyLength(historyLength: Long): TaskSendParamsBuilder =
        apply {
            this.historyLength = historyLength
        }

    public fun metadata(metadata: Metadata): TaskSendParamsBuilder =
        apply {
            this.metadata = metadata
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

/**
 * Creates a new instance of a TaskSendParams using the provided Java-friendly Consumer.
 *
 * @param block A consumer for building a TaskSendParams instance using the TaskSendParamsBuilder.
 * @return A newly created TaskSendParams instance.
 */
public fun TaskSendParams.Companion.create(
    block: Consumer<TaskSendParamsBuilder>,
): TaskSendParams {
    val builder = TaskSendParamsBuilder()
    block.accept(builder)
    return builder.build()
}
