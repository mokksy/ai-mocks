package dev.mokksy.aimocks.ollama.lc4j

import dev.langchain4j.model.ollama.OllamaEmbeddingModel
import dev.mokksy.aimocks.ollama.AbstractMockOllamaTest
import dev.mokksy.aimocks.ollama.mockOllama
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

internal class EmbeddingLc4jTest : AbstractMockOllamaTest() {
    private val model by lazy {
        OllamaEmbeddingModel
            .builder()
            .baseUrl(mockOllama.baseUrl())
            .logRequests(true)
            .logResponses(true)
            .modelName(embeddingModelName)
            .build()
    }

    @Test
    fun `Should embed string`() =
        runTest {
            // Configure mock response
            val expectedEmbeddingVector = listOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f)
            mockOllama.embed {
                model = embeddingModelName
            } responds {
                embeddings(expectedEmbeddingVector)
                delay = 42.milliseconds
            }

            // Use langchain4j Ollama client to send a request
            val result = model.embed("Hello")

            // Verify response
            result shouldNotBeNull {
                content().vector() shouldBe expectedEmbeddingVector
                metadata() shouldNotBeNull {}
            }
        }
}
