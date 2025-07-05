package me.kpavlov.aimocks.anthropic

import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.anthropic.StreamingResponseHelper.randomIdString
import me.kpavlov.aimocks.anthropic.model.Message
import me.kpavlov.aimocks.anthropic.model.MessageCreateParams
import me.kpavlov.aimocks.core.AbstractResponseSpecification
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
) : AbstractResponseSpecification<MessageCreateParams, Message>(
    response = response,
    delay = delay
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

