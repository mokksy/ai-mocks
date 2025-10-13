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
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

internal class ChatCompletionLc4jTest : AbstractMockOpenaiTest() {
    private val model: OpenAiChatModel =
        OpenAiChatModel
            .builder()
            .apiKey("foo")
            .baseUrl(openai.baseUrl())
            .build()

    @Test
    fun `Should respond to Chat Completion`() =
        runTest {
            openai.completion {
                temperature = temperatureValue
                seed = seedValue
                model = modelName
                maxTokens = maxCompletionTokensValue
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
                    messages += userMessage("Say Hello")
                }

            result.apply {
                finishReason() shouldBe FinishReason.STOP
                tokenUsage() shouldNotBe null
                aiMessage().text() shouldBe "Hello"
            }
        }
}
