package dev.mokksy.aimocks.gemini.content

import dev.mokksy.aimocks.core.AbstractResponseSpecification
import dev.mokksy.aimocks.gemini.Candidate
import dev.mokksy.aimocks.gemini.Content
import dev.mokksy.aimocks.gemini.GenerateContentRequest
import dev.mokksy.aimocks.gemini.GenerateContentResponse
import dev.mokksy.aimocks.gemini.Part
import dev.mokksy.aimocks.gemini.PromptFeedback
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

/**
 * Specification for configuring a Gemini content generation response.
 *
 * This class provides a fluent API for configuring the response that will be returned
 * by the mock Gemini API when a content generation request is made.
 *
 * @property content The content to include in the response.
 * @property finishReason The reason why the model stopped generating tokens.
 */
public class GeminiContentResponseSpecification(
    response: AbstractResponseDefinition<GenerateContentResponse>,
    public var content: String = "This is a mock response from Gemini API.",
    public var finishReason: String = "STOP",
    public var role: String = "model",
    delay: Duration = Duration.ZERO,
) : AbstractResponseSpecification<GenerateContentRequest, GenerateContentResponse>(
        response = response,
        delay = delay,
    ) {
    public fun assistantContent(value: String): GeminiContentResponseSpecification = content(value)

    /**
     * Sets the content of the response.
     *
     * @param content The content to include in the response.
     * @return This specification instance for method chaining.
     */
    public fun content(content: String): GeminiContentResponseSpecification =
        apply {
            this.content = content
        }

    /**
     * Sets the finish reason for the response.
     *
     * @param finishReason The reason why the model stopped generating tokens.
     * @return This specification instance for method chaining.
     */
    public fun finishReason(finishReason: String): GeminiContentResponseSpecification =
        apply {
            this.finishReason = finishReason
        }

    /**
     * Sets the role for the response content.
     *
     * @param role The role of the content (e.g., "model").
     * @return This specification instance for method chaining.
     */
    public fun role(role: String): GeminiContentResponseSpecification =
        apply {
            this.role = role
        }

    /**
     * Builds a GenerateContentResponse based on the current specification.
     *
     * @return A GenerateContentResponse object configured according to this specification.
     */
    public fun build(): GenerateContentResponse {
        val candidate =
            Candidate(
                content =
                    Content(
                        parts =
                            listOf(
                                Part(
                                    text = content,
                                ),
                            ),
                        role = role,
                    ),
                finishReason = finishReason,
            )

        return GenerateContentResponse(
            candidates = listOf(candidate),
            promptFeedback =
                PromptFeedback(
                    safetyRatings = null,
                ),
            modelVersion = "gemini-2.5-flash-preview-04-17",
        )
    }
}
