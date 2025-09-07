package me.kpavlov.aimocks.ollama.springai

import me.kpavlov.aimocks.ollama.AbstractMockOllamaTest
import me.kpavlov.aimocks.ollama.mockOllama
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.ollama.api.OllamaApi
import org.springframework.ai.ollama.api.OllamaOptions

internal val chatClient =
    ChatClient
        .builder(
            org.springframework.ai.ollama.OllamaChatModel
                .builder()
                .ollamaApi(
                    OllamaApi
                        .builder()
                        .baseUrl(mockOllama.baseUrl())
                        .build(),
                ).build(),
        ).build()

internal abstract class AbstractSpringAiTest : AbstractMockOllamaTest() {
    protected fun prepareClientRequest(): ChatClient.ChatClientRequestSpec =
        chatClient
            .prompt()
            .system("You are a helpful pirate")
            .user("Just say 'Hello!'")
            .options(
                OllamaOptions
                    .builder()
                    .temperature(temperatureValue)
                    .seed(seedValue)
                    .model(modelName)
                    .topK(topKValue.toInt())
                    .topP(topPValue)
                    .build(),
            )
}
