package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class SendTaskRequestBuilderTest {
    @Test
    fun `should build SendTaskRequest with minimal parameters`() {
        // when
        val request =
            SendTaskRequest.create {
                id = "request-123"
                params {
                    id = "task-123"
                    message {
                        role = Message.Role.user
                        parts +=
                            textPart {
                                text = "Hello, how can I help you?"
                            }
                    }
                }
            }

        // then
        request.id shouldBe "request-123"
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/send"
        request.params shouldNotBe null
        request.params.id shouldBe "task-123"
        request.params.message shouldNotBe null
        request.params.message.role shouldBe Message.Role.user
        request.params.message.parts.size shouldBe 1
        (request.params.message.parts[0] as TextPart).text shouldBe "Hello, how can I help you?"
    }

    @Test
    fun `should build SendTaskRequest with all parameters`() {
        // when
        val request =
            SendTaskRequest.create {
                id = "request-123"
                params {
                    id = "task-123"
                    sessionId = "session-456"
                    message {
                        role = Message.Role.user
                        parts.add(
                            textPart {
                                text = "Hello, how can I help you?"
                            },
                        )
                    }
                    pushNotification {
                        url = "https://example.org/notifications"
                        token = "auth-token"
                    }
                    historyLength = 10
                    metadata = Metadata.of(
                        "foo" to "bar",
                        "baz" to 42,
                        "qux" to "quux"
                    )
                }
            }

        // then
        request.id shouldBe "request-123"
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/send"
        request.params shouldNotBe null
        request.params.id shouldBe "task-123"
        request.params.sessionId shouldBe "session-456"
        request.params.message shouldNotBe null
        request.params.message.role shouldBe Message.Role.user
        request.params.message.parts.size shouldBe 1
        (request.params.message.parts[0] as TextPart).text shouldBe "Hello, how can I help you?"
        request.params.pushNotification shouldNotBe null
        request.params.pushNotification?.url shouldBe "https://example.org/notifications"
        request.params.pushNotification?.token shouldBe "auth-token"
        request.params.historyLength shouldBe 10
        request.params.metadata shouldNotBe null
    }

    @Test
    fun `should throw exception when params are missing`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            SendTaskRequestBuilder().build()
        }
    }
}
