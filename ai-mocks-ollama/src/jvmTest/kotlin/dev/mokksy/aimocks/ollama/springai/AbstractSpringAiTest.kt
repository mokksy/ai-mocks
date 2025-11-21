package dev.mokksy.aimocks.ollama.springai

import dev.mokksy.aimocks.ollama.AbstractMockOllamaTest
import dev.mokksy.aimocks.ollama.mockOllama
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.ollama.api.OllamaApi
import org.springframework.ai.ollama.api.OllamaChatOptions

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
    protected fun prepareClientRequest(systemMessage: String): ChatClient.ChatClientRequestSpec =
        chatClient
            .prompt()
            .system(systemMessage)
            .user("Just say 'Hello!'")
            .options(
                OllamaChatOptions
                    .builder()
                    .temperature(temperatureValue)
                    .seed(seedValue)
                    .model(modelName)
                    .topK(topKValue.toInt())
                    .topP(topPValue)
                    .build(),
            )
}
