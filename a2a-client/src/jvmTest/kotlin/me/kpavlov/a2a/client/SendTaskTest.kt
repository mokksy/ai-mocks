package me.kpavlov.a2a.client

import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import kotlinx.coroutines.test.runTest
import me.kpavlov.aimocks.a2a.model.Message
import me.kpavlov.aimocks.a2a.model.SendTaskResponse
import me.kpavlov.aimocks.a2a.model.Task
import me.kpavlov.aimocks.a2a.model.create
import java.util.UUID
import kotlin.test.Test

internal class SendTaskTest : AbstractTest() {
    /**
     * https://github.com/google/A2A/blob/gh-pages/documentation.md#send-a-task
     */
    @Test
    @Suppress("LongMethod")
    fun `Should send task`() =
        runTest {
            val task =
                Task.create {
                    id = "tid_12345"
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

            val reply =
                SendTaskResponse(
                    id = 1,
                    result = task,
                )

            a2aServer.sendTask() responds {
                id = 1
                result = task
            }

            val taskParams = me.kpavlov.aimocks.a2a.model.TaskSendParams.create {
                id = UUID.randomUUID().toString()
                message {
                    role = Message.Role.user
                    parts +=
                        textPart {
                            text = "Tell me a joke"
                        }
                }
            }

            val payload = client.sendTask(taskParams)
            logger.info { "response = $payload" }
            payload shouldBeEqualToComparingFields reply
        }
}
