package me.kpavlov.aimocks.a2a.model

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

    public fun status(block: TaskStatusBuilder.() -> Unit) {
        TaskStatusBuilder().apply(block).build().also { status = it }
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
 * DSL extension for [TaskStatusUpdateEvent.Companion].
 */
public fun TaskStatusUpdateEvent.Companion.create(
    init: TaskStatusUpdateEventBuilder.() -> Unit,
): TaskStatusUpdateEvent = TaskStatusUpdateEventBuilder().apply(init).build()
