package dev.mokksy.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class TextPartBuilderTest {
    @Test
    fun `should build TextPart with text`() {
        // when
        val textPart =
            textPart {
                text = "Hello, world!"
            }

        // then
        textPart.text shouldBe "Hello, world!"
        textPart.metadata shouldBe null
    }

    @Test
    fun `should build TextPart with text and metadata`() {
        // when
        val textPart =
            textPart {
                text = "Hello, world!"
                metadata = Metadata.empty()
            }

        // then
        textPart.text shouldBe "Hello, world!"
        textPart.metadata shouldNotBe null
    }

    @Test
    fun `should build TextPart with empty text when not specified`() {
        // when
        val textPart = textPart {}

        // then
        textPart.text shouldBe ""
        textPart.metadata shouldBe null
    }

    @Test
    fun `should validate TextPart when required`() {
        // when
        val textPart =
            TextPartBuilder()
                .apply {
                    text = "Hello, world!"
                }.build(validate = true)

        // then
        textPart.text shouldBe "Hello, world!"
        textPart.metadata shouldBe null
    }
}
