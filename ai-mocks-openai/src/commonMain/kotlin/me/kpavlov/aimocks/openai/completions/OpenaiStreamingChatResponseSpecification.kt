package me.kpavlov.aimocks.openai.completions

import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.core.ChatResponseSpecification
import me.kpavlov.aimocks.openai.ChatCompletionRequest
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

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
