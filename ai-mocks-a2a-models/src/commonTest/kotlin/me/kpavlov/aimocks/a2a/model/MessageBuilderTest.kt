package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class MessageBuilderTest {
    @Test
    fun `should build Message with minimal parameters`() {
        // when
        val message =
            Message.create {
                role = Message.Role.user
                parts +=
                    textPart {
                        text = "Hello, how can I help you?"
                    }
            }

        // then
        message.role shouldBe Message.Role.user
        message.parts.size shouldBe 1
        (message.parts[0] as TextPart).text shouldBe "Hello, how can I help you?"
        message.metadata shouldBe null
    }

    @Test
    fun `should build Message with multiple parts`() {
        // when
        val message =
            Message.create {
                role = Message.Role.agent
                parts.add(
                    textPart {
                        text = "Hello, I'm an agent."
                    },
                )
                parts.add(
                    textPart {
                        text = "How can I help you today?"
                    },
                )
            }

        // then
        message.role shouldBe Message.Role.agent
        message.parts.size shouldBe 2
        (message.parts[0] as TextPart).text shouldBe "Hello, I'm an agent."
        (message.parts[1] as TextPart).text shouldBe "How can I help you today?"
        message.metadata shouldBe null
    }

    @Test
    fun `should build Message with metadata`() {
        // when
        val message =
            Message.create {
                role = Message.Role.user
                parts.add(
                    textPart {
                        text = "Hello, how can I help you?"
                    },
                )
                metadata =
                    Metadata.of(
                        "foo" to "bar",
                        "baz" to 42,
                    )
            }

        // then
        message.role shouldBe Message.Role.user
        message.parts.size shouldBe 1
        (message.parts[0] as TextPart).text shouldBe "Hello, how can I help you?"
        message.metadata shouldNotBe null
    }

    @Test
    fun `should throw exception when role is missing`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            MessageBuilder()
                .apply {
                    parts.add(
                        textPart {
                            text = "Hello, how can I help you?"
                        },
                    )
                }.build()
        }
    }

    @Test
    fun `should throw exception when parts are empty`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            MessageBuilder()
                .apply {
                    role = Message.Role.user
                }.build()
        }
    }
}
