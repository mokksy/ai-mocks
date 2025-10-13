package dev.mokksy.aimocks.openai.completions

import dev.mokksy.aimocks.core.AbstractResponseSpecification
import dev.mokksy.aimocks.core.ResponseSpecification
import dev.mokksy.aimocks.openai.ChatCompletionRequest
import dev.mokksy.aimocks.openai.ChatResponse
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

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
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/object">Chat Completion Object</a>
 * @author Konstantin Pavlov
 */
@Suppress("LongParameterList")
public class OpenaiChatResponseSpecification(
    response: AbstractResponseDefinition<ChatResponse>,
    public var assistantContent: String = "",
    public var responseFlow: Flow<String>? = null,
    public var responseChunks: List<String>? = null,
    public var delayBetweenChunks: Duration = Duration.ZERO,
    delay: Duration = Duration.ZERO,
    public var finishReason: String = "stop",
) : AbstractResponseSpecification<ChatCompletionRequest, ChatResponse>(
        response = response,
        delay = delay,
    ) {
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
}
