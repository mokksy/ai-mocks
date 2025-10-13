package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.AuthenticationInfo
import dev.mokksy.aimocks.a2a.model.ListTaskPushNotificationConfigRequest
import dev.mokksy.aimocks.a2a.model.ListTaskPushNotificationConfigResponse
import dev.mokksy.aimocks.a2a.model.PushNotificationConfig
import dev.mokksy.aimocks.a2a.model.TaskId
import dev.mokksy.aimocks.a2a.model.TaskPushNotificationConfig
import dev.mokksy.aimocks.a2a.model.invalidParamsError
import dev.mokksy.aimocks.a2a.model.listTaskPushNotificationConfigRequest
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

internal class ListTaskPushNotificationConfigTest : AbstractTest() {
    /**
     * https://a2a-protocol.org/latest/specification/#77-taskspushnotificationconfiglist
     */
    @Test
    fun `Should list TaskPushNotification configs`() =
        runTest {
            val taskId1: TaskId = "task_12345"
            val taskId2: TaskId = "task_67890"

            val config1 =
                TaskPushNotificationConfig(
                    id = taskId1,
                    pushNotificationConfig =
                        PushNotificationConfig(
                            url = "https://example.com/callback1",
                            token = "abc.def.jk1",
                            authentication =
                                AuthenticationInfo(
                                    schemes = listOf("Bearer"),
                                ),
                        ),
                )

            val config2 =
                TaskPushNotificationConfig(
                    id = taskId2,
                    pushNotificationConfig =
                        PushNotificationConfig(
                            url = "https://example.com/callback2",
                            token = "abc.def.jk2",
                            authentication =
                                AuthenticationInfo(
                                    schemes = listOf("Basic"),
                                ),
                        ),
                )

            val configList = listOf(config1, config2)

            a2aServer.listTaskPushNotificationConfig() responds {
                id = 1
                result = configList
            }

            val response =
                a2aClient
                    .post("/") {
                        val jsonRpcRequest =
                            listTaskPushNotificationConfigRequest {
                                id = "1"
                                params {
                                    limit = 10
                                    offset = 0
                                }
                            }
                        contentType(ContentType.Application.Json)
                        setBody(jsonRpcRequest)
                    }.call
                    .response

            response.status shouldBe HttpStatusCode.OK
            val payload = response.body<ListTaskPushNotificationConfigResponse>()

            val expectedReply =
                ListTaskPushNotificationConfigResponse(
                    id = 1,
                    result = configList,
                )
            payload shouldBeEqualToComparingFields expectedReply
        }

    @Test
    fun `Should fail to list TaskPushNotification configs`() =
        runTest {
            a2aServer.listTaskPushNotificationConfig() responds {
                id = 1
                error =
                    invalidParamsError {
                        message = "Invalid parameters"
                    }
            }

            val response =
                a2aClient
                    .post("/") {
                        val jsonRpcRequest =
                            ListTaskPushNotificationConfigRequest(
                                id = "1",
                                params = null,
                            )
                        contentType(ContentType.Application.Json)
                        setBody(jsonRpcRequest)
                    }.call
                    .response

            response.status shouldBe HttpStatusCode.OK
            val payload = response.body<ListTaskPushNotificationConfigResponse>()

            val expectedReply =
                ListTaskPushNotificationConfigResponse(
                    id = 1,
                    error =
                        invalidParamsError {
                            message = "Invalid parameters"
                        },
                )
            payload shouldBeEqualToComparingFields expectedReply
        }
}
