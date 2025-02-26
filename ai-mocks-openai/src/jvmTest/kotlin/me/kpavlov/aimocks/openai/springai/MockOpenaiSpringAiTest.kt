package me.kpavlov.aimocks.openai

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.test.runTest
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.api.OpenAiApi
import kotlin.test.Test

private val chatClient =
    ChatClient
        .builder(
            org.springframework.ai.openai.OpenAiChatModel
                .builder()
                .openAiApi(
                    OpenAiApi
                        .builder()
                        .apiKey("demo-key")
                        .baseUrl("http://127.0.0.1:${openai.port()}")
                        .build(),
                ).build(),
        ).build()

internal class MockOpenaiSpringAiTest : AbstractMockOpenaiTest() {
    @Test
    fun `Should respond to Chat Completion`() =
        runTest {
            openai.completion {
                temperature = temperatureValue
                seed = seedValue
                model = modelName
                maxCompletionTokens = maxCompletionTokensValue
                systemMessageContains("helpful pirate")
                userMessageContains("say 'Hello!'")
            } responds {
                assistantContent = "Ahoy there, matey! Hello!"
                finishReason = "stop"
            }

            val response =
                chatClient
                    .prompt()
                    .system("You are a helpful pirate")
                    .user("Just say 'Hello!'")
                    .options<OpenAiChatOptions>(
                        OpenAiChatOptions
                            .builder()
                            .maxCompletionTokens(maxCompletionTokensValue.toInt())
                            .temperature(temperatureValue)
                            .model(modelName)
                            .seed(seedValue)
                            .build(),
                    ).call()
                    .chatResponse()

            response?.result shouldNotBe null
            response?.result?.apply {
                metadata.finishReason shouldBe "STOP"
                output?.text shouldBe "Ahoy there, matey! Hello!"
            }
        }
}
