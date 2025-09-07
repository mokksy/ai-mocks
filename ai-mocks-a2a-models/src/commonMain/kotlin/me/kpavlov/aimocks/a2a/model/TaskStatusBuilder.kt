package me.kpavlov.aimocks.a2a.model

import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import java.util.function.Consumer

/**
 * Builder class for creating [TaskStatus] instances.
 *
 * This builder provides a fluent API for creating TaskStatus objects,
 * making it easier to construct complex task statuses with many parameters.
 *
 * Example usage:
 * ```
 * val status = TaskStatus.create {
 *     state = TaskState.working
 *     timestamp = System.currentTimeMillis()
 *     message {
 *         role = Message.Role.agent
 *         textPart("Processing your request...")
 *     }
 * }
 * ```
 */
public class TaskStatusBuilder {
    public var state: String? = null
    public var message: Message? = null
    public var timestamp: Instant? = null

    /**
     * Sets the state of the task status.
     *
     * @param state The state of the task. Must be one of: "submitted", "working", "input-required",
     *              "completed", "canceled", "failed", "unknown".
     * @return This builder instance for method chaining.
     */
    public fun state(state: String): TaskStatusBuilder =
        apply {
            this.state = state
        }

    public fun state(state: TaskState): TaskStatusBuilder =
        apply {
            this.state = state.value
        }

    /**
     * Sets the message of the task status.
     *
     * @param message The message associated with the task status.
     * @return This builder instance for method chaining.
     */
    public fun message(message: Message): TaskStatusBuilder =
        apply {
            this.message = message
        }

    /**
     * Sets the timestamp of the task status.
     *
     * @param timestamp The timestamp when the status was updated.
     * @return This builder instance for method chaining.
     */
    public fun timestamp(timestamp: Instant): TaskStatusBuilder =
        apply {
            this.timestamp = timestamp
        }

    public fun timestamp(timestamp: java.time.Instant): TaskStatusBuilder =
        apply {
            this.timestamp = timestamp.toKotlinInstant()
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

/**
 * Top-level DSL function for creating [TaskStatus].
 *
 * @param init The lambda to configure the task status.
 * @return A new [TaskStatus] instance.
 */
public inline fun taskStatus(init: TaskStatusBuilder.() -> Unit): TaskStatus =
    TaskStatusBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [TaskStatus].
 *
 * @param init The consumer to configure the task status.
 * @return A new [TaskStatus] instance.
 */
public fun taskStatus(init: Consumer<TaskStatusBuilder>): TaskStatus {
    val builder = TaskStatusBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [TaskStatus.Companion].
 *
 * @param init The lambda to configure the task status.
 * @return A new [TaskStatus] instance.
 */
public fun TaskStatus.Companion.create(init: TaskStatusBuilder.() -> Unit): TaskStatus =
    TaskStatusBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [TaskStatus.Companion].
 *
 * @param init The consumer to configure the task status.
 * @return A new [TaskStatus] instance.
 */
public fun TaskStatus.Companion.create(init: Consumer<TaskStatusBuilder>): TaskStatus {
    val builder = TaskStatusBuilder()
    init.accept(builder)
    return builder.build()
}
