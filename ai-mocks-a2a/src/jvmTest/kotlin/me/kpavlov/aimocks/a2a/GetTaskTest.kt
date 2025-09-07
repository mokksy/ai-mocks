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
import me.kpavlov.aimocks.a2a.model.GetTaskResponse
import me.kpavlov.aimocks.a2a.model.getTaskRequest
import me.kpavlov.aimocks.a2a.model.getTaskResponse
import me.kpavlov.aimocks.a2a.model.taskNotFoundError
import java.util.UUID
import kotlin.test.Test

internal class GetTaskTest : AbstractTest() {
    /**
     * https://a2a-protocol.org/latest/specification/#73-tasksget
     */
    @Test
    fun `Should get task`() =
        runTest {
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
            }

            val response =
                a2aClient
                    .post("/") {
                        val jsonRpcRequest =
                            getTaskRequest {
                                id = "1"
                                params {
                                    id = UUID.randomUUID().toString()
                                    historyLength = 2
                                }
                            }
                        contentType(ContentType.Application.Json)
                        setBody(Json.encodeToString(jsonRpcRequest))
                    }.call
                    .response

            response.status.shouldBeEqual(HttpStatusCode.OK)
            val body = response.body<String>()
            logger.info { "body = $body" }
            val payload = Json.decodeFromString<GetTaskResponse>(body)

            val expectedReply =
                getTaskResponse {
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
                }
            payload shouldBeEqualToComparingFields expectedReply
        }

    @Test
    fun `Should fail to get task`() =
        runTest {
            a2aServer.getTask() responds {
                id = 1
                error =
                    taskNotFoundError {
                        message = "Task not found"
                    }
            }

            val response =
                a2aClient
                    .post("/") {
                        val jsonRpcRequest =
                            getTaskRequest {
                                id = "1"
                                params {
                                    id = UUID.randomUUID().toString()
                                    historyLength = 2
                                }
                            }
                        contentType(ContentType.Application.Json)
                        setBody(Json.encodeToString(jsonRpcRequest))
                    }.call
                    .response

            response.status.shouldBeEqual(HttpStatusCode.OK)
            val body = response.body<String>()
            logger.info { "body = $body" }
            val payload = Json.decodeFromString<GetTaskResponse>(body)

            val expectedReply =
                getTaskResponse {
                    id = 1
                    error =
                        taskNotFoundError {
                            message = "Task not found"
                        }
                }
            payload shouldBeEqualToComparingFields expectedReply
        }
}
