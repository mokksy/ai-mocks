package me.kpavlov.aimocks.openai.completions

import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.core.AbstractStreamingResponseSpecification
import me.kpavlov.aimocks.core.ResponseSpecification
import me.kpavlov.aimocks.openai.ChatCompletionRequest
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

/**
 * A specification class for defining and customizing the streaming chat response behavior
 * in an OpenAI-like environment. This class provides configuration options for managing
 * streaming responses, delays, chunking, and signaling the end of the response.
 *
 * This class extends [ResponseSpecification] to offer additional parameters
 * aimed at handling streaming chat responses.
 *
 * @constructor Creates an instance of OpenaiStreamingChatResponseSpecification.
 *
 * @param response The base response definition of type [AbstractResponseDefinition] for the streaming response.
 * @param responseFlow A flow of string-based response chunks to be emitted sequentially
 * as part of the streaming behavior.
 * @param responseChunks A predefined list of response content chunks to be used in the streaming response.
 * @param delayBetweenChunks The delay duration inserted between emitting each response chunk from the flow.
 * @param delay A global delay applied to the entire response before it is initiated.
 * @param finishReason A descriptor to indicate the reason for completion of the response, such as "stop".
 * @param sendDone A flag indicating whether the special marker `[DONE]`
 * should be sent at the end of the streaming response.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/streaming">Chat Completion Streaming</a>
 * @author Konstantin Pavlov
 */
@Suppress("LongParameterList")
public class OpenaiStreamingChatResponseSpecification(
    response: AbstractResponseDefinition<String>,
    responseFlow: Flow<String>? = null,
    responseChunks: List<String>? = null,
    delayBetweenChunks: Duration = Duration.ZERO,
    delay: Duration = Duration.ZERO,
    public var finishReason: String = "stop",
    /**
     * Should send `[DONE]` at the end.
     */
    public var sendDone: Boolean = false,
) : AbstractStreamingResponseSpecification<ChatCompletionRequest, String, String>(
        response = response,
        responseFlow = responseFlow,
        responseChunks = responseChunks,
        delayBetweenChunks = delayBetweenChunks,
        delay = delay,
    )
