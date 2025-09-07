package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [TaskNotFoundError].
 *
 * Example usage:
 * ```
 * val error = taskNotFoundError {
 *     data = myErrorData
 * }
 * ```
 */
public class TaskNotFoundErrorBuilder :
    JSONRPCErrorBuilder<TaskNotFoundError, TaskNotFoundErrorBuilder>() {
    /**
     * Builds a [TaskNotFoundError] instance with the configured parameters.
     *
     * @return A new [TaskNotFoundError] instance.
     */
    public override fun build(): TaskNotFoundError =
        TaskNotFoundError(
            data = data,
        )
}

/**
 * Top-level DSL function for creating [TaskNotFoundError].
 *
 * @param init The lambda to configure the task not found error.
 * @return A new [TaskNotFoundError] instance.
 */
public inline fun taskNotFoundError(init: TaskNotFoundErrorBuilder.() -> Unit): TaskNotFoundError =
    TaskNotFoundErrorBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [TaskNotFoundError].
 *
 * @param init The consumer to configure the task not found error.
 * @return A new [TaskNotFoundError] instance.
 */
public fun taskNotFoundError(init: Consumer<TaskNotFoundErrorBuilder>): TaskNotFoundError {
    val builder = TaskNotFoundErrorBuilder()
    init.accept(builder)
    return builder.build()
}
