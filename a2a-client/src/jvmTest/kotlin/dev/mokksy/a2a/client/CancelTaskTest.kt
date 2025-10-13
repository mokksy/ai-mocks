package dev.mokksy.a2a.client

import dev.mokksy.aimocks.a2a.model.CancelTaskResponse
import dev.mokksy.aimocks.a2a.model.Task
import dev.mokksy.aimocks.a2a.model.TaskStatus
import dev.mokksy.aimocks.a2a.model.cancelTaskRequest
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import kotlinx.coroutines.test.runTest
import java.util.UUID
import kotlin.test.Test

internal class CancelTaskTest : AbstractTest() {
    /**
     * https://a2a-protocol.org/latest/specification/#74-taskscancel
     */
    @Test
    fun `Should cancel task`() =
        runTest {
            lateinit var expectedTask: Task

            a2aServer.cancelTask() responds {
                id = 1
                result {
                    id = "tid_12345"
                    contextId = UUID.randomUUID().toString()
                    status = TaskStatus(state = "canceled")
                }
                expectedTask = requireNotNull(result) { "Result should not be null" }
            }

            val jsonRpcRequest =
                cancelTaskRequest {
                    id = "1"
                    params {
                        id = UUID.randomUUID().toString()
                    }
                }

            val payload = client.cancelTask(jsonRpcRequest)

            val expectedReply =
                CancelTaskResponse(
                    id = 1,
                    result = expectedTask,
                )
            payload shouldBeEqualToComparingFields expectedReply
        }
}
