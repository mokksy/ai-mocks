package dev.mokksy.aimocks.anthropic

import dev.mokksy.aimocks.anthropic.model.MessageCreateParams
import dev.mokksy.aimocks.core.AbstractStreamingResponseSpecification
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import kotlinx.coroutines.flow.Flow
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
