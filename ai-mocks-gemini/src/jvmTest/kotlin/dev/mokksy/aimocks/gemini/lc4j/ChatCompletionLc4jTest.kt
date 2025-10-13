package dev.mokksy.aimocks.gemini.lc4j

import dev.langchain4j.data.message.UserMessage.userMessage
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import dev.mokksy.aimocks.gemini.AbstractMockGeminiTest
import dev.mokksy.aimocks.gemini.gemini
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Disabled
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

@Disabled("Wait for LangChain4j release > 1.1.0")
internal class ChatCompletionLc4jTest : AbstractMockGeminiTest() {
    private val model: GoogleAiGeminiChatModel

    init {
        model =
            GoogleAiGeminiChatModel
                .builder()
                // TODO: .baseUrl(gemini.baseUrl())
                .apiKey("foo")
                .build()
    }

    @Test
    fun `Should respond to Chat Completion`() =
        runTest {
            gemini.generateContent {
                temperature = temperatureValue
                model = modelName
                maxTokens = maxCompletionTokensValue
            } responds {
                content = "Hello"
                finishReason = "stop"
                delay = 42.milliseconds
            }

            val result =
                model.chat(
                    ChatRequest
                        .builder()
                        .temperature(temperatureValue)
                        .modelName(modelName)
                        .messages(userMessage("Say Hello"))
                        .build(),
                )

            result.aiMessage().text() shouldBe "Hello"
        }
}
