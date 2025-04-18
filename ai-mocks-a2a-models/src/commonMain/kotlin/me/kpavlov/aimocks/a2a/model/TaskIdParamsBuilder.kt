package me.kpavlov.aimocks.a2a.model

/**
 * DSL builder for [TaskIdParams].
 *
 * Example usage:
 * ```
 * val params = taskIdParams {
 *     id = someTaskId
 *     metadata = someMetadata // optional
 * }
 * ```
 */
public class TaskIdParamsBuilder {
    public var id: TaskId? = null
    public var metadata: Metadata? = null

    public fun build(): TaskIdParams =
        TaskIdParams(
            id = requireNotNull(id) { "TaskIdParams.id must be provided" },
            metadata = metadata,
        )
}

/**
 * Top-level DSL function for building [TaskIdParams].
 */
public inline fun taskIdParams(init: TaskIdParamsBuilder.() -> Unit): TaskIdParams =
    TaskIdParamsBuilder().apply(init).build()

/**
 * DSL extension for [TaskIdParams.Companion].
 */
public fun TaskIdParams.Companion.create(init: TaskIdParamsBuilder.() -> Unit): TaskIdParams =
    TaskIdParamsBuilder().apply(init).build()
