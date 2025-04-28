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
import me.kpavlov.aimocks.a2a.model.cancelTaskRequest
import me.kpavlov.aimocks.a2a.model.cancelTaskResponse
import me.kpavlov.aimocks.a2a.model.internalError
import java.util.UUID
import kotlin.test.Test

internal class CancelTaskTest : AbstractTest() {
    /**
     * https://github.com/google/A2A/blob/gh-pages/documentation.md#cancel-a-task
     */
    @Test
    fun `Should cancel task`() =
        runTest {
            val sessionId = UUID.randomUUID().toString()

            a2aServer.cancelTask() responds {
                id = 1
                result {
                    id = "tid_12345"
                    this.sessionId = sessionId
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

            val expectedReply = cancelTaskResponse {
                id = 1
                result {
                    id = "tid_12345"
                    this.sessionId = sessionId
                    status {
                        state = "canceled"
                    }
                }
            }
            payload shouldBeEqualToComparingFields expectedReply
        }

    @Test
    fun `Should fail to cancel task`() =
        runTest {
            a2aServer.cancelTask() responds {
                id = 1
                error = internalError {
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

            val expectedReply = cancelTaskResponse {
                id = 1
                error = internalError {
                    message = "Oops"
                }
            }
            payload shouldBeEqualToComparingFields expectedReply
        }
}
