package me.kpavlov.aimocks.openai

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import me.kpavlov.aimocks.core.LlmBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer
import me.kpavlov.mokksy.StreamResponseDefinition
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

private const val LINE_SEPARATOR = "\n\n"

public class OpenaiBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<*>,
) : LlmBuildingStep<OpenaiChatResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    private var counter: AtomicInteger = AtomicInteger(1)

    override infix fun responds(block: OpenaiChatResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val responseDefinition = this.build()
            val chatResponseSpecification = OpenaiChatResponseSpecification(responseDefinition)
            block.invoke(chatResponseSpecification)
            val assistantContent = chatResponseSpecification.textContent
            val finishReason = chatResponseSpecification.finishReason

            val response =
                ChatResponse(
                    id = "chatcmpl-abc${counter.addAndGet(1)}",
                    created = Instant.now().epochSecond,
                    model = "gpt-4o-mini",
                    usage =
                        Usage(
                            promptTokens = 13,
                            completionTokens = 7,
                            totalTokens = 20,
                            completionTokensDetails =
                                CompletionTokensDetails(
                                    reasoningTokens = 0,
                                    acceptedPredictionTokens = 0,
                                    rejectedPredictionTokens = 0,
                                ),
                        ),
                    choices =
                        listOf<Choice>(
                            Choice(
                                index = 0,
                                message =
                                    Message(
                                        role = "assistant",
                                        content = assistantContent,
                                    ),
                                finishReason = finishReason,
                            ),
                        ),
                    systemFingerprint = "fp_44709d6fcb",
                )

            body = Json.encodeToString(response)
        }
    }

    /**
     * Configures a streaming response for a chat completion request by applying the provided specifications.
     *
     * This function sets up
     * a [chunked response](https://platform.openai.com/docs/api-reference/chat/streaming)
     * where the response is streamed as a series of JSON objects,
     * often used in streaming chat scenarios. It allows the specification of response content and other
     * streaming-specific details through a configuration block.
     *
     * @param block A configuration block that customizes the streaming response by applying specifications
     *              to an instance of [OpenaiChatResponseSpecification].
     * @link
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    public infix fun respondsStream(block: OpenaiStreamingChatResponseSpecification.() -> Unit) {
        buildingStep.respondsWithStream<String> {
            val responseDefinition: StreamResponseDefinition<String> = this.build()
            val responseSpec =
                OpenaiStreamingChatResponseSpecification(
                    responseDefinition,
                )
            block.invoke(responseSpec)

            headers += "Content-Type" to "text/event-stream"
            headers += "Connection" to "keep-alive"

            val id = counter.addAndGet(1)

            val chunkFlow = responseSpec.responseFlow ?: responseSpec.responseChunks?.asFlow()

            if (chunkFlow == null) {
                error("Either responseChunks or responseFlow must be defined")
            }
            flow =
                prepareFlow(
                    id = id,
                    chunksFlow = chunkFlow,
                    finishReason = responseSpec.finishReason,
                    sendDone = responseSpec.sendDone,
                )
        }
    }

    private fun prepareFlow(
        id: Int,
        chunksFlow: Flow<String>,
        finishReason: String,
        sendDone: Boolean,
    ): Flow<String> {
        val timestamp = Instant.now().epochSecond
        return flow {
            emit(
                createChunk(
                    id = id,
                    created = timestamp,
                    role = "assistant",
                    content = "",
                ),
            )
            emitAll(
                chunksFlow.map {
                    createChunk(
                        id = id,
                        created = timestamp,
                        content = it,
                    )
                },
            )
            emit(
                createChunk(
                    id = id,
                    created = timestamp,
                    finishReason = finishReason,
                ),
            )
            if (sendDone) {
                emit("[DONE]")
            }
        }.map { "data:$it$LINE_SEPARATOR" }
    }

    @Suppress("LongParameterList")
    private fun createChunk(
        id: Int,
        created: Long,
        content: String? = null,
        role: String? = null,
        model: String = "gpt-4o-mini",
        finishReason: String? = null,
    ): String {
        val chunk =
            Chunk(
                id = "chatcmpl-$id",
                model = model,
                objectType = "chat.completion.chunk",
                choices =
                    listOf(
                        Choice(
                            index = 0,
                            delta = Delta(role = role, content = content),
                            logprobs = null,
                            finishReason = finishReason,
                        ),
                    ),
                created = created,
                systemFingerprint = "fp_44709d6fcb",
            )
        val string = Json.encodeToJsonElement(chunk).toString()
        return string
    }
}
