package me.kpavlov.a2a.client

import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import kotlinx.coroutines.test.runTest
import me.kpavlov.aimocks.a2a.model.Message
import me.kpavlov.aimocks.a2a.model.SendMessageResponse
import me.kpavlov.aimocks.a2a.model.Task
import me.kpavlov.aimocks.a2a.model.create
import kotlin.test.Test

internal class SendMessageTest : AbstractTest() {
    /**
     * https://a2a-protocol.org/latest/specification/#71-messagesend
     */
    @Test
    @Suppress("LongMethod")
    fun `Should send task`() =
        runTest {
            val task =
                Task.create {
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

            val reply =
                SendMessageResponse(
                    id = 1,
                    result = task,
                )

            a2aServer.sendMessage() responds {
                id = 1
                result = task
            }

            val taskParams =
                me.kpavlov.aimocks.a2a.model.MessageSendParams.create {
                    message {
                        role = Message.Role.user
                        parts +=
                            textPart {
                                text = "Tell me a joke"
                            }
                    }
                }

            val payload = client.sendMessage(taskParams)
            logger.info { "response = $payload" }
            payload shouldBeEqualToComparingFields reply
        }
}
