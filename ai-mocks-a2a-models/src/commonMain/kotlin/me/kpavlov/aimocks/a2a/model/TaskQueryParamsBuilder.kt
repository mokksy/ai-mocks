package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [TaskQueryParams].
 *
 * Example usage:
 * ```
 * val params = taskQueryParams {
 *     id = "task-123"
 *     historyLength = 10
 *     metadata = myMetadata
 * }
 * ```
 */
public class TaskQueryParamsBuilder {
    public var id: String? = null
    public var historyLength: Long? = null
    public var metadata: Metadata? = null

    /**
     * Sets the ID of the task.
     *
     * @param id The ID of the task.
     * @return This builder instance for method chaining.
     */
    public fun id(id: String): TaskQueryParamsBuilder =
        apply {
            this.id = id
        }

    /**
     * Sets the history length.
     *
     * @param historyLength The history length.
     * @return This builder instance for method chaining.
     */
    public fun historyLength(historyLength: Long): TaskQueryParamsBuilder =
        apply {
            this.historyLength = historyLength
        }

    /**
     * Sets the metadata.
     *
     * @param metadata The metadata.
     * @return This builder instance for method chaining.
     */
    public fun metadata(metadata: Metadata): TaskQueryParamsBuilder =
        apply {
            this.metadata = metadata
        }

    /**
     * Builds a [TaskQueryParams] instance with the configured parameters.
     *
     * @return A new [TaskQueryParams] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): TaskQueryParams =
        TaskQueryParams(
            id = requireNotNull(id) { "TaskQueryParams.id must be provided" },
            historyLength = historyLength,
            metadata = metadata,
        )
}

/**
 * Top-level DSL function for creating [TaskQueryParams].
 *
 * @param init The lambda to configure the task query params.
 * @return A new [TaskQueryParams] instance.
 */
public inline fun taskQueryParams(init: TaskQueryParamsBuilder.() -> Unit): TaskQueryParams =
    TaskQueryParamsBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [TaskQueryParams].
 *
 * @param init The consumer to configure the task query params.
 * @return A new [TaskQueryParams] instance.
 */
public fun taskQueryParams(init: Consumer<TaskQueryParamsBuilder>): TaskQueryParams {
    val builder = TaskQueryParamsBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [TaskQueryParams.Companion].
 *
 * @param init The lambda to configure the task query params.
 * @return A new [TaskQueryParams] instance.
 */
public fun TaskQueryParams.Companion.create(init: TaskQueryParamsBuilder.() -> Unit): TaskQueryParams =
    TaskQueryParamsBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [TaskQueryParams.Companion].
 *
 * @param init The consumer to configure the task query params.
 * @return A new [TaskQueryParams] instance.
 */
public fun TaskQueryParams.Companion.create(init: Consumer<TaskQueryParamsBuilder>): TaskQueryParams {
    val builder = TaskQueryParamsBuilder()
    init.accept(builder)
    return builder.build()
}
