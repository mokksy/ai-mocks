package dev.mokksy.aimocks.openai.moderation

import dev.mokksy.aimocks.core.ModelRequestSpecification
import dev.mokksy.aimocks.openai.model.moderation.CreateModerationRequest

/**
 * Request specification for OpenAI Moderation endpoint.
 *
 * @see <a href="https://platform.openai.com/docs/api-reference/moderations/create">Create Moderation</a>
 */
public class OpenaiModerationRequestSpecification :
    ModelRequestSpecification<CreateModerationRequest>() {
    /**
     * Adds a matcher to ensure input contains the given substring.
     */
    public fun inputContains(substring: String): OpenaiModerationRequestSpecification =
        apply { requestBody.add(OpenaiModerationMatchers.inputContains(substring)) }
}
