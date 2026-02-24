package dev.mokksy.aimocks.ollama.ktor

import dev.mokksy.aimocks.ollama.chat.ChatRequest
import dev.mokksy.aimocks.ollama.chat.ChatResponse
import dev.mokksy.aimocks.ollama.chat.Message
import dev.mokksy.aimocks.ollama.mockOllama
import dev.mokksy.aimocks.ollama.model.ModelOptions
import dev.mokksy.test.utils.runIntegrationTest
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

internal class OllamaChatCompletionTest : AbstractOllamaKtorTest() {
    @Test
    fun `Should respond to Chat Completion`() =
        runIntegrationTest {
            // Configure mock response
            mockOllama.chat {
                model = modelName
            } responds {
                content("Hello, how can I help you today?")
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
                                content = "Hello",
                            ),
                        ),
                    stream = false,
                    options =
                        ModelOptions(
                            temperature = temperatureValue,
                            topP = topPValue,
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
                message.content shouldBe "Hello, how can I help you today?"
                model shouldBe modelName
                done shouldBe true
            }
        }
}
