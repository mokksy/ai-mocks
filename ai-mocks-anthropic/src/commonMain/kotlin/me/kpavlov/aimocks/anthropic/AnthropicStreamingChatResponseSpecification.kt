package me.kpavlov.aimocks.anthropic

import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.anthropic.model.MessageCreateParams
import me.kpavlov.aimocks.core.AbstractStreamingResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

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
) : AbstractStreamingResponseSpecification<MessageCreateParams, String, String>(
        response = response,
        responseFlow = responseFlow,
        responseChunks = responseChunks,
        delayBetweenChunks = delayBetweenChunks,
        delay = delay,
    )
