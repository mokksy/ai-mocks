package me.kpavlov.aimocks.anthropic

import com.anthropic.models.Message
import com.anthropic.models.MessageCreateParams
import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.anthropic.StreamingResponseHelper.randomIdString
import me.kpavlov.aimocks.core.ChatResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Suppress("LongParameterList")
public class AnthropicMessagesResponseSpecification(
    response: AbstractResponseDefinition<Message>,
    public var messageId: String = randomIdString("msg_"),
    public var assistantContent: String = "",
    public var responseFlow: Flow<String>? = null,
    public var responseChunks: List<String>? = null,
    public var delayBetweenChunks: Duration = Duration.ZERO,
    public var delay: Duration = Duration.ZERO,
    public var finishReason: String = "stop",
) : ChatResponseSpecification<MessageCreateParams.Body, Message>(response = response) {
    public fun assistantContent(content: String): AnthropicMessagesResponseSpecification =
        apply {
            this.assistantContent =
                content
        }

    public fun delayMillis(value: Long): AnthropicMessagesResponseSpecification =
        apply {
            this.delay = value.milliseconds
        }

    public fun finishReason(finishReason: String): AnthropicMessagesResponseSpecification =
        apply {
            this.finishReason =
                finishReason
        }
}

@Suppress("LongParameterList")
public class AnthropicStreamingChatResponseSpecification(
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
) : ChatResponseSpecification<MessageCreateParams.Body, String>(response = response)
