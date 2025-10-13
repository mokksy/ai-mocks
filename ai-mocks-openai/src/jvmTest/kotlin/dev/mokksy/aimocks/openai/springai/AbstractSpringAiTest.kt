package dev.mokksy.aimocks.openai.springai

import dev.mokksy.aimocks.openai.AbstractMockOpenaiTest
import dev.mokksy.aimocks.openai.openai
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.api.OpenAiApi

internal val chatClient =
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

internal abstract class AbstractSpringAiTest : AbstractMockOpenaiTest() {
    protected fun prepareClientRequest(systemPrompt: String): ChatClient.ChatClientRequestSpec =
        chatClient
            .prompt()
            .system(systemPrompt)
            .user("Just say 'Hello!'")
            .options(
                OpenAiChatOptions
                    .builder()
                    .maxCompletionTokens(maxCompletionTokensValue.toInt())
                    .temperature(temperatureValue)
                    .model(modelName)
                    .seed(seedValue)
                    .build(),
            )
}
