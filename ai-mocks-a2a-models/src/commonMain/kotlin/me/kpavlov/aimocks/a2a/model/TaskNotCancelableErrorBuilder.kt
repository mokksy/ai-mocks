package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [TaskNotCancelableError].
 *
 * Example usage:
 * ```
 * val error = taskNotCancelableError {
 *     data = myErrorData
 * }
 * ```
 */
public class TaskNotCancelableErrorBuilder :
    JSONRPCErrorBuilder<TaskNotCancelableError, TaskNotCancelableErrorBuilder>() {

    /**
     * Builds a [TaskNotCancelableError] instance with the configured parameters.
     *
     * @return A new [TaskNotCancelableError] instance.
     */
    public override fun build(): TaskNotCancelableError =
        TaskNotCancelableError(
            data = data,
        )
}

/**
 * Top-level DSL function for creating [TaskNotCancelableError].
 *
 * @param init The lambda to configure the task not cancelable error.
 * @return A new [TaskNotCancelableError] instance.
 */
public inline fun taskNotCancelableError(init: TaskNotCancelableErrorBuilder.() -> Unit): TaskNotCancelableError =
    TaskNotCancelableErrorBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [TaskNotCancelableError].
 *
 * @param init The consumer to configure the task not cancelable error.
 * @return A new [TaskNotCancelableError] instance.
 */
public fun taskNotCancelableError(init: Consumer<TaskNotCancelableErrorBuilder>): TaskNotCancelableError {
    val builder = TaskNotCancelableErrorBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [TaskNotCancelableError.Companion].
 *
 * @param init The lambda to configure the task not cancelable error.
 * @return A new [TaskNotCancelableError] instance.
 */
public fun TaskNotCancelableError.Companion.create(
    init: TaskNotCancelableErrorBuilder.() -> Unit,
): TaskNotCancelableError = TaskNotCancelableErrorBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [TaskNotCancelableError.Companion].
 *
 * @param init The consumer to configure the task not cancelable error.
 * @return A new [TaskNotCancelableError] instance.
 */
public fun TaskNotCancelableError.Companion.create(
    init: Consumer<TaskNotCancelableErrorBuilder>,
): TaskNotCancelableError {
    val builder = TaskNotCancelableErrorBuilder()
    init.accept(builder)
    return builder.build()
}
