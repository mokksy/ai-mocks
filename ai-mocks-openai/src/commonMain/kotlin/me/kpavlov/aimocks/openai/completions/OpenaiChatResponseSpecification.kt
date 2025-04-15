package me.kpavlov.aimocks.openai.completions

import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.core.ResponseSpecification
import me.kpavlov.aimocks.openai.ChatCompletionRequest
import me.kpavlov.aimocks.openai.ChatResponse
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * A specification class for defining and customizing the response to a chat completion request
 * in an OpenAI-like conversational environment. Allows the configuration of the assistant response
 * content, response handling flow, chunking behavior, delays, and the designated finish reason.
 *
 * This class extends [ResponseSpecification] and provides additional parameters specific
 * to OpenAI chat responses such as assistant content manipulation and timing controls.
 *
 * @constructor Creates an instance of OpenaiChatResponseSpecification.
 *
 * @param response The base response definition of type [AbstractResponseDefinition] for the chat response.
 * @param assistantContent The initial assistant response content.
 * @param responseFlow A flow of string-based response chunks to be emitted sequentially.
 * @param responseChunks A list of pre-defined response content chunks.
 * @param delayBetweenChunks The delay applied between emitting each response chunk from the flow.
 * @param delay The global delay applied to the response before sending it.
 * @param finishReason The reason indicating why the completion process finished, such as "stop".
 * @author Konstantin Pavlov
 */
@Suppress("LongParameterList")
public class OpenaiChatResponseSpecification(
    response: AbstractResponseDefinition<ChatResponse>,
    public var assistantContent: String = "",
    public var responseFlow: Flow<String>? = null,
    public var responseChunks: List<String>? = null,
    public var delayBetweenChunks: Duration = Duration.ZERO,
    public var delay: Duration = Duration.ZERO,
    public var finishReason: String = "stop",
) : ResponseSpecification<ChatCompletionRequest, ChatResponse>(response = response) {
    public fun assistantContent(content: String): OpenaiChatResponseSpecification =
        apply {
            this.assistantContent =
                content
        }

    public fun finishReason(finishReason: String): OpenaiChatResponseSpecification =
        apply {
            this.finishReason =
                finishReason
        }

    public fun delayMillis(value: Long): OpenaiChatResponseSpecification =
        apply {
            this.delay = value.milliseconds
        }
}
