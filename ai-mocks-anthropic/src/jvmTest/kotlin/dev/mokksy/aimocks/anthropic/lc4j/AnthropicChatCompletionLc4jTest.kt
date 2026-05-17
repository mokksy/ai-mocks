package dev.mokksy.aimocks.anthropic.lc4j

import dev.langchain4j.data.message.UserMessage.userMessage
import dev.langchain4j.kotlin.model.chat.chat
import dev.langchain4j.model.anthropic.AnthropicChatModel
import dev.langchain4j.model.output.FinishReason
import dev.mokksy.aimocks.anthropic.AbstractAnthropicIntegrationTest
import dev.mokksy.aimocks.anthropic.anthropic
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

internal class AnthropicChatCompletionLc4jTest : AbstractAnthropicIntegrationTest() {
    private lateinit var model: AnthropicChatModel

    @BeforeEach
    fun setupModel() {
        model =
            AnthropicChatModel
                .builder()
                .apiKey("foo")
                .baseUrl(anthropic.baseUrl() + "/v1")
                .modelName(requireNotNull(modelName))
                .build()
    }

    @Test
    suspend fun `Should respond to Chat Completion`() {
        val userMessage = "Please say Hello"
        anthropic.messages {
            model = modelName
            userMessageContains("say Hello")
        } responds {
            assistantContent = "Hello"
            delay = 42.milliseconds
        }

        val result =
            model.chat {
                messages += userMessage(userMessage)
            }

        result.apply {
            finishReason() shouldBe FinishReason.STOP
            tokenUsage() shouldNotBe null
            aiMessage().text() shouldBe "Hello"
        }
    }
}
