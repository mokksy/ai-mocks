package dev.mokksy.aimocks.anthropic

import dev.mokksy.aimocks.anthropic.StreamingResponseHelper.randomIdString
import dev.mokksy.aimocks.anthropic.model.Message
import dev.mokksy.aimocks.anthropic.model.MessageCreateParams
import dev.mokksy.aimocks.core.AbstractResponseSpecification
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import kotlinx.coroutines.flow.Flow
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
) : AbstractResponseSpecification<MessageCreateParams, Message>(
        response = response,
        delay = delay,
    ) {
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
