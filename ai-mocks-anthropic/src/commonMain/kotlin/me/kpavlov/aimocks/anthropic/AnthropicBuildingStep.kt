package me.kpavlov.aimocks.anthropic

import com.anthropic.core.JsonValue
import com.anthropic.models.messages.ContentBlock
import com.anthropic.models.messages.Message
import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.MessageParam
import com.anthropic.models.messages.TextBlock
import com.anthropic.models.messages.Usage
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import me.kpavlov.aimocks.anthropic.StreamingResponseHelper.randomIdString
import me.kpavlov.aimocks.core.LlmBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer
import me.kpavlov.mokksy.response.StreamResponseDefinition
import java.util.Optional
import java.util.concurrent.atomic.AtomicInteger

public class AnthropicBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<MessageCreateParams.Body>,
    private val serializer: (Any) -> String,
) : LlmBuildingStep<MessageCreateParams.Body, AnthropicMessagesResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    private var counter: AtomicInteger = AtomicInteger(1)

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
                Message
                    .builder()
                    .id(chatResponseSpecification.messageId)
                    .role(JsonValue.from(MessageParam.Role.ASSISTANT))
                    .model(request.model())
                    .content(
                        listOf(
                            ContentBlock.ofText(
                                TextBlock
                                    .builder()
                                    .text(assistantContent)
                                    .citations(Optional.empty())
                                    .build(),
                            ),
                        ),
                    ).stopSequence(null)
                    .stopReason(Message.StopReason.of(stopReason))
                    .usage(
                        Usage
                            .builder()
                            .inputTokens(0)
                            .outputTokens(completionTokens)
                            .cacheCreationInputTokens(0)
                            .cacheReadInputTokens(0)
                            .build(),
                    ).build()
        }
    }

    /**
     * Configures a streaming response for a chat completion request by applying the provided specifications.
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
    @OptIn(ExperimentalCoroutinesApi::class)
    public infix fun respondsStream(block: AnthropicStreamingChatResponseSpecification.() -> Unit) {
        buildingStep.respondsWithStream<String> {
            val responseDefinition: StreamResponseDefinition<MessageCreateParams.Body, String> =
                this.build()
            val responseSpec =
                AnthropicStreamingChatResponseSpecification(
                    responseDefinition,
                )
            block.invoke(responseSpec)

            headers += "Content-Type" to "text/event-stream"
            headers += "Connection" to "keep-alive"
            headers += "x-request-id" to randomIdString("req_")

            val id = counter.addAndGet(1)

            val chunkFlow = responseSpec.responseFlow ?: responseSpec.responseChunks?.asFlow()

            if (chunkFlow == null) {
                error("Either responseChunks or responseFlow must be defined")
            }
            val request = this.request.body
            flow =
                prepareFlow(
                    id = id,
                    model = request.model().asString(),
                    chunksFlow = chunkFlow,
                    stopReason = responseSpec.stopReason,
                ).map {
                    "event: ${it.event}\ndata: ${it.data}\n\n"
                }
        }
    }

    /**
     * See [Anthropic example](https://docs.anthropic.com/en/api/messages-streaming#basic-streaming-request)
     */
    private fun prepareFlow(
        id: Int,
        model: String,
        chunksFlow: Flow<String>,
        stopReason: String,
    ): Flow<ServerSentEvent> =
        flow {
            emit(
                StreamingResponseHelper.createMessageStartChunk(
                    id = id,
                    model = model,
                    serializer = serializer,
                ),
            )
            emit(
                StreamingResponseHelper.createContentBlockStartChunk(
                    serializer = serializer,
                ),
            )
            emit(
                StreamingResponseHelper.createPingEvent(),
            )
            emitAll(
                chunksFlow.map {
                    StreamingResponseHelper.createTextDeltaChunk(
                        content = it,
                        serializer = serializer,
                    )
                },
            )
            emit(
                StreamingResponseHelper.createContentBlockStopChunk(
                    serializer = serializer,
                ),
            )
            emit(
                StreamingResponseHelper.createMessageDeltaChunk(
                    stopReason = stopReason,
                    outputTokens = 100,
                    serializer = serializer,
                ),
            )
            emit(
                StreamingResponseHelper.createMessageStopChunk(
                    serializer = serializer,
                ),
            )
        }
}
