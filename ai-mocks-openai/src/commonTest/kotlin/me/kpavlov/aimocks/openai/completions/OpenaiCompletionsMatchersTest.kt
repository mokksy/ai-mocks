package me.kpavlov.aimocks.openai.completions

import io.kotest.matchers.shouldBe
import me.kpavlov.aimocks.openai.ChatCompletionRequest
import me.kpavlov.aimocks.openai.Message
import me.kpavlov.aimocks.openai.model.ChatCompletionRole
import me.kpavlov.aimocks.openai.model.chat.MessageContent
import kotlin.test.Test

class OpenaiCompletionsMatchersTest {
    @Test
    fun `systemMessageContains matcher should provide correct failure message`() {
        // Given
        val matcher = OpenaiCompletionsMatchers.systemMessageContains("expected content")
        val request =
            ChatCompletionRequest(
                messages =
                    listOf(
                        Message(role = ChatCompletionRole.SYSTEM, content = MessageContent.Text("actual content")),
                    ),
                model = "gpt-4",
            )

        // When
        val result = matcher.test(request)

        // Then
        result.passed() shouldBe false
        result.failureMessage() shouldBe "System message should contain \"expected content\""
        result.negatedFailureMessage() shouldBe
            "System message should not contain \"expected content\""
    }

    @Test
    fun `systemMessageContains matcher should pass when content matches`() {
        // Given
        val matcher = OpenaiCompletionsMatchers.systemMessageContains("expected content")
        val request =
            ChatCompletionRequest(
                messages =
                    listOf(
                        Message(
                            role = ChatCompletionRole.SYSTEM,
                            content = MessageContent.Text("This contains expected content here"),
                        ),
                    ),
                model = "gpt-4",
            )

        // When
        val result = matcher.test(request)

        // Then
        result.passed() shouldBe true
    }

    @Test
    fun `userMessageContains matcher should provide correct failure message`() {
        // Given
        val matcher = OpenaiCompletionsMatchers.userMessageContains("expected content")
        val request =
            ChatCompletionRequest(
                messages =
                    listOf(
                        Message(role = ChatCompletionRole.USER, content = MessageContent.Text("actual content")),
                    ),
                model = "gpt-4",
            )

        // When
        val result = matcher.test(request)

        // Then
        result.passed() shouldBe false
        result.failureMessage() shouldBe "User message should contain \"expected content\""
        result.negatedFailureMessage() shouldBe
            "User message should not contain \"expected content\""
    }
}
