package dev.mokksy.aimocks.ollama.ktor

import dev.mokksy.aimocks.ollama.generate.GenerateRequest
import dev.mokksy.aimocks.ollama.generate.GenerateResponse
import dev.mokksy.aimocks.ollama.mockOllama
import dev.mokksy.aimocks.ollama.model.ModelOptions
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

internal class GenerateCompletionTest : AbstractKtorTest() {
    @Test
    fun `Should respond to Generate Completion`() =
        runTest {
            // Configure mock response
            mockOllama.generate {
                model = modelName
                temperature = temperatureValue
                topP = topPValue
                userMessageContains("Tell me a joke")
            } responds {
                content("Why did the chicken cross the road? To get to the other side!")
                doneReason("stop")
                delay = 42.milliseconds
            }

            // Create request
            val request =
                GenerateRequest(
                    model = modelName,
                    prompt = "Tell me a joke",
                    stream = false,
                    options =
                        ModelOptions(
                            temperature = temperatureValue,
                            topP = topPValue,
                        ),
                )

            // Send request to mock server using Ktor client
            val response: GenerateResponse =
                client
                    .post("${mockOllama.baseUrl()}/api/generate") {
                        contentType(ContentType.Application.Json)
                        setBody(request)
                    }.body()

            // Verify response
            response.response shouldBe
                "Why did the chicken cross the road? To get to the other side!"
            response.model shouldBe modelName
            response.done shouldBe true
            response.doneReason shouldBe "stop"
        }
}
