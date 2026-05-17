package dev.mokksy.aimocks.ollama.lc4j

import dev.langchain4j.data.message.UserMessage.userMessage
import dev.langchain4j.kotlin.model.chat.chat
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.mokksy.aimocks.ollama.AbstractMockOllamaTest
import dev.mokksy.aimocks.ollama.mockOllama
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

internal class OllamaChatCompletionLc4jTest : AbstractMockOllamaTest() {
    private lateinit var model: OllamaChatModel

    @BeforeEach
    fun setupModel() {
        model =
            OllamaChatModel
                .builder()
                .baseUrl(mockOllama.baseUrl())
                .modelName(modelName)
                .temperature(temperatureValue)
                .topP(topPValue)
                .seed(seedValue)
                .build()
    }

    @Test
    suspend fun `Should respond to Chat Completion`() {
        // Configure mock response
        mockOllama.chat("ollama-lc4j-chat-$seedValue") {
            model = modelName
            seed = seedValue
            temperature = temperatureValue
            topP = topPValue
            userMessageContains("Lc4j chat test")
        } responds {
            content("Hello, how can I help you today?")
            delay = 152.milliseconds
        }

        // Use langchain4j Ollama client to send a request
        val startTime = TimeSource.Monotonic.markNow()
        val result =
            model.chat {
                messages += userMessage("Run the Lc4j chat test")
                parameters {
                    temperature = temperatureValue
                    topP = topPValue
                }
            }
        val elapsed = startTime.elapsedNow()
        elapsed shouldBeGreaterThan 152.milliseconds

        // Verify response
        result.aiMessage().text() shouldBe "Hello, how can I help you today?"
    }
}
