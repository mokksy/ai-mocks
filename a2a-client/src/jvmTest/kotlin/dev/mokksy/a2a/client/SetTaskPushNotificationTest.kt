package dev.mokksy.a2a.client

import dev.mokksy.aimocks.a2a.model.SetTaskPushNotificationResponse
import dev.mokksy.aimocks.a2a.model.TaskId
import dev.mokksy.aimocks.a2a.model.TaskPushNotificationConfig
import dev.mokksy.aimocks.a2a.model.create
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SetTaskPushNotificationTest : AbstractTest() {
    /**
     * https://a2a-protocol.org/latest/specification/#75-taskspushnotificationconfigset
     */
    @Test
    fun `Should set TaskPushNotification config`() =
        runTest {
            val taskId: TaskId = "task_12345"
            val config =
                TaskPushNotificationConfig.create {
                    id = taskId
                    pushNotificationConfig {
                        url = "https://example.com/callback"
                        token = "abc.def.jk"
                        authentication {
                            credentials = "secret"
                            schemes += "Bearer"
                        }
                    }
                }

            a2aServer.setTaskPushNotification() responds {
                id = 1
                result {
                    id = taskId
                    pushNotificationConfig {
                        url = "https://example.com/callback"
                        token = "abc.def.jk"
                        authentication {
                            credentials = "secret"
                            schemes += "Bearer"
                        }
                    }
                }
            }

            val payload =
                client.setTaskPushNotification(
                    id = taskId,
                    config = config.pushNotificationConfig,
                )
            logger.info { "response = $payload" }
            payload shouldBeEqualToComparingFields
                SetTaskPushNotificationResponse(
                    id = 1,
                    result = config,
                )
        }
}
