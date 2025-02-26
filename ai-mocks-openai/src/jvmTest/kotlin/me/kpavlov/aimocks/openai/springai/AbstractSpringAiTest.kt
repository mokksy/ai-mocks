package me.kpavlov.aimocks.openai.springai

import me.kpavlov.aimocks.openai.AbstractMockOpenaiTest
import me.kpavlov.aimocks.openai.openai
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
    protected fun prepareClientRequest(): ChatClient.ChatClientRequestSpec =
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
            )
}
