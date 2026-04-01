package dev.mokksy.aimocks.ollama.ktor

import dev.mokksy.aimocks.ollama.generate.GenerateRequest
import dev.mokksy.aimocks.ollama.generate.GenerateResponse
import dev.mokksy.aimocks.ollama.mockOllama
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

internal class OllamaStreamingGenerateCompletionTest : AbstractOllamaKtorTest() {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    suspend fun `Should respond to Streaming Generate Completion with chunks`() {
        val chunks = listOf("Hello", ", ", "World", "!")

        mockOllama.generate {
            model = modelName
            userMessageContains("Tell me a story")
        } respondsStream {
            responseChunks = chunks
            doneReason("stop")
        }

        val responses = collectStreamingResponses("Tell me a story")

        val contentChunks = responses.filter { !it.done }
        val finalChunk = responses.last()

        assertSoftly {
            // First content chunk is the initial empty chunk, followed by real chunks
            contentChunks.drop(1).map { it.response } shouldBe chunks
            finalChunk.done shouldBe true
            finalChunk.doneReason shouldBe "stop"
            finalChunk.model shouldBe modelName
            finalChunk.context shouldBe listOf(1, 2, 3)
        }
    }

    @Test
    suspend fun `Should respond to Streaming Generate Completion with Flow`() {
        val words = listOf("Streaming", "with", "flow")

        mockOllama.generate {
            model = modelName
            userMessageContains("Any prompt")
        } respondsStream {
            responseFlow =
                flow {
                    words.forEach { emit(it) }
                }
            doneReason("stop")
        }

        val responses = collectStreamingResponses("Any prompt")

        val contentChunks = responses.filter { !it.done }
        val finalChunk = responses.last()

        assertSoftly {
            contentChunks.drop(1).map { it.response } shouldBe words
            finalChunk.done shouldBe true
            finalChunk.doneReason shouldBe "stop"
            finalChunk.model shouldBe modelName
        }
    }

    @Test
    suspend fun `Should include model name in all chunks`() {
        mockOllama.generate {
            model = modelName
            userMessageContains("prompt")
        } respondsStream {
            responseChunks = listOf("test chunk")
        }

        val responses = collectStreamingResponses("prompt")

        responses.forEach { chunk ->
            chunk.model shouldBe modelName
        }
    }

    private suspend fun collectStreamingResponses(prompt: String): List<GenerateResponse> {
        val request =
            GenerateRequest(
                model = modelName,
                prompt = prompt,
                stream = true,
            )

        val responses = mutableListOf<GenerateResponse>()
        client
            .preparePost("${mockOllama.baseUrl()}/api/generate") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.execute { response ->
                val channel = response.bodyAsChannel()
                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line() ?: break
                    if (line.isNotBlank()) {
                        responses.add(json.decodeFromString(line))
                    }
                }
            }
        return responses
    }
}
