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
import me.kpavlov.aimocks.a2a.model.Artifact
import me.kpavlov.aimocks.a2a.model.Message
import me.kpavlov.aimocks.a2a.model.SendTaskRequest
import me.kpavlov.aimocks.a2a.model.SendTaskResponse
import me.kpavlov.aimocks.a2a.model.Task
import me.kpavlov.aimocks.a2a.model.TaskSendParams
import me.kpavlov.aimocks.a2a.model.TaskStatus
import me.kpavlov.aimocks.a2a.model.TextPart
import java.util.UUID
import kotlin.test.Test

internal class SendTaskTest : AbstractTest() {
    /**
     * https://github.com/google/A2A/blob/gh-pages/documentation.md#send-a-task
     */
    @Test
    fun `Should send task`() =
        runTest {
            val task =
                Task(
                    id = "tid_12345",
                    sessionId = null,
                    status = TaskStatus(state = "completed"),
                    artifacts =
                        listOf(
                            Artifact(
                                name = "joke",
                                parts =
                                    listOf(
                                        TextPart(
                                            text = "This is a joke",
                                        ),
                                    ),
                            ),
                        ),
                )
            val reply =
                SendTaskResponse(
                    id = 1,
                    result = task,
                )

            a2aServer.sendTask() responds {
                id = 1
                result = task
            }

            val response =
                a2aClient
                    .post("/") {
                        val jsonRpcRequest =
                            SendTaskRequest(
                                id = "1",
                                params =
                                    TaskSendParams(
                                        id = UUID.randomUUID().toString(),
                                        message =
                                            Message(
                                                role = Message.Role.user,
                                                parts =
                                                    listOf(
                                                        TextPart(
                                                            text = "Tell me a joke",
                                                        ),
                                                    ),
                                            ),
                                    ),
                            )
                        contentType(ContentType.Application.Json)
                        setBody(Json.encodeToString(jsonRpcRequest))
                    }.call
                    .response

            response.status.shouldBeEqual(HttpStatusCode.OK)
            val body = response.body<String>()
            logger.info { "body = $body" }
            val payload = Json.decodeFromString<SendTaskResponse>(body)
            payload shouldBeEqualToComparingFields reply
        }
}
