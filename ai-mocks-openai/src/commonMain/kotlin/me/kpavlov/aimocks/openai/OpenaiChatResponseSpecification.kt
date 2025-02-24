package me.kpavlov.aimocks.openai

import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.core.ChatResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition

public class OpenaiChatResponseSpecification(
    response: AbstractResponseDefinition<ChatCompletionRequest, String>,
    public var assistantContent: String = "",
    public var responseFlow: Flow<String>? = null,
    public var responseChunks: List<String>? = null,
    public var delayBetweenChunksMs: Long = 0,
    public var finishReason: String = "stop",
) : ChatResponseSpecification<ChatCompletionRequest, String>(response = response) {
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

public class OpenaiStreamingChatResponseSpecification(
    response: AbstractResponseDefinition<ChatCompletionRequest, String>,
    public var responseFlow: Flow<String>? = null,
    public var responseChunks: List<String>? = null,
    public var delayBetweenChunksMs: Long = 0,
    public var finishReason: String = "stop",
    /**
     * Should send `[DONE]` at the end.
     */
    public var sendDone: Boolean = false,
) : ChatResponseSpecification<ChatCompletionRequest, String>(response = response)
