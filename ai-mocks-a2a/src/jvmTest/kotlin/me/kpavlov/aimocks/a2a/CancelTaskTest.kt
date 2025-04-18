package me.kpavlov.aimocks.a2a

import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.equals.shouldBeEqual
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.test.runTest
import me.kpavlov.aimocks.a2a.model.CancelTaskResponse
import me.kpavlov.aimocks.a2a.model.Task
import me.kpavlov.aimocks.a2a.model.TaskStatus
import me.kpavlov.aimocks.a2a.model.cancelTaskRequest
import java.util.UUID
import kotlin.test.Test

internal class CancelTaskTest : AbstractTest() {
    /**
     * https://github.com/google/A2A/blob/gh-pages/documentation.md#send-a-task
     */
    @Test
    fun `Should cancel task`() =
        runTest {
            lateinit var expectedTask: Task

            a2aServer.cancelTask() responds {
                id = 1
                result {
                    id = "tid_12345"
                    sessionId = UUID.randomUUID().toString()
                    status = TaskStatus(state = "canceled")
                }
                expectedTask = requireNotNull(result) { "Result should not be null" }
            }

            val response =
                a2aClient
                    .post("/") {
                        val jsonRpcRequest =
                            cancelTaskRequest {
                                id = "1"
                                params {
                                    id = UUID.randomUUID().toString()
                                }
                            }
                        contentType(ContentType.Application.Json)
                        setBody(jsonRpcRequest)
                    }.call
                    .response

            response.status.shouldBeEqual(HttpStatusCode.OK)
            val payload = response.body<CancelTaskResponse>()

            val expectedReply =
                CancelTaskResponse(
                    id = 1,
                    result = expectedTask,
                )
            payload shouldBeEqualToComparingFields expectedReply
        }
}
