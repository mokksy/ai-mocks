package dev.mokksy.aimocks.gemini.springai

import dev.mokksy.aimocks.gemini.AbstractMockGeminiTest
import org.junit.jupiter.api.TestInstance
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal abstract class AbstractSpringAiTest : AbstractMockGeminiTest() {
    protected val chatClient: ChatClient =
        ChatClient
            .builder(
                VertexAiGeminiChatModel
                    .builder()
                    .vertexAI(vertexAI)
                    .build(),
            ).build()

    protected fun prepareClientRequest(systemMessage: String): ChatClient.ChatClientRequestSpec =
        chatClient
            .prompt()
            .system(systemMessage)
            .user("Just say 'Hello!'")
            .options(
                VertexAiGeminiChatOptions
                    .builder()
                    .model(modelName)
                    .temperature(temperatureValue)
                    .build(),
            )
}
