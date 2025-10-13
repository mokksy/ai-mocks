package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.CancelTaskRequest
import dev.mokksy.aimocks.a2a.model.CancelTaskResponse
import dev.mokksy.aimocks.a2a.model.JSONRPCError
import dev.mokksy.aimocks.a2a.model.RequestId
import dev.mokksy.aimocks.a2a.model.Task
import dev.mokksy.aimocks.a2a.model.TaskBuilder
import dev.mokksy.aimocks.core.AbstractResponseSpecification
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

public class CancelTaskResponseSpecification(
    response: AbstractResponseDefinition<CancelTaskResponse>,
    public var id: RequestId? = null,
    public var result: Task? = null,
    public var error: JSONRPCError? = null,
    delay: Duration = Duration.ZERO,
) : AbstractResponseSpecification<CancelTaskRequest, CancelTaskResponse>(
        response = response,
        delay = delay,
    ) {
    public fun task(block: TaskBuilder.() -> Unit) {
        require(result == null) { "Task is already defined" }
        result = TaskBuilder().apply(block).build()
    }

    public fun result(block: TaskBuilder.() -> Unit) {
        task { block.invoke(this) }
    }
}
