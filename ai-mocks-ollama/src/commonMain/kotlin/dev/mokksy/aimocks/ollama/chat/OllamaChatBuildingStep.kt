package dev.mokksy.aimocks.ollama.chat

import dev.mokksy.aimocks.core.AbstractBuildingStep
import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import io.ktor.http.ContentType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlin.random.Random.Default.nextInt
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * OllamaChatBuildingStep is a specialized implementation of [AbstractBuildingStep]
 * intended for constructing and managing chat completion responses as part of the Ollama
 * Mock Server setup.
 *
 * The class provides features to create both single-blocked responses and streaming
 * responses for simulated chat completions using mock data.
 * It extends the functionality of [AbstractBuildingStep] by applying specific logic
 * for generating fake responses compliant with Ollama's chat completion API.
 *
 * @constructor Initializes the building step with the provided mock server instance and
 *              a higher-level building step for configuring chat completion responses.
 *
 * @param mokksy The [MokksyServer] instance used for handling mock request and response lifecycle.
 * @param buildingStep The underlying [BuildingStep] for managing and supporting response configurations
 *                     for Ollama Chat Completion requests.
 */
public class OllamaChatBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<ChatRequest>,
) : AbstractBuildingStep<ChatRequest, OllamaChatResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    /**
     * Configures a single, complete chat response for the mock Ollama chat completion API.
     *
     * Applies the provided configuration block to an [OllamaChatResponseSpecification],
     * generates randomized timing and evaluation metadata, and constructs a [ChatResponse]
     * with the specified model, message, and completion details.
     */
    @Suppress("MagicNumber")
    override infix fun responds(block: OllamaChatResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val request = this.request.body
            val responseDefinition = this.build()
            val chatResponseSpecification =
                OllamaChatResponseSpecification(
                    response = responseDefinition as AbstractResponseDefinition<ChatResponse>,
                )
            block.invoke(chatResponseSpecification)
            delay = chatResponseSpecification.delay
            contentType = ContentType.Application.Json

            val promptEvalCount = nextInt(1, 200)
            val evalCount = nextInt(1, 500)
            val totalDuration = nextInt(10, 5000).toLong()
            val loadDuration = nextInt(10, 5000).toLong()
            val promptEvalDuration = nextInt(10, 5000).toLong()
            val evalDuration = nextInt(10, 5000).toLong()

            body =
                ChatResponse(
                    model = request.model,
                    createdAt = Clock.System.now(),
                    message = chatResponseSpecification.createMessage(),
                    done = true,
                    doneReason = chatResponseSpecification.finishReason,
                    totalDuration = totalDuration,
                    loadDuration = loadDuration,
                    promptEvalCount = promptEvalCount,
                    promptEvalDuration = promptEvalDuration,
                    evalCount = evalCount,
                    evalDuration = evalDuration,
                )
        }
    }

    /**
     * Configures a streaming chat completion response using a user-defined specification block.
     *
     * Sets up a chunked HTTP response where chat completion data is streamed as a sequence of JSON objects,
     * simulating real-time message delivery. The configuration block customizes the streaming behavior and content.
     *
     * @param block A configuration block for customizing the streaming chat response
     * via [OllamaStreamingChatResponseSpecification].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    public infix fun respondsStream(block: OllamaStreamingChatResponseSpecification.() -> Unit) {
        buildingStep.respondsWithStream {
            val responseDefinition = this.build()
            val responseSpec =
                OllamaStreamingChatResponseSpecification(
                    response = responseDefinition as AbstractResponseDefinition<String>,
                )
            block.invoke(responseSpec)

            headers += "Content-Type" to "application/x-ndjson"
            headers += "Connection" to "keep-alive"

            val chunkFlow = responseSpec.responseFlow ?: responseSpec.responseChunks?.asFlow()

            if (chunkFlow == null) {
                error("Either responseChunks or responseFlow must be defined")
            }
            val request = this.request.body
            delayBetweenChunks = responseSpec.delayBetweenChunks
            delay = responseSpec.delay
            flow =
                prepareFlow(
                    model = request.model,
                    chunksFlow = chunkFlow,
                )
        }
    }

    /**
     * Constructs a flow of JSON-encoded chat response chunks for streaming,
     * including initial, content, and final chunks.
     *
     * The resulting flow emits:
     *  - An initial empty chunk with `done = false`.
     *  - Each content chunk from the input flow, wrapped as a [ChatResponse] with `done = false`.
     *  - A final empty chunk with `done = true`.
     * Each chunk is serialized to JSON and followed by two newlines.
     *
     * @param model The model name to include in each response chunk.
     * @param chunksFlow A flow of content strings to be included as response chunks.
     * @return A flow of JSON-encoded chat response chunks formatted for streaming.
     */
    private fun prepareFlow(
        model: String,
        chunksFlow: Flow<String>,
    ): Flow<String> {
        val timestamp = Clock.System.now()
        return flow {
            // Initial empty response
            emit(
                createChunk(
                    model = model,
                    createdAt = timestamp,
                    content = "",
                    done = false,
                ),
            )
            // Content chunks
            emitAll(
                chunksFlow.map {
                    createChunk(
                        model = model,
                        createdAt = timestamp,
                        content = it,
                        done = false,
                    )
                },
            )
            // Final chunk with done=true
            emit(
                createChunk(
                    model = model,
                    createdAt = timestamp,
                    content = "",
                    done = true,
                ),
            )
        }.map { chunk -> Json.encodeToString(chunk) + "\r\n" }
    }

    /**
     * Creates a [ChatResponse] chunk representing a segment of a chat completion response.
     *
     * If `done` is true, the response includes randomized timing and evaluation metadata;
     * otherwise, these fields are null.
     *
     * @param model The model identifier for the response.
     * @param createdAt The timestamp when the chunk is created.
     * @param content The message content for this chunk.
     * @param done Indicates whether this is the final chunk in the response.
     * @return A [ChatResponse] object containing the specified content and metadata.
     */
    @Suppress("MagicNumber")
    private fun createChunk(
        model: String,
        createdAt: Instant,
        content: String,
        done: Boolean,
    ): ChatResponse {
        val message = Message(role = "assistant", content = content)

        val totalDuration = if (done) nextInt(10, 5000).toLong() else null
        val loadDuration = if (done) nextInt(10, 1000).toLong() else null
        val promptEvalCount = if (done) nextInt(1, 200) else null
        val promptEvalDuration = if (done) nextInt(10, 5000).toLong() else null
        val evalCount = if (done) nextInt(1, 5000) else null
        val evalDuration = if (done) nextInt(10, 5000).toLong() else null

        return ChatResponse(
            model = model,
            createdAt = createdAt,
            message = message,
            done = done,
            totalDuration = totalDuration,
            loadDuration = loadDuration,
            promptEvalCount = promptEvalCount,
            promptEvalDuration = promptEvalDuration,
            evalCount = evalCount,
            evalDuration = evalDuration,
        )
    }
}
