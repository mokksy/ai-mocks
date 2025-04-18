package me.kpavlov.a2a.client

import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import kotlinx.coroutines.test.runTest
import me.kpavlov.aimocks.a2a.model.GetTaskRequest
import me.kpavlov.aimocks.a2a.model.GetTaskResponse
import me.kpavlov.aimocks.a2a.model.Task
import me.kpavlov.aimocks.a2a.model.TaskQueryParams
import java.util.UUID
import kotlin.test.Test

internal class GetTaskTest : AbstractTest() {
    /**
     * https://github.com/google/A2A/blob/gh-pages/documentation.md#send-a-task
     */
    @Test
    fun `Should get task`() =
        runTest {
            lateinit var expectedTask: Task

            a2aServer.getTask() responds {
                id = 1
                result {
                    id = "tid_12345"
                    sessionId = null
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
}
