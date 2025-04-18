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
import kotlinx.serialization.json.Json
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
                a2aClient
                    .post("/") {
                        val jsonRpcRequest =
                            GetTaskRequest(
                                id = "1",
                                params =
                                    TaskQueryParams(
                                        id = UUID.randomUUID().toString(),
                                        historyLength = 2,
                                    ),
                            )
                        contentType(ContentType.Application.Json)
                        setBody(Json.encodeToString(jsonRpcRequest))
                    }.call
                    .response

            response.status.shouldBeEqual(HttpStatusCode.OK)
            val body = response.body<String>()
            logger.info { "body = $body" }
            val payload = Json.decodeFromString<GetTaskResponse>(body)

            val expectedReply =
                GetTaskResponse(
                    id = 1,
                    result = expectedTask,
                )
            payload shouldBeEqualToComparingFields expectedReply
        }
}
