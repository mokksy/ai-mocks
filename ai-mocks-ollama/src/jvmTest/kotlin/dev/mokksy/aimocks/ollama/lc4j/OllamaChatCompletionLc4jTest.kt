package dev.mokksy.aimocks.ollama.lc4j

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.data.message.UserMessage.userMessage
import dev.langchain4j.kotlin.model.chat.chat
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.mokksy.aimocks.ollama.AbstractMockOllamaTest
import dev.mokksy.aimocks.ollama.mockOllama
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
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

    @Test
    suspend fun `Should respond to Chat Completion with tool calls`() {
        val userMessage = "Check weather for $seedValue"
        val toolCallId = "call_$seedValue"
        val arguments = """{"city":"Tokyo"}"""

        mockOllama.chat("ollama-lc4j-chat-tools-$seedValue") {
            model = modelName
            seed = seedValue
            temperature = temperatureValue
            topP = topPValue
            userMessageContains(userMessage)
        } responds {
            content("")
            toolCalls(
                listOf(
                    mapOf(
                        "id" to toolCallId,
                        "type" to "function",
                        "function" to
                            mapOf(
                                "name" to "get_weather",
                                "arguments" to mapOf("city" to "Tokyo"),
                            ),
                    ),
                ),
            )
        }

        val result =
            model.chat {
                messages += userMessage(userMessage)
                parameters {
                    temperature = temperatureValue
                    topP = topPValue
                }
            }

        val toolRequest =
            result.aiMessage()
                .toolExecutionRequests()
                .shouldNotBeNull()
                .single()
                .shouldBeInstanceOf<ToolExecutionRequest>()

        toolRequest.id() shouldBe toolCallId
        toolRequest.name() shouldBe "get_weather"
        toolRequest.arguments() shouldBe arguments
    }
}
