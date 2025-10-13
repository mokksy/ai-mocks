package dev.mokksy.aimocks.gemini.content

import dev.mokksy.aimocks.core.AbstractStreamingResponseSpecification
import dev.mokksy.aimocks.gemini.GenerateContentRequest
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

/**
 * Specification for configuring a streaming Gemini content generation response.
 *
 * This class provides a fluent API for configuring the streaming response that will be returned
 * by the mock Gemini API when a content generation request is made with streaming enabled.
 *
 * @property chunks The chunks of content to include in the streaming response.
 * @property finishReason The reason why the model stopped generating tokens.
 */
public class GeminiStreamingContentResponseSpecification(
    response: AbstractResponseDefinition<String>,
    responseFlow: Flow<String>? = null,
    responseChunks: List<String>? = null,
    delayBetweenChunks: Duration = Duration.ZERO,
    delay: Duration = Duration.ZERO,
    public var finishReason: String = "STOP",
) : AbstractStreamingResponseSpecification<GenerateContentRequest, String, String>(
        response,
        responseFlow,
        responseChunks,
        delayBetweenChunks,
        delay,
    ) {
    /**
     * Sets the finish reason for the streaming response.
     *
     * @param finishReason The reason why the model stopped generating tokens.
     * @return This specification instance for method chaining.
     */
    public fun finishReason(finishReason: String): GeminiStreamingContentResponseSpecification =
        apply {
            this.finishReason = finishReason
        }
}
