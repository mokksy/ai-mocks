package me.kpavlov.aimocks.a2a

import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.test.runTest
import me.kpavlov.aimocks.a2a.model.Message
import me.kpavlov.aimocks.a2a.model.SendMessageResponse
import me.kpavlov.aimocks.a2a.model.Task
import me.kpavlov.aimocks.a2a.model.create
import me.kpavlov.aimocks.a2a.model.invalidRequestError
import me.kpavlov.aimocks.a2a.model.sendMessageRequest
import java.util.UUID
import kotlin.test.Test

internal class SendMessageTest : AbstractTest() {
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
                    artifact {
                        name = "joke"
                        parts += text { "This is a joke" }
                    }
                }

            val reply =
                SendMessageResponse.create {
                    id = 1
                    result {
                        id = "tid_12345"
                        status {
                            state = "completed"
                        }
                        artifact {
                            name = "joke"
                            parts += text { "This is a joke" }
                            parts += file { uri = "https://example.com/readme.md" }
                            parts += file { bytes = "1234".toByteArray() }
                            parts += data { mapOf("foo" to "bar") }
                        }
                    }
                }

            a2aServer.sendTask() responds {
                id = 1
                result = task
            }

            val response =
                a2aClient
                    .post("/") {
                        val jsonRpcRequest =
                            sendMessageRequest {
                                id = "1"
                                params {
                                    id = UUID.randomUUID().toString()
                                    message {
                                        role = Message.Role.user
                                        parts += text { "Tell me a joke" }
                                        parts += file { uri = "https://example.com/readme.md" }
                                        parts += file { bytes = "1234".toByteArray() }
                                        parts += data { mapOf("foo" to "bar") }
                                    }
                                }
                            }
                        contentType(ContentType.Application.Json)
                        setBody(jsonRpcRequest)
                    }.call
                    .response

            response.status shouldBe HttpStatusCode.OK
            val payload = response.body<SendMessageResponse>()
            payload shouldBeEqualToComparingFields reply
        }

    @Test
    fun `Should fail to send task`() =
        runTest {
            a2aServer.sendTask() responds {
                id = 1
                error =
                    invalidRequestError {
                        message = "Invalid request"
                    }
            }

            val response =
                a2aClient
                    .post("/") {
                        val jsonRpcRequest =
                            sendMessageRequest {
                                id = "1"
                                params {
                                    id = UUID.randomUUID().toString()
                                    message {
                                        role = Message.Role.user
                                        parts += text { "Tell me a joke" }
                                    }
                                }
                            }
                        contentType(ContentType.Application.Json)
                        setBody(jsonRpcRequest)
                    }.call
                    .response

            response.status shouldBe HttpStatusCode.OK
            val payload = response.body<SendMessageResponse>()

            val expectedReply =
                SendMessageResponse.create {
                    id = 1
                    error =
                        invalidRequestError {
                            message = "Invalid request"
                        }
                }
            payload shouldBeEqualToComparingFields expectedReply
        }
}
