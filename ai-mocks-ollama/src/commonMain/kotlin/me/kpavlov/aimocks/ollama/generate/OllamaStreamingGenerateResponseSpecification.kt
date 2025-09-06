package me.kpavlov.aimocks.ollama.generate

import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.core.AbstractStreamingResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Specification for configuring streaming generate completion responses.
 *
 * This class is used to specify the content and behavior of streaming responses to generate completion requests.
 * It allows specifying the response chunks, response flow, done reason, and delay between chunks.
 *
 * @property doneReason The reason why the generation completed (e.g., "stop", "length")
 */
public class OllamaStreamingGenerateResponseSpecification(
    response: AbstractResponseDefinition<String>,
    responseFlow: Flow<String>? = null,
    responseChunks: List<String>? = null,
    delayBetweenChunks: Duration = 0.1.seconds,
    delay: Duration = 0.seconds,
    public var doneReason: String? = "stop",
) : AbstractStreamingResponseSpecification<GenerateRequest, String, String>(
        response = response,
        responseFlow = responseFlow,
        responseChunks = responseChunks,
        delayBetweenChunks = delayBetweenChunks,
        delay = delay,
    ) {
    /**
     * Sets the reason for completion of the generation response.
     *
     * @param reason The reason the generation finished (e.g., "stop", "length").
     * @return This specification instance for method chaining.
     */
    public fun doneReason(reason: String): OllamaStreamingGenerateResponseSpecification {
        this.doneReason = reason
        return this
    }
}
