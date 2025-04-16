package me.kpavlov.aimocks.a2a.model

import kotlinx.datetime.Instant

/**
 * Builder class for creating [TaskStatus] instances.
 *
 * This builder provides a fluent API for creating TaskStatus objects,
 * making it easier to construct complex task statuses with many parameters.
 *
 * Example usage:
 * ```kotlin
 * val taskStatus = TaskStatusBuilder()
 *     .state("completed")
 *     .message(messageBuilder.build())
 *     .timestamp(Instant.parse("2023-01-01T00:00:00Z"))
 *     .build()
 * ```
 */
public class TaskStatusBuilder {
    private var state: String? = null
    private var message: Message? = null
    private var timestamp: Instant? = null

    /**
     * Sets the state of the task status.
     *
     * @param state The state of the task. Must be one of: "submitted", "working", "input-required",
     *              "completed", "canceled", "failed", "unknown".
     * @return This builder instance for method chaining.
     */
    public fun state(state: String): TaskStatusBuilder {
        this.state = state
        return this
    }

    /**
     * Sets the message of the task status.
     *
     * @param message The message associated with the task status.
     * @return This builder instance for method chaining.
     */
    public fun message(message: Message): TaskStatusBuilder {
        this.message = message
        return this
    }

    /**
     * Sets the timestamp of the task status.
     *
     * @param timestamp The timestamp when the status was updated.
     * @return This builder instance for method chaining.
     */
    public fun timestamp(timestamp: Instant): TaskStatusBuilder {
        this.timestamp = timestamp
        return this
    }

    /**
     * Builds a [TaskStatus] instance with the configured parameters.
     *
     * @return A new [TaskStatus] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): TaskStatus {
        requireNotNull(state) { "State is required" }

        return TaskStatus(
            state = state!!,
            message = message,
            timestamp = timestamp,
        )
    }
}
