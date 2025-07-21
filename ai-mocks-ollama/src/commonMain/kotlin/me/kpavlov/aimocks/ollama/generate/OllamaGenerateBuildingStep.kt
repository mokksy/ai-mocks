package me.kpavlov.aimocks.ollama.generate

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
     * Configures a streaming response for a generate completions request by applying the provided specifications.
     *
     * This function sets up a chunked response where the response is streamed as a series of JSON objects,
     * often used in streaming generate scenarios. It allows the specification of response content and other
     * streaming-specific details through a configuration block.
     *
     * @param block A configuration block that customizes the streaming response by applying specifications
     *              to an instance of [OllamaStreamingGenerateResponseSpecification].
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

    private fun createChunk(
        model: String,
        createdAt: Instant,
        response: String,
    ): GenerateResponse = GenerateResponse(
        model = model,
        createdAt = createdAt,
        response = response,
        done = false,
    )

    private fun createFinalChunk(
        model: String,
        createdAt: Instant,
        doneReason: String? = null,
    ): GenerateResponse = GenerateResponse(
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
