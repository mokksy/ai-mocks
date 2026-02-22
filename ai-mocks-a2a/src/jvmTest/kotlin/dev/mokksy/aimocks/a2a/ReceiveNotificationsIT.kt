package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.TaskId
import dev.mokksy.aimocks.a2a.model.taskArtifactUpdateEvent
import dev.mokksy.test.utils.runIntegrationTest
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.Test

internal class ReceiveNotificationsIT : AbstractIT() {
    @Test
    fun `Should resubscribe to task`() =
        runIntegrationTest {
            val taskId: TaskId = "task_12345"

            val notificationHistory = a2aServer.getTaskNotifications(taskId)

            notificationHistory.events() shouldHaveSize 0

            val taskUpdateEvent =
                taskArtifactUpdateEvent {
                    id = taskId
                    artifact {
                        name = "joke"
                        parts +=
                            textPart {
                                text = "This is a notification joke!"
                            }
                        lastChunk = true
                    }
                }
            a2aServer.sendPushNotification(event = taskUpdateEvent)

            notificationHistory.events() shouldContain taskUpdateEvent
        }
}
