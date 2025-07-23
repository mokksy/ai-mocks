package me.kpavlov.aimocks.ollama.lc4j

import dev.langchain4j.data.message.UserMessage.userMessage
import dev.langchain4j.kotlin.model.chat.chat
import dev.langchain4j.model.ollama.OllamaChatModel
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import me.kpavlov.aimocks.ollama.AbstractMockOllamaTest
import me.kpavlov.aimocks.ollama.mockOllama
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

internal class ChatCompletionLc4jTest : AbstractMockOllamaTest() {
    private val model by lazy {
        OllamaChatModel.builder()
            .baseUrl(mockOllama.baseUrl())
            .modelName(modelName)
            .topP(topPValue)
            .build()
    }

    @Test
    fun `Should respond to Chat Completion`() = runTest {
        // Configure mock response
        mockOllama.chat {
            model = modelName
            userMessageContains("Hello")
            temperature = temperatureValue
            topP = topPValue
            requestMatchesPredicate { !it.stream }
        } responds {
            content("Hello, how can I help you today?")
            delay = 152.milliseconds
        }

        // Use langchain4j Ollama client to send a request
        val startTime = TimeSource.Monotonic.markNow()
        val result = model.chat {
            messages += userMessage("Hello")
            parameters {
                temperature = temperatureValue
            }
        }
        val elapsed = startTime.elapsedNow()
        elapsed shouldBeGreaterThan 152.milliseconds

        // Verify response
        result.apply {
            aiMessage().text() shouldBe "Hello, how can I help you today?"
        }
    }
}
