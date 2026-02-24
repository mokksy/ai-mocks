package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.GetTaskResponse
import dev.mokksy.aimocks.a2a.model.getTaskRequest
import dev.mokksy.aimocks.a2a.model.getTaskResponse
import dev.mokksy.aimocks.a2a.model.taskNotFoundError
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.equals.shouldBeEqual
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.util.UUID

internal class GetTaskIT : AbstractIT() {
    /**
     * https://a2a-protocol.org/latest/specification/#73-tasksget
     */
    @Test
    suspend fun `Should get task`() {
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
            }
        payload shouldBeEqualToComparingFields expectedReply
    }

    @Test
    suspend fun `Should fail to get task`() {
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
