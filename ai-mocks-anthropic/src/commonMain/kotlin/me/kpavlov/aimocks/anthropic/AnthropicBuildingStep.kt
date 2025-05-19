package me.kpavlov.aimocks.anthropic

import io.ktor.sse.TypedServerSentEvent
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import me.kpavlov.aimocks.anthropic.StreamingResponseHelper.randomIdString
import me.kpavlov.aimocks.anthropic.model.AnthropicSseData
import me.kpavlov.aimocks.anthropic.model.Message
import me.kpavlov.aimocks.anthropic.model.MessageCreateParams
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer
import me.kpavlov.mokksy.response.StreamResponseDefinition

public class AnthropicBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<MessageCreateParams>,
) : AbstractBuildingStep<MessageCreateParams, AnthropicMessagesResponseSpecification>(
    mokksy,
    buildingStep,
) {
    @Suppress("MagicNumber")
    @OptIn(ExperimentalStdlibApi::class)
    override infix fun responds(block: AnthropicMessagesResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val request = this.request.body
            val responseDefinition = this.build()
            val chatResponseSpecification =
                AnthropicMessagesResponseSpecification(responseDefinition)
            block.invoke(chatResponseSpecification)
            val assistantContent = chatResponseSpecification.assistantContent
            val stopReason = chatResponseSpecification.stopReason
            val completionTokens = LongRange(1, 10).random()
            delay = chatResponseSpecification.delay

            headers += "x-request-id" to randomIdString("req_")
            body =
                Message(
                    role = "assistant",
                    id = chatResponseSpecification.messageId,
                    content = listOf(
                        me.kpavlov.aimocks.anthropic.model.TextBlock(
                            text = assistantContent
                        )
                    ),
                    model = request.model,
                    stopReason = me.kpavlov.aimocks.anthropic.model.StopReason.valueOf(stopReason.uppercase()),
                    usage = me.kpavlov.aimocks.anthropic.model.Usage(
                        outputTokens = completionTokens,
                        cacheCreationInputTokens = 0,
                        inputTokens = LongRange(10, 1000).random(),
                        cacheReadInputTokens = 0,
                    )
                )
        }
    }

    /**
     * Configures a streaming response for a chat completions request by applying the provided specifications.
     *
     * This function sets up
     * a [chunked response](https://docs.anthropic.com/en/api/messages-streaming#basic-streaming-request)
     * where the response is streamed as a series of SSE objects.
     * It allows the specification of response content and other
     * streaming-specific details through a configuration block.
     *
     * @param block A configuration block that customizes the streaming response by applying specifications
     *              to an instance of [AnthropicStreamingChatResponseSpecification].
     * @link
     */
    @OptIn(ExperimentalCoroutinesApi::class, InternalAPI::class)
    public infix fun respondsStream(block: AnthropicStreamingChatResponseSpecification.() -> Unit) {
        buildingStep.respondsWithStream {
            val responseDefinition: StreamResponseDefinition<MessageCreateParams, String> =
                this.build()
            val responseSpec =
                AnthropicStreamingChatResponseSpecification(
                    responseDefinition,
                )
            block.invoke(responseSpec)

            headers += "Content-Type" to "text/event-stream"
            headers += "Connection" to "keep-alive"
            headers += "x-request-id" to randomIdString("req_")

            val id = randomIdString("msg_")

            val chunkFlow = responseSpec.responseFlow ?: responseSpec.responseChunks?.asFlow()

            if (chunkFlow == null) {
                error("Either responseChunks or responseFlow must be defined")
            }
            val request = this.request.body
            flow =
                prepareFlow(
                    id = id,
                    model = request.model,
                    chunksFlow = chunkFlow,
                    stopReason = responseSpec.stopReason,
                ).mapNotNull {
                    val dataJson = Json.encodeToString(
                        value = it.data,
                        serializer = AnthropicSseData.serializersModule.serializer()
                    )
                    "event: ${it.event}\ndata: ${dataJson}\n\n"
                }
        }
    }

    /**
     * See [Anthropic example](https://docs.anthropic.com/en/api/messages-streaming#basic-streaming-request)
     */
    private fun prepareFlow(
        id: String,
        model: String,
        chunksFlow: Flow<String>,
        stopReason: String,
    ): Flow<TypedServerSentEvent<AnthropicSseData>> =
        flow {
            @Suppress("TooGenericExceptionCaught")
            try {
                emit(
                    StreamingResponseHelper.createMessageStartChunk(
                        id = id,
                        model = model,
                    ),
                )
                emit(
                    StreamingResponseHelper.createContentBlockStartChunk(
                    ),
                )
                emit(
                    StreamingResponseHelper.createPingEvent(),
                )
                emitAll(
                    chunksFlow.map {
                        StreamingResponseHelper.createTextDeltaChunk(
                            content = it,
                        )
                    },
                )
                emit(
                    StreamingResponseHelper.createContentBlockStopChunk(
                    ),
                )
                emit(
                    StreamingResponseHelper.createMessageDeltaChunk(
                        stopReason = stopReason,
                        outputTokens = 100,
                    ),
                )
                emit(
                    StreamingResponseHelper.createMessageStopChunk(
                    ),
                )
            } catch (e: Exception) {
                logger.error("Failed to build streaming response", e)
                throw e
            }
        }
}
