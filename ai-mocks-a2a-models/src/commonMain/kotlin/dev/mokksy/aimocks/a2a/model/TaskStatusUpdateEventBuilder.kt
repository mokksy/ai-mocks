package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [TaskStatusUpdateEvent].
 *
 * Example usage:
 * ```
 * val event = taskStatusUpdateEvent {
 *     id = myTaskId
 *     status = TaskStatus.COMPLETED
 *     final = true
 *     metadata = someMetadata
 * }
 * ```
 */
public class TaskStatusUpdateEventBuilder {
    public var id: TaskId? = null
    public var status: TaskStatus? = null
    public var final: Boolean = false
    public var metadata: Metadata? = null

    /**
     * Sets the task ID.
     *
     * @param id The task ID.
     * @return This builder instance for method chaining.
     */
    public fun id(id: TaskId): TaskStatusUpdateEventBuilder =
        apply {
            this.id = id
        }

    /**
     * Sets the final status flag.
     *
     * @param isFinal Whether this is the final status update.
     * @return This builder instance for method chaining.
     */
    public fun isFinal(isFinal: Boolean): TaskStatusUpdateEventBuilder =
        apply {
            this.final = isFinal
        }

    /**
     * Sets the task status directly.
     *
     * @param status The task status.
     * @return This builder instance for method chaining.
     */
    public fun status(status: TaskStatus): TaskStatusUpdateEventBuilder =
        apply {
            this.status = status
        }

    /**
     * Configures the task status using a DSL.
     *
     * @param block The lambda to configure the task status.
     * @return This builder instance for method chaining.
     */
    public fun status(block: TaskStatusBuilder.() -> Unit): TaskStatusUpdateEventBuilder =
        apply {
            status = TaskStatusBuilder().apply(block).build()
        }

    /**
     * Configures the task status using a Java-friendly Consumer.
     *
     * @param block The consumer to configure the task status.
     * @return This builder instance for method chaining.
     */
    public fun status(block: Consumer<TaskStatusBuilder>): TaskStatusUpdateEventBuilder =
        apply {
            val builder = TaskStatusBuilder()
            block.accept(builder)
            status = builder.build()
        }

    /**
     * Sets the metadata.
     *
     * @param metadata The metadata.
     * @return This builder instance for method chaining.
     */
    public fun metadata(metadata: Metadata): TaskStatusUpdateEventBuilder =
        apply {
            this.metadata = metadata
        }

    public fun build(): TaskStatusUpdateEvent =
        TaskStatusUpdateEvent(
            id = requireNotNull(id) { "TaskStatusUpdateEvent.id must be provided" },
            status = requireNotNull(status) { "TaskStatusUpdateEvent.status must be provided" },
            final = final,
            metadata = metadata,
        )
}

/**
 * Top-level DSL function for creating [TaskStatusUpdateEvent].
 */
public inline fun taskStatusUpdateEvent(
    init: TaskStatusUpdateEventBuilder.() -> Unit,
): TaskStatusUpdateEvent = TaskStatusUpdateEventBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [TaskStatusUpdateEvent].
 */
public fun taskStatusUpdateEvent(
    init: Consumer<TaskStatusUpdateEventBuilder>,
): TaskStatusUpdateEvent {
    val builder = TaskStatusUpdateEventBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [TaskStatusUpdateEvent].
 */
public fun TaskStatusUpdateEvent.Companion.create(
    init: TaskStatusUpdateEventBuilder.() -> Unit,
): TaskStatusUpdateEvent = TaskStatusUpdateEventBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [TaskStatusUpdateEvent].
 */
public fun TaskStatusUpdateEvent.Companion.create(
    init: Consumer<TaskStatusUpdateEventBuilder>,
): TaskStatusUpdateEvent {
    val builder = TaskStatusUpdateEventBuilder()
    init.accept(builder)
    return builder.build()
}
