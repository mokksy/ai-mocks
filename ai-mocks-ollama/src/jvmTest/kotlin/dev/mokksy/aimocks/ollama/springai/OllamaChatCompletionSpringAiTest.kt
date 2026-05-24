package dev.mokksy.aimocks.ollama.springai

import dev.mokksy.aimocks.ollama.mockOllama
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.ai.ollama.api.OllamaApi
import org.springframework.ai.ollama.api.OllamaApi.Message.Role
import org.springframework.ai.ollama.api.OllamaChatOptions
import kotlin.test.Test

internal class OllamaChatCompletionSpringAiTest : AbstractSpringAiTest() {
    @Test
    fun `Should respond to Chat Completion`() {
        mockOllama.chat("ollama-spring-ai-chat-$seedValue") {
            temperature = temperatureValue
            seed = seedValue
            model = modelName
            systemMessageContains("helpful pirate")
            userMessageContains("say 'Hello!'")
            requestMatchesPredicate {
                !it.stream
            }
        } responds {
            assistantContent = "Ahoy there, matey! Hello!"
            finishReason = "stop"
        }

        val response =
            requireNotNull(
                prepareClientRequest("You are a helpful pirate")
                    .call()
                    .chatResponse(),
            )

        response.result shouldNotBeNull {
            metadata.finishReason shouldBe "stop"
            output.text shouldBe "Ahoy there, matey! Hello!"
        }
    }

    @Test
    fun `Should respond to Chat Completion with tool calls`() {
        val toolCallId = "call_$seedValue"

        mockOllama.chat("ollama-spring-ai-chat-tools-$seedValue") {
            temperature = temperatureValue
            seed = seedValue
            model = modelName
            systemMessageContains("helpful pirate")
            userMessageContains("say 'Hello!'")
            requestMatchesPredicate {
                !it.stream
            }
        } responds {
            assistantContent = ""
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
            finishReason = "stop"
        }

        val api =
            OllamaApi
                .builder()
                .baseUrl(mockOllama.baseUrl())
                .build()

        val request =
            OllamaApi.ChatRequest
                .builder(modelName)
                .messages(
                    listOf(
                        OllamaApi.Message.builder(Role.SYSTEM)
                            .content("You are a helpful pirate")
                            .build(),
                        OllamaApi.Message.builder(Role.USER)
                            .content("Just say 'Hello!'")
                            .build(),
                    ),
                ).options(
                    OllamaChatOptions
                        .builder()
                        .temperature(temperatureValue)
                        .seed(seedValue)
                        .model(modelName)
                        .topK(topKValue.toInt())
                        .topP(topPValue)
                        .build(),
                ).build()

        val response =
            requireNotNull(api.chat(request))

        response.doneReason() shouldBe "stop"
        response.message().content() shouldBe ""

        val toolCall = response.message().toolCalls().shouldNotBeNull().single()
        toolCall.id() shouldBe toolCallId
        toolCall.function().name() shouldBe "get_weather"
        toolCall.function().arguments() shouldBe mapOf("city" to "Tokyo")
    }
}
