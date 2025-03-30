package me.kpavlov.aimocks.openai.completions

import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.core.ChatResponseSpecification
import me.kpavlov.aimocks.openai.ChatCompletionRequest
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

/**
 * A specification class for defining and customizing the streaming chat response behavior
 * in an OpenAI-like environment. This class provides configuration options for managing
 * streaming responses, delays, chunking, and signaling the end of the response.
 *
 * This class extends [ChatResponseSpecification] to offer additional parameters
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
 * @author Konstantin Pavlov
 */
@Suppress("LongParameterList")
public class OpenaiStreamingChatResponseSpecification(
    response: AbstractResponseDefinition<String>,
    public var responseFlow: Flow<String>? = null,
    public var responseChunks: List<String>? = null,
    public var delayBetweenChunks: Duration = Duration.ZERO,
    public var delay: Duration = Duration.ZERO,
    public var finishReason: String = "stop",
    /**
     * Should send `[DONE]` at the end.
     */
    public var sendDone: Boolean = false,
) : ChatResponseSpecification<ChatCompletionRequest, String>(response = response)
