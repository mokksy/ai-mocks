package me.kpavlov.aimocks.a2a

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.test.runTest
import me.kpavlov.aimocks.a2a.model.TaskId
import me.kpavlov.aimocks.a2a.model.taskArtifactUpdateEvent
import kotlin.test.Test

internal class ReceiveNotificationsTest : AbstractTest() {
    @Test
    fun `Should resubscribe to task`() =
        runTest {
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
