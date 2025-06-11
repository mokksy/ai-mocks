package me.kpavlov.aimocks.anthropic

import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.anthropic.StreamingResponseHelper.randomIdString
import me.kpavlov.aimocks.anthropic.model.Message
import me.kpavlov.aimocks.anthropic.model.MessageCreateParams
import me.kpavlov.aimocks.core.ResponseSpecification
import me.kpavlov.aimocks.core.StreamingResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

@Suppress("LongParameterList")
public class AnthropicMessagesResponseSpecification(
    response: AbstractResponseDefinition<Message>,
    public var messageId: String = randomIdString("msg_"),
    public var assistantContent: String = "",
    public var responseFlow: Flow<String>? = null,
    public var responseChunks: List<String>? = null,
    public var delayBetweenChunks: Duration = Duration.ZERO,
    delay: Duration = Duration.ZERO,
    public var stopReason: String = "end_turn",
) : ResponseSpecification<MessageCreateParams, Message>(response = response, delay = delay) {
    public fun assistantContent(content: String): AnthropicMessagesResponseSpecification =
        apply {
            this.assistantContent =
                content
        }

    public fun finishReason(finishReason: String): AnthropicMessagesResponseSpecification =
        apply {
            this.stopReason =
                finishReason
        }
}

@Suppress("LongParameterList")
public class AnthropicStreamingChatResponseSpecification(
    response: AbstractResponseDefinition<String>,
    responseFlow: Flow<String>? = null,
    responseChunks: List<String>? = null,
    delayBetweenChunks: Duration = Duration.ZERO,
    delay: Duration = Duration.ZERO,
    public var stopReason: String = "end_turn",
    /**
     * Should send `[DONE]` at the end.
     */
    public var sendDone: Boolean = false,
) : StreamingResponseSpecification<MessageCreateParams, String, String>(
    response = response,
    responseFlow = responseFlow,
    responseChunks = responseChunks,
    delayBetweenChunks = delayBetweenChunks,
    delay = delay
)
