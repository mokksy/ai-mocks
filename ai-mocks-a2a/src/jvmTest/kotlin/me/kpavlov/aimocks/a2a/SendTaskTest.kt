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
import me.kpavlov.aimocks.a2a.model.Message
import me.kpavlov.aimocks.a2a.model.SendTaskRequest
import me.kpavlov.aimocks.a2a.model.SendTaskResponse
import me.kpavlov.aimocks.a2a.model.Task
import me.kpavlov.aimocks.a2a.model.create
import java.util.UUID
import kotlin.test.Test

internal class SendTaskTest : AbstractTest() {
    /**
     * https://github.com/google/A2A/blob/gh-pages/documentation.md#send-a-task
     */
    @Test
    @Suppress("LongMethod")
    fun `Should send task`() =
        runTest {
            val task =
                Task.create {
                    id = "tid_12345"
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
                            SendTaskRequest.create {
                                id = "1"
                                params {
                                    id = UUID.randomUUID().toString()
                                    message {
                                        role = Message.Role.user
                                        parts +=
                                            textPart {
                                                text = "Tell me a joke"
                                            }
                                    }
                                }
                            }
                        contentType(ContentType.Application.Json)
                        setBody(jsonRpcRequest)
                    }.call
                    .response

            response.status.shouldBeEqual(HttpStatusCode.OK)
            val payload = response.body<SendTaskResponse>()
            payload shouldBeEqualToComparingFields reply
        }
}
