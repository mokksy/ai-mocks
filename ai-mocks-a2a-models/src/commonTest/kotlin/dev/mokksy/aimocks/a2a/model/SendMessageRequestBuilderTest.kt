package dev.mokksy.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class SendMessageRequestBuilderTest {
    @Test
    fun `should build SendMessageRequest with minimal parameters`() {
        // when
        val request =
            SendMessageRequest.create {
                id = "request-123"
                params {
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
        request.method shouldBe "message/send"
        request.params shouldNotBe null
        request.params.message shouldNotBe null
        request.params.message.role shouldBe Message.Role.user
        request.params.message.parts.size shouldBe 1
        (request.params.message.parts[0] as TextPart).text shouldBe "Hello, how can I help you?"
    }

    @Test
    fun `should build SendMessageRequest with all parameters`() {
        // when
        val request =
            SendMessageRequest.create {
                id = "request-123"
                params {
                    message {
                        role = Message.Role.user
                        parts.add(
                            textPart {
                                text = "Hello, how can I help you?"
                            },
                        )
                    }
                    configuration {
                        blocking = false
                    }
                }
            }

        // then
        request.id shouldBe "request-123"
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "message/send"
        request.params shouldNotBe null
        request.params.message shouldNotBe null
        request.params.message.role shouldBe Message.Role.user
        request.params.message.parts.size shouldBe 1
        (request.params.message.parts[0] as TextPart).text shouldBe "Hello, how can I help you?"
        request.params.configuration?.blocking shouldBe false
    }

    @Test
    fun `should throw exception when params are missing`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            SendMessageRequestBuilder().build()
        }
    }
}
