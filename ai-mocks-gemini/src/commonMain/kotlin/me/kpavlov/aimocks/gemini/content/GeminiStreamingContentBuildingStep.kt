package me.kpavlov.aimocks.gemini.content

import io.ktor.sse.TypedServerSentEvent
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.core.AbstractStreamingBuildingStep
import me.kpavlov.aimocks.gemini.GenerateContentRequest
import me.kpavlov.aimocks.gemini.GenerateContentResponse
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer
import me.kpavlov.mokksy.response.StreamResponseDefinition
import java.util.UUID

/**
 * Building step for configuring responses to Gemini content generation requests.
 *
 * This class provides methods for configuring both regular and streaming responses
 * to Gemini content generation requests.
 *
 * @property mokksy The MokksyServer instance to use for configuring responses.
 * @property buildingStep The BuildingStep instance to use for configuring responses.
 */
public class GeminiStreamingContentBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<GenerateContentRequest>,
) : AbstractStreamingBuildingStep<GenerateContentRequest, GeminiStreamingContentResponseSpecification>(
        mokksy = mokksy,
        buildingStep = buildingStep,
    ) {
    public override infix fun respondsStream(block: GeminiStreamingContentResponseSpecification.() -> Unit) {
        respondsStream(sse = true, block)
    }

    public fun respondsStream(
        sse: Boolean = true,
        block: GeminiStreamingContentResponseSpecification.() -> Unit,
    ) {
        buildingStep.respondsWithStream {
            val responseDefinition: StreamResponseDefinition<GenerateContentRequest, String> =
                this.build()
            val responseSpec =
                GeminiStreamingContentResponseSpecification(
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
            val responseId = UUID.randomUUID().toString().replace("-", "")
            flow =
                prepareFlow(
                    responseId = responseId,
                    model = request.model,
                    chunksFlow = chunkFlow,
                    finishReason = responseSpec.finishReason,
                ).map {
                    encodeChunk(it, sse = sse, lastChunk = false)
                }.onStart {
                    if (!sse) {
                        emit("[")
                    }
                }.onCompletion {
                    val chunk =
                        generateFinalContentResponse(
                            finishReason = responseSpec.finishReason,
                            responseId = responseId,
                        )
                    emit(encodeChunk(chunk, sse = sse, lastChunk = true))
                    if (!sse) {
                        emit("]")
                    }
                }
        }
    }

    @OptIn(InternalAPI::class)
    private fun encodeChunk(
        chunk: GenerateContentResponse,
        sse: Boolean,
        lastChunk: Boolean = false,
    ): String {
        return if (sse) {
            TypedServerSentEvent(
                data = chunk,
            ).toString {
                Json.encodeToString(it)
            }
        } else if (lastChunk) {
            Json.encodeToString(value = chunk)
        } else {
            "${Json.encodeToString(value = chunk)},\r\n"
        }
    }

    private fun prepareFlow(
        responseId: String,
        model: String?,
        chunksFlow: Flow<String>,
        finishReason: String?,
    ): Flow<GenerateContentResponse> =
        chunksFlow.mapNotNull { text ->
            generateContentResponse(
                assistantContent = text,
                finishReason = finishReason?.uppercase(),
                modelVersion = model,
                responseId = responseId,
            )
        }
}
