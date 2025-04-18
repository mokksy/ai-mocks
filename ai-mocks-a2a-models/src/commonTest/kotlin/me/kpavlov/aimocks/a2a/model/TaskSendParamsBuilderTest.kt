package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class TaskSendParamsBuilderTest {
    @Test
    fun `should build TaskSendParams with minimal parameters`() {
        // when
        val params = TaskSendParams.create {
            id = "task-123"
            message {
                role = Message.Role.user
                parts.add(textPart {
                    text = "Hello, how can I help you?"
                })
            }
        }

        // then
        params.id shouldBe "task-123"
        params.message shouldNotBe null
        params.message.role shouldBe Message.Role.user
        params.message.parts.size shouldBe 1
        (params.message.parts[0] as TextPart).text shouldBe "Hello, how can I help you?"
        params.sessionId shouldBe null
        params.pushNotification shouldBe null
        params.historyLength shouldBe null
        params.metadata shouldBe null
    }

    @Test
    fun `should build TaskSendParams with all parameters`() {
        // when
        val params = TaskSendParams.build {
            id = "task-123"
            sessionId = "session-456"
            message {
                role = Message.Role.user
                parts.add(textPart {
                    text = "Hello, how can I help you?"
                })
            }
            pushNotification {
                url = "https://example.org/notifications"
                token = "auth-token"
            }
            historyLength = 10
            metadata = Metadata()
        }

        // then
        params.id shouldBe "task-123"
        params.sessionId shouldBe "session-456"
        params.message shouldNotBe null
        params.message.role shouldBe Message.Role.user
        params.message.parts.size shouldBe 1
        (params.message.parts[0] as TextPart).text shouldBe "Hello, how can I help you?"
        params.pushNotification shouldNotBe null
        params.pushNotification?.url shouldBe "https://example.org/notifications"
        params.pushNotification?.token shouldBe "auth-token"
        params.historyLength shouldBe 10
        params.metadata shouldNotBe null
    }

    @Test
    fun `should throw exception when id is missing`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            TaskSendParamsBuilder().apply {
                message {
                    role = Message.Role.user
                    parts.add(textPart {
                        text = "Hello, how can I help you?"
                    })
                }
            }.build()
        }
    }

    @Test
    fun `should throw exception when message is missing`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            TaskSendParamsBuilder().apply {
                id = "task-123"
            }.build()
        }
    }
}
