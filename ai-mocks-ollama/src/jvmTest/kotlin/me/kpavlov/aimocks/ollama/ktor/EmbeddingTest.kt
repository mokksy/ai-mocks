package me.kpavlov.aimocks.ollama.ktor

import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.test.runTest
import me.kpavlov.aimocks.core.EmbeddingUtils
import me.kpavlov.aimocks.ollama.embed.EmbeddingsRequest
import me.kpavlov.aimocks.ollama.embed.EmbeddingsResponse
import me.kpavlov.aimocks.ollama.mockOllama
import me.kpavlov.aimocks.ollama.model.ModelOptions
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

internal class EmbeddingTest : AbstractKtorTest() {

    @Test
    fun `Should respond to String Embedding Request`() = runTest {
        // Configure mock response
        val embeddings = listOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f)

        mockOllama.embed {
            model = embeddingModelName
            stringInput = "The sky is blue"
        } responds {
            embeddings(embeddings)
            delay = 42.milliseconds
        }

        // Create request
        val request = EmbeddingsRequest(
            model = embeddingModelName,
            input = listOf("The sky is blue"),
            options = ModelOptions(
                temperature = temperatureValue,
                topP = topPValue
            )
        )

        // Send request to mock server using Ktor client
        val response: EmbeddingsResponse = client.post("${mockOllama.baseUrl()}/api/embed") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

        // Verify response
        response.embeddings shouldBe listOf(embeddings)
        response.model shouldBe embeddingModelName
    }

    @Test
    fun `Should respond to String List Embedding Request`() = runTest {
        // Configure mock response
        val embeddings = listOf(
            listOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f),
            listOf(0.6f, 0.7f, 0.8f, 0.9f, 1.0f)
        )

        mockOllama.embed {
            model = embeddingModelName
            stringListInput = listOf("The sky is blue", "The grass is green")
        } responds {
            embeddings(embeddings)
            delay = 42.milliseconds
        }

        // Create request
        val request = EmbeddingsRequest(
            model = embeddingModelName,
            input = listOf("The sky is blue", "The grass is green"),
            options = ModelOptions(
                temperature = temperatureValue,
                topP = topPValue
            )
        )

        // Send request to mock server using Ktor client
        val response: EmbeddingsResponse = client.post("${mockOllama.baseUrl()}/api/embed") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

        // Verify response
        response.embeddings shouldBe embeddings
        response.model shouldBe embeddingModelName
    }

    @Test
    fun `Should generate some embedding result`() = runTest {
        // Configure mock response
        val input = listOf("The sea is blue", "The tree is green")
        mockOllama.embed {
            model = embeddingModelName
            stringListInput = input
        } responds {
            // do not specify any embeddings
        }

        // Create request
        val request = EmbeddingsRequest(
            model = embeddingModelName,
            input = input,
            options = ModelOptions(
                temperature = temperatureValue,
                topP = topPValue
            )
        )

        // Send request to mock server using Ktor client
        val response: EmbeddingsResponse = client.post("${mockOllama.baseUrl()}/api/embed") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

        // Verify response
        response.embeddings shouldBe
            input.map { sentence ->
                EmbeddingUtils.generateEmbedding(sentence)
            }

        response.model shouldBe embeddingModelName
    }
}
