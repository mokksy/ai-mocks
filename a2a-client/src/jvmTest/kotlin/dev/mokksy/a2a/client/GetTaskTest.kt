package dev.mokksy.a2a.client

import dev.mokksy.aimocks.a2a.model.GetTaskRequest
import dev.mokksy.aimocks.a2a.model.GetTaskResponse
import dev.mokksy.aimocks.a2a.model.Task
import dev.mokksy.aimocks.a2a.model.TaskQueryParams
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import kotlinx.coroutines.test.runTest
import java.util.UUID
import kotlin.test.Test

internal class GetTaskTest : AbstractTest() {
    /**
     * https://a2a-protocol.org/latest/specification/#73-tasksget
     */
    @Test
    fun `Should get task`() =
        runTest {
            lateinit var expectedTask: Task

            a2aServer.getTask() responds {
                id = 1
                result {
                    id = "tid_12345"
                    contextId = "ctx_12345"
                    status {
                        state = "completed"
                    }
                    artifacts +=
                        artifact {
                            name = "joke"
                            parts +=
                                textPart {
                                    text = "This is a joke"
                                }
                        }
                }
                expectedTask = requireNotNull(result) { "Result should not be null" }
            }

            val response =
                client.getTask(
                    GetTaskRequest(
                        id = "1",
                        params =
                            TaskQueryParams(
                                id = UUID.randomUUID().toString(),
                                historyLength = 2,
                            ),
                    ),
                )

            val expectedReply =
                GetTaskResponse(
                    id = 1,
                    result = expectedTask,
                )
            response shouldBeEqualToComparingFields expectedReply
        }

    @Test
    fun `Should get task using id and historyLength parameters`() =
        runTest {
            lateinit var expectedTask: Task

            a2aServer.getTask() responds {
                id = 1
                result {
                    id = "tid_12345"
                    contextId = "ctx_12345"
                    status {
                        state = "completed"
                    }
                    artifacts +=
                        artifact {
                            name = "joke"
                            parts +=
                                textPart {
                                    text = "This is a joke"
                                }
                        }
                }
                expectedTask = requireNotNull(result) { "Result should not be null" }
            }

            // Call the overload that takes id and historyLength parameters
            val taskId = UUID.randomUUID().toString()
            val historyLength = 3
            val response = client.getTask(taskId, historyLength)

            val expectedReply =
                GetTaskResponse(
                    id = 1,
                    result = expectedTask,
                )
            response shouldBeEqualToComparingFields expectedReply
        }
}
