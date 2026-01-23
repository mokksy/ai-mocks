package dev.mokksy.aimocks.anthropic.lc4j

import dev.langchain4j.data.message.UserMessage.userMessage
import dev.langchain4j.kotlin.model.chat.chat
import dev.langchain4j.model.anthropic.AnthropicChatModel
import dev.langchain4j.model.output.FinishReason
import dev.mokksy.aimocks.anthropic.AbstractAnthropicIntegrationTest
import dev.mokksy.aimocks.anthropic.anthropic
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

internal class ChatCompletionLc4jTest : AbstractAnthropicIntegrationTest() {
    private lateinit var systemMessage: String
    private lateinit var model: AnthropicChatModel

    @BeforeEach
    fun setupModel() {
        systemMessage = "You are a person of 60s $seedValue"
        model =
            AnthropicChatModel
                .builder()
                .apiKey("foo")
                .baseUrl(anthropic.baseUrl() + "/v1")
                .modelName(requireNotNull(modelName))
                .build()
    }

    @Test
    fun `Should respond to Chat Completion`() =
        runTest {
            anthropic.messages {
                userMessageContains("Hello")
            } responds {
                assistantContent = "Hello"
                delay = 42.milliseconds
            }

            val result =
                model.chat {
                    messages += userMessage("Say Hello")
                }

            result.apply {
                finishReason() shouldBe FinishReason.STOP
                tokenUsage() shouldNotBe null
                aiMessage().text() shouldBe "Hello"
            }
        }
}
