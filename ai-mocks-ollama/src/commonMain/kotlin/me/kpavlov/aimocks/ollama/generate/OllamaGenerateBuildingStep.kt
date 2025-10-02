package me.kpavlov.aimocks.ollama.generate

import io.ktor.http.ContentType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer
import me.kpavlov.mokksy.response.StreamResponseDefinition
import kotlin.random.Random.Default.nextInt

/**
 * OllamaGenerateBuildingStep is a specialized implementation of [AbstractBuildingStep]
 * intended for constructing and managing generate completion responses as part of the Ollama
 * Mock Server setup.
 *
 * The class provides features to create both single-blocked responses and streaming
 * responses for simulated generate completions using mock data.
 * It extends the functionality of [AbstractBuildingStep] by applying specific logic
 * for generating fake responses compliant with Ollama's generate completion API.
 *
 * @constructor Initializes the building step with the provided mock server instance and
 *              a higher-level building step for configuring generate completion responses.
 *
 * @param mokksy The mock server instance used for handling mock request and response lifecycle.
 * @param buildingStep The underlying building step for managing and supporting response configurations
 *                     for Ollama Generate Completion requests.
 */
public class OllamaGenerateBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<GenerateRequest>,
) : AbstractBuildingStep<GenerateRequest, OllamaGenerateResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    /**
     * Configures a mock generate completion response for an Ollama generate request.
     *
     * Applies the provided configuration block to customize the response content and completion reason, then generates a single-block `GenerateResponse` with randomized timing and evaluation metrics, the current timestamp, and the model from the request.
     */
    @Suppress("MagicNumber")
    override infix fun responds(block: OllamaGenerateResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val request = this.request.body
            val responseDefinition = this.build()
            val generateResponseSpecification =
                OllamaGenerateResponseSpecification(responseDefinition)
            block.invoke(generateResponseSpecification)
            val responseContent = generateResponseSpecification.responseContent
            val doneReason = generateResponseSpecification.doneReason
            delay = generateResponseSpecification.delay
            contentType = ContentType.Application.Json

            val promptEvalCount = nextInt(1, 200)
            val evalCount = nextInt(1, 500)

            body =
                GenerateResponse(
                    model = request.model,
                    createdAt = Clock.System.now(),
                    response = responseContent,
                    done = true,
                    doneReason = doneReason,
                    context = listOf(1, 2, 3),
                    totalDuration = nextInt(10, 5000).toLong(),
                    loadDuration = nextInt(10, 1000).toLong(),
                    promptEvalCount = promptEvalCount,
                    promptEvalDuration = nextInt(10, 5000).toLong(),
                    evalCount = evalCount,
                    evalDuration = nextInt(10, 5000).toLong(),
                )
        }
    }

    /**
     * Configures a streaming mock response for a generate completion request using the provided specification block.
     *
     * Sets up a chunked server-sent event (SSE) response, streaming JSON-encoded completion chunks as defined by the configuration. The response stream includes initial, content, and final chunks, and requires either a flow or a list of response chunks to be specified.
     *
     * @param block Configuration block for customizing the streaming response via [OllamaStreamingGenerateResponseSpecification].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    public infix fun respondsStream(block: OllamaStreamingGenerateResponseSpecification.() -> Unit) {
        buildingStep.respondsWithStream {
            val responseDefinition: StreamResponseDefinition<GenerateRequest, String> =
                this.build()
            val responseSpec =
                OllamaStreamingGenerateResponseSpecification(
                    responseDefinition,
                )
            block.invoke(responseSpec)

            headers += "Content-Type" to "text/event-stream"
            headers += "Connection" to "keep-alive"

            val chunkFlow = responseSpec.responseFlow ?: responseSpec.responseChunks?.asFlow()

            if (chunkFlow == null) {
                error("Either responseChunks or responseFlow must be defined")
            }
            val request = this.request.body
            flow =
                prepareFlow(
                    model = request.model,
                    chunksFlow = chunkFlow,
                    doneReason = responseSpec.doneReason,
                )
        }
    }

    /**
     * Constructs a flow emitting a sequence of JSON-encoded generate response chunks for streaming.
     *
     * The flow starts with an initial empty chunk, followed by content chunks for each string in [chunksFlow],
     * and ends with a final chunk indicating completion and an optional done reason. Each emitted value is a JSON string
     * followed by two newlines.
     *
     * @param model The model identifier to include in each response chunk.
     * @param chunksFlow A flow of string content chunks to be included in the response.
     * @param doneReason An optional reason for stream completion, included in the final chunk.
     * @return A flow of JSON-encoded response chunks formatted for streaming.
     */
    private fun prepareFlow(
        model: String,
        chunksFlow: Flow<String>,
        doneReason: String?,
    ): Flow<String> {
        val timestamp = Clock.System.now()
        return flow {
            // Initial empty response
            emit(
                createChunk(
                    model = model,
                    createdAt = timestamp,
                    response = "",
                ),
            )
            // Content chunks
            emitAll(
                chunksFlow.map {
                    createChunk(
                        model = model,
                        createdAt = timestamp,
                        response = it,
                    )
                },
            )
            // Final chunk with done=true
            emit(
                createFinalChunk(
                    model = model,
                    createdAt = timestamp,
                    doneReason = doneReason,
                ),
            )
        }.map { chunk -> Json.encodeToString(chunk) + "\n\n" }
    }

    /**
     * Creates a `GenerateResponse` chunk representing an intermediate (not final) part of a streaming response.
     *
     * The chunk includes the specified model, creation timestamp, and response content, with `done` set to false.
     *
     * @param model The model identifier for the response.
     * @param createdAt The timestamp when the chunk is created.
     * @param response The content of the response chunk.
     * @return A `GenerateResponse` object with `done` set to false.
     */
    private fun createChunk(
        model: String,
        createdAt: Instant,
        response: String,
    ): GenerateResponse =
        GenerateResponse(
            model = model,
            createdAt = createdAt,
            response = response,
            done = false,
        )

    /**
     * Creates a final chunk for a streaming generate response, indicating completion.
     *
     * The returned [GenerateResponse] has `done` set to true, an empty response string, a fixed context, randomized timing and evaluation metrics, and an optional done reason.
     *
     * @param model The model identifier.
     * @param createdAt The timestamp when the response was created.
     * @param doneReason Optional reason for completion.
     * @return A [GenerateResponse] representing the final chunk in a streaming response.
     */
    private fun createFinalChunk(
        model: String,
        createdAt: Instant,
        doneReason: String? = null,
    ): GenerateResponse =
        GenerateResponse(
            model = model,
            createdAt = createdAt,
            response = "",
            done = true,
            doneReason = doneReason,
            context = listOf(1, 2, 3),
            totalDuration = nextInt(10, 5000).toLong(),
            loadDuration = nextInt(10, 1000).toLong(),
            promptEvalCount = nextInt(1, 200),
            promptEvalDuration = nextInt(10, 5000).toLong(),
            evalCount = nextInt(1, 500),
            evalDuration = nextInt(10, 5000).toLong(),
        )
}
