package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.DeleteTaskPushNotificationConfigRequest
import dev.mokksy.aimocks.a2a.model.DeleteTaskPushNotificationConfigResponse
import dev.mokksy.aimocks.a2a.model.TaskId
import dev.mokksy.aimocks.a2a.model.deleteTaskPushNotificationConfigRequest
import dev.mokksy.aimocks.a2a.model.invalidParamsError
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DeleteTaskPushNotificationConfigTest : AbstractTest() {
    /**
     * https://a2a-protocol.org/latest/specification/#78-taskspushnotificationconfigdelete
     */
    @Test
    fun `Should delete TaskPushNotification config`() =
        runTest {
            val taskId: TaskId = "task_12345"

            a2aServer.deleteTaskPushNotificationConfig() responds {
                id = 1
                result = null
            }

            val response =
                a2aClient
                    .post("/") {
                        val jsonRpcRequest =
                            deleteTaskPushNotificationConfigRequest {
                                id = "1"
                                params {
                                    id = taskId
                                }
                            }
                        contentType(ContentType.Application.Json)
                        setBody(jsonRpcRequest)
                    }.call
                    .response

            response.status shouldBe HttpStatusCode.OK
            val payload = response.body<DeleteTaskPushNotificationConfigResponse>()

            val expectedReply =
                DeleteTaskPushNotificationConfigResponse(
                    id = 1,
                    result = null,
                )
            payload shouldBeEqualToComparingFields expectedReply
        }

    @Test
    fun `Should fail to delete TaskPushNotification config`() =
        runTest {

            a2aServer.deleteTaskPushNotificationConfig() responds {
                id = 1
                error {
                    code = -32602
                    message = "Invalid parameters"
                }
            }

            val response =
                a2aClient
                    .post("/") {
                        val jsonRpcRequest =
                            DeleteTaskPushNotificationConfigRequest(
                                id = "1",
                                params = null,
                            )
                        contentType(ContentType.Application.Json)
                        setBody(jsonRpcRequest)
                    }.call
                    .response

            response.status shouldBe HttpStatusCode.OK
            val payload = response.body<DeleteTaskPushNotificationConfigResponse>()

            val expectedReply =
                DeleteTaskPushNotificationConfigResponse(
                    id = 1,
                    error =
                        invalidParamsError {
                            message = "Invalid parameters"
                        },
                )
            payload shouldBeEqualToComparingFields expectedReply
        }
}
