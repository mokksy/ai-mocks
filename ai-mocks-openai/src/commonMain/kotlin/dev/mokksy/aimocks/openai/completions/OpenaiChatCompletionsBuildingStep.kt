package dev.mokksy.aimocks.openai.completions

import dev.mokksy.aimocks.core.AbstractBuildingStep
import dev.mokksy.aimocks.openai.ChatCompletionRequest
import dev.mokksy.aimocks.openai.ChatResponse
import dev.mokksy.aimocks.openai.Choice
import dev.mokksy.aimocks.openai.Chunk
import dev.mokksy.aimocks.openai.CompletionTokensDetails
import dev.mokksy.aimocks.openai.Delta
import dev.mokksy.aimocks.openai.Message
import dev.mokksy.aimocks.openai.Usage
import dev.mokksy.aimocks.openai.model.ChatCompletionRole
import dev.mokksy.aimocks.openai.model.chat.MessageContent
import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer
import io.ktor.http.ContentType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.random.Random.Default.nextInt
import kotlin.time.Clock

private const val LINE_SEPARATOR = "\n\n"

/**
 * OpenaiChatCompletionsBuildingStep is a specialized implementation of [AbstractBuildingStep]
 * intended for constructing and managing chat completion responses as part of the OpenAI
 * Mock Server setup.
 *
 * The class provides features to create both single-blocked responses and streaming
 * responses for simulated chat completions using mock data.
 * It extends the functionality of [AbstractBuildingStep] by applying specific logic
 * for generating fake responses compliant with OpenAI's chat completion API.
 *
 * @constructor Initializes the building step with the provided mock server instance and
 *              a higher-level building step for configuring chat completion responses.
 *
 * @param mokksy The mock server instance used for handling mock request and response lifecycle.
 * @param buildingStep The underlying building step for managing and supporting response configurations
 *                     for OpenAI Chat Completion requests.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat">Chat Completions API</a>
 * @author Konstantin Pavlov
 */
public class OpenaiChatCompletionsBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<ChatCompletionRequest>,
) : AbstractBuildingStep<ChatCompletionRequest, OpenaiChatResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    @OptIn(ExperimentalAtomicApi::class)
    private val counter: AtomicLong = AtomicLong(1)

    @OptIn(ExperimentalAtomicApi::class)
    @Suppress("MagicNumber")
    override infix fun responds(block: suspend OpenaiChatResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val request = this.request.body()
            val chatResponseSpecification = OpenaiChatResponseSpecification()
            block.invoke(chatResponseSpecification)
            val assistantContent = chatResponseSpecification.assistantContent
            val finishReason = chatResponseSpecification.finishReason
            delay = chatResponseSpecification.delay
            contentType = ContentType.Application.Json

            val promptTokens = nextInt(1, 200)
            val completionTokens = nextInt(1, request.maxCompletionTokens ?: 500)
            val reasoningTokens = completionTokens / 3
            val acceptedPredictionTokens = (completionTokens - reasoningTokens) / 2
            val rejectedPredictionTokens =
                completionTokens - reasoningTokens - acceptedPredictionTokens

            body =
                ChatResponse(
                    id = "chatcmpl-abc${counter.addAndFetch(1)}",
                    objectType = "chat.completion",
                    created = Clock.System.now().epochSeconds,
                    model = request.model,
                    usage =
                        Usage(
                            promptTokens = promptTokens,
                            completionTokens = completionTokens,
                            totalTokens = promptTokens + completionTokens,
                            completionTokensDetails =
                                CompletionTokensDetails(
                                    reasoningTokens = reasoningTokens,
                                    acceptedPredictionTokens = acceptedPredictionTokens,
                                    rejectedPredictionTokens = rejectedPredictionTokens,
                                ),
                        ),
                    choices =
                        listOf(
                            Choice(
                                index = 0,
                                message =
                                    Message(
                                        role = ChatCompletionRole.ASSISTANT,
                                        content = MessageContent.Text(assistantContent),
                                    ),
                                finishReason = finishReason,
                            ),
                        ),
                    systemFingerprint = "fp_44709d6fcb",
                )
        }
    }

    /**
     * Configures a streaming response for a chat completions request by applying the provided specifications.
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
    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalAtomicApi::class)
    public infix fun respondsStream(block: OpenaiStreamingChatResponseSpecification.() -> Unit) {
        buildingStep.respondsWithStream {
            val responseSpec =
                OpenaiStreamingChatResponseSpecification()
            block.invoke(responseSpec)

            headers += "Content-Type" to "text/event-stream"
            headers += "Connection" to "keep-alive"

            val id = counter.addAndFetch(1)

            val chunkFlow = responseSpec.responseFlow ?: responseSpec.responseChunks?.asFlow()

            if (chunkFlow == null) {
                error("Either responseChunks or responseFlow must be defined")
            }
            val request = this.request.body()
            flow =
                prepareFlow(
                    id = id,
                    model = request.model,
                    chunksFlow = chunkFlow,
                    finishReason = responseSpec.finishReason,
                    sendDone = responseSpec.sendDone,
                )
        }
    }

    private fun prepareFlow(
        id: Long,
        model: String,
        chunksFlow: Flow<String>,
        finishReason: String,
        sendDone: Boolean,
    ): Flow<String> {
        val timestamp = Clock.System.now().epochSeconds
        return flow {
            emit(
                createChunk(
                    id = id,
                    created = timestamp,
                    model = model,
                    role = ChatCompletionRole.ASSISTANT,
                    content = "",
                ),
            )
            emitAll(
                chunksFlow.map {
                    createChunk(
                        id = id,
                        created = timestamp,
                        model = model,
                        content = it,
                    )
                },
            )
            emit(
                createChunk(
                    id = id,
                    created = timestamp,
                    model = model,
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
        id: Long,
        created: Long,
        content: String? = null,
        role: ChatCompletionRole? = null,
        model: String,
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
        return Json.encodeToString(chunk)
    }
}
