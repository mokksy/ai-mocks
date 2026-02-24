package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.CancelTaskResponse
import dev.mokksy.aimocks.a2a.model.cancelTaskRequest
import dev.mokksy.aimocks.a2a.model.cancelTaskResponse
import dev.mokksy.aimocks.a2a.model.internalError
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.equals.shouldBeEqual
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.junit.jupiter.api.Test
import java.util.UUID

internal class CancelTaskIT : AbstractIT() {
    /**
     * https://a2a-protocol.org/latest/specification/#74-taskscancel
     */
    @Test
    suspend fun `Should cancel task`() {
        val contextId = UUID.randomUUID().toString()

        a2aServer.cancelTask() responds {
            id = 1
            result {
                id = "tid_12345"
                this.contextId = contextId
                status {
                    state = "canceled"
                }
            }
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
            cancelTaskResponse {
                id = 1
                result {
                    id = "tid_12345"
                    this.contextId = contextId
                    status {
                        state = "canceled"
                    }
                }
            }
        payload shouldBeEqualToComparingFields expectedReply
    }

    @Test
    suspend fun `Should fail to cancel task`() {
        a2aServer.cancelTask() responds {
            id = 1
            error =
                internalError {
                    message = "Oops"
                }
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
            cancelTaskResponse {
                id = 1
                error =
                    internalError {
                        message = "Oops"
                    }
            }
        payload shouldBeEqualToComparingFields expectedReply
    }
}
