package me.kpavlov.aimocks.gemini.springai

import me.kpavlov.aimocks.gemini.AbstractMockGeminiTest
import org.junit.jupiter.api.BeforeAll
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions

internal abstract class AbstractSpringAiTest : AbstractMockGeminiTest() {
    protected lateinit var chatClient: ChatClient

    @BeforeAll
    fun createChatClient() {
        chatClient =
            ChatClient
                .builder(
                    VertexAiGeminiChatModel
                        .builder()
                        .vertexAI(vertexAI)
                        .build(),
                ).build()
    }

    protected fun prepareClientRequest(): ChatClient.ChatClientRequestSpec =
        chatClient
            .prompt()
            .system("You are a helpful pirate. $seedValue")
            .user("Just say 'Hello!'")
            .options(
                VertexAiGeminiChatOptions
                    .builder()
                    .model(modelName)
                    .temperature(temperatureValue)
                    .build(),
            )
}
