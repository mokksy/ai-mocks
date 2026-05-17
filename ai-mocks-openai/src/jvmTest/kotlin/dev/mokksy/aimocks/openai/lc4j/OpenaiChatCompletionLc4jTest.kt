package dev.mokksy.aimocks.openai.lc4j

import dev.langchain4j.data.message.UserMessage.userMessage
import dev.langchain4j.kotlin.model.chat.chat
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiChatRequestParameters
import dev.langchain4j.model.output.FinishReason
import dev.mokksy.aimocks.openai.AbstractMockOpenaiTest
import dev.mokksy.aimocks.openai.openai
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

internal class OpenaiChatCompletionLc4jTest : AbstractMockOpenaiTest() {
    private lateinit var model: OpenAiChatModel

    @BeforeEach
    fun setupModel() {
        model =
            OpenAiChatModel
                .builder()
                .apiKey("foo")
                .baseUrl(openai.baseUrl())
                .modelName(modelName)
                .build()
    }

    @Test
    suspend fun `Should respond to Chat Completion`() {
        val userMessage = "Please run the Lc4j chat test"
        val expectedSubstring = "Lc4j chat test"
        openai.completion {
            temperature = temperatureValue
            seed = seedValue
            model = modelName
            maxTokens = maxCompletionTokensValue
            userMessageContains(expectedSubstring)
        } responds {
            assistantContent = "Hello"
            finishReason = "stop"
            delay = 42.milliseconds
        }

        val result =
            model.chat {
                parameters =
                    OpenAiChatRequestParameters
                        .builder()
                        .maxCompletionTokens(maxCompletionTokensValue.toInt())
                        .temperature(temperatureValue)
                        .modelName(modelName)
                        .seed(seedValue)
                        .build()
                messages += userMessage(userMessage)
            }

        result.apply {
            finishReason() shouldBe FinishReason.STOP
            tokenUsage() shouldNotBe null
            aiMessage().text() shouldBe "Hello"
        }
    }
}
