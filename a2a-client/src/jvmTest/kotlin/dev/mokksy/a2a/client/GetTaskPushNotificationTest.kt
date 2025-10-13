package dev.mokksy.a2a.client

import dev.mokksy.aimocks.a2a.model.AuthenticationInfo
import dev.mokksy.aimocks.a2a.model.GetTaskPushNotificationResponse
import dev.mokksy.aimocks.a2a.model.PushNotificationConfig
import dev.mokksy.aimocks.a2a.model.TaskId
import dev.mokksy.aimocks.a2a.model.TaskPushNotificationConfig
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class GetTaskPushNotificationTest : AbstractTest() {
    /**
     * https://a2a-protocol.org/latest/specification/#76-taskspushnotificationconfigget
     */
    @Test
    fun `Should get TaskPushNotification config`() =
        runTest {
            val taskId: TaskId = "task_12345"
            val config =
                TaskPushNotificationConfig(
                    id = taskId,
                    pushNotificationConfig =
                        PushNotificationConfig(
                            url = "https://example.com/callback",
                            token = "abc.def.jk",
                            authentication =
                                AuthenticationInfo(
                                    schemes = listOf("Bearer"),
                                ),
                        ),
                )

            a2aServer.getTaskPushNotification() responds {
                id = 1
                result = config
            }

            val payload = client.getTaskPushNotification(taskId)
            logger.info { "response = $payload" }
            payload shouldBeEqualToComparingFields
                GetTaskPushNotificationResponse(
                    id = 1,
                    result = config,
                )
        }
}
