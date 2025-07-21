package me.kpavlov.aimocks.ollama.chat

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
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.random.Random.Default.nextInt

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
 * @param mokksy The mock server instance used for handling mock request and response lifecycle.
 * @param buildingStep The underlying building step for managing and supporting response configurations
 *                     for Ollama Chat Completion requests.
 */
public class OllamaChatBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<ChatRequest>,
) : AbstractBuildingStep<ChatRequest, OllamaChatResponseSpecification>(
    mokksy,
    buildingStep,
) {
    @Suppress("MagicNumber")
    override infix fun responds(block: OllamaChatResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val request = this.request.body
            val responseDefinition = this.build()
            val chatResponseSpecification = OllamaChatResponseSpecification(
                response = responseDefinition as AbstractResponseDefinition<ChatResponse>
            )
            block.invoke(chatResponseSpecification)
            delay = chatResponseSpecification.delay

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
     * Configures a streaming response for a chat completions request by applying the provided specifications.
     *
     * This function sets up a chunked response where the response is streamed as a series of JSON objects,
     * often used in streaming chat scenarios. It allows the specification of response content and other
     * streaming-specific details through a configuration block.
     *
     * @param block A configuration block that customizes the streaming response by applying specifications
     *              to an instance of [OllamaStreamingChatResponseSpecification].
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    public infix fun respondsStream(block: OllamaStreamingChatResponseSpecification.() -> Unit) {
        buildingStep.respondsWithStream {
            val responseDefinition = this.build()
            val responseSpec =
                OllamaStreamingChatResponseSpecification(
                    response = responseDefinition as AbstractResponseDefinition<String>
                )
            block.invoke(responseSpec)

            headers += "Content-Type" to "application/x-ndjson"
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
                )
        }
    }

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
        }.map { chunk -> Json.encodeToString(chunk) + "\n\n" }
    }

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
