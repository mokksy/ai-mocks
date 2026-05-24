package dev.mokksy.aimocks.ollama.ktor

import dev.mokksy.aimocks.ollama.chat.ChatRequest
import dev.mokksy.aimocks.ollama.chat.ChatResponse
import dev.mokksy.aimocks.ollama.chat.FunctionCall
import dev.mokksy.aimocks.ollama.chat.Message
import dev.mokksy.aimocks.ollama.mockOllama
import dev.mokksy.aimocks.ollama.model.ModelOptions
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

internal class OllamaChatCompletionTest : AbstractOllamaKtorTest() {
    @Test
    suspend fun `Should respond to Chat Completion`() {
        val userMessage = "Hello, $seedValue!?"
        val assistantMessage = "Hello, how can I help you today? $seedValue!"
        // Configure mock response
        mockOllama.chat {
            model = modelName
            seed = seedValue
            topP = topPValue
            temperature = temperatureValue
            maxTokens = maxTokensValue
            requestBodyContains(userMessage)
            stream(false)
        } responds {
            content(assistantMessage)
            delay = 42.milliseconds
        }

        // Create request
        val request =
            ChatRequest(
                model = modelName,
                messages =
                    listOf(
                        Message(
                            role = "user",
                            content = userMessage,
                        ),
                    ),
                stream = false,
                options =
                    ModelOptions(
                        temperature = temperatureValue,
                        topP = topPValue,
                        seed = seedValue,
                        numPredict = maxTokensValue.toInt(),
                    ),
            )

        // Send request to mock server using Ktor client
        val response: ChatResponse =
            client
                .post("${mockOllama.baseUrl()}/api/chat") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }.body()

        // Verify response
        with(response) {
            message.content shouldBe assistantMessage
            model shouldBe modelName
            done shouldBe true
        }
    }

    @Test
    suspend fun `Should respond to Chat Completion with tool calls`() {
        val userMessage = "Check weather for $seedValue"
        val arguments =
            buildJsonObject {
                put("city", "Tokyo")
            }

        mockOllama.chat("ollama-chat-tools-$seedValue") {
            model = modelName
            seed = seedValue
            requestBodyContains(userMessage)
            stream(false)
        } responds {
            content("")
            toolCalls(
                listOf(
                    mapOf(
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

        val request =
            ChatRequest(
                model = modelName,
                messages =
                    listOf(
                        Message(
                            role = "user",
                            content = userMessage,
                        ),
                    ),
                stream = false,
                options = ModelOptions(seed = seedValue),
            )

        val response: ChatResponse =
            client
                .post("${mockOllama.baseUrl()}/api/chat") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }.body()

        val toolCall =
            requireNotNull(response.message.toolCalls).single()
                .function
                .shouldBeInstanceOf<FunctionCall>()

        response.message.content shouldBe ""
        response.message.toolCalls.single().id shouldBe null
        "get_weather" shouldBe toolCall.name
        """{"city":"Tokyo"}""" shouldBe toolCall.arguments
        arguments shouldBe toolCall.argumentsJson
    }
}
