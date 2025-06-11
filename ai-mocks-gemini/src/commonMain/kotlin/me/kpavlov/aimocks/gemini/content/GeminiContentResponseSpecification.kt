package me.kpavlov.aimocks.gemini.content

import me.kpavlov.aimocks.core.ResponseSpecification
import me.kpavlov.aimocks.gemini.Candidate
import me.kpavlov.aimocks.gemini.Content
import me.kpavlov.aimocks.gemini.GenerateContentRequest
import me.kpavlov.aimocks.gemini.GenerateContentResponse
import me.kpavlov.aimocks.gemini.Part
import me.kpavlov.aimocks.gemini.PromptFeedback
import me.kpavlov.mokksy.response.AbstractResponseDefinition
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
) : ResponseSpecification<GenerateContentRequest, GenerateContentResponse>(
    response = response,
    delay = delay
) {
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
