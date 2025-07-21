package me.kpavlov.aimocks.ollama.generate

import me.kpavlov.aimocks.core.AbstractResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Specification for configuring generate completion responses.
 *
 * This class is used to specify the content and behavior of responses to generate completion requests.
 * It allows specifying the response content, done reason, and delay.
 *
 * @property responseContent The content of the response
 * @property doneReason The reason why the generation completed (e.g., "stop", "length")
 */
public class OllamaGenerateResponseSpecification(
    response: AbstractResponseDefinition<GenerateResponse>,
    public var responseContent: String = "This is a mock response from Ollama.",
    public var doneReason: String? = "stop",
    delay: Duration = 0.seconds,
) : AbstractResponseSpecification<GenerateRequest, GenerateResponse>(
    response = response,
    delay = delay,
) {
    /**
     * Specifies the content of the response.
     *
     * @param content The response content
     * @return This specification for method chaining
     */
    public fun content(content: String): OllamaGenerateResponseSpecification {
        this.responseContent = content
        return this
    }

    /**
     * Specifies the reason why the generation completed.
     *
     * @param reason The done reason (e.g., "stop", "length")
     * @return This specification for method chaining
     */
    public fun doneReason(reason: String): OllamaGenerateResponseSpecification {
        this.doneReason = reason
        return this
    }
}
