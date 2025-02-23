package me.kpavlov.aimocks.openai

import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.core.ChatResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition

public class OpenaiChatResponseSpecification(
    response: AbstractResponseDefinition<ChatCompletionRequest, String>,
    public var textContent: String = "",
    public var responseFlow: Flow<String>? = null,
    public var responseChunks: List<String>? = null,
    public var delayBetweenChunksMs: Long = 0,
    public var finishReason: String = "stop",
) : ChatResponseSpecification<ChatCompletionRequest, String>(response = response) {
    public fun textContent(textContent: String): OpenaiChatResponseSpecification =
        apply {
            this.textContent =
                textContent
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
