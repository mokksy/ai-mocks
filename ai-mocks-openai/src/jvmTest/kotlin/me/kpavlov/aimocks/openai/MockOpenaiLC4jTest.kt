package me.kpavlov.aimocks.openai

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.langchain4j.data.message.UserMessage.userMessage
import dev.langchain4j.model.openai.OpenAiChatModel
import dev.langchain4j.model.openai.OpenAiChatRequestParameters
import kotlinx.coroutines.test.runTest
import me.kpavlov.langchain4j.kotlin.model.chat.chatAsync
import org.junit.jupiter.api.Test

internal class MockOpenaiLC4jTest : AbstractMockOpenaiTest() {
    private val model: OpenAiChatModel =
        OpenAiChatModel
            .builder()
            .apiKey("foo")
            .baseUrl("http://127.0.0.1:${openai.port()}/v1")
            .build()

    @Test
    fun `Should respond to Chat Completion`() =
        runTest {
            openai.completion {
                temperature = temperatureValue
                seed = seedValue
                model = modelName
                maxCompletionTokens = maxCompletionTokens
            } responds {
                assistantContent = "Hello"
                finishReason = "stop"
            }

            val result =
                model.chatAsync {
                    parameters =
                        OpenAiChatRequestParameters
                            .builder()
                            .temperature(temperatureValue)
                            .modelName(modelName)
                            .seed(seedValue)
                            .build()
                    messages += userMessage("Say Hello")
                }

            println(result)
            assertThat(
                result.aiMessage().text(),
            ).isEqualTo("Hello")
        }
}
