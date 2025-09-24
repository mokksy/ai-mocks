package me.kpavlov.aimocks.openai.moderation

import me.kpavlov.aimocks.core.ModelRequestSpecification
import me.kpavlov.aimocks.openai.model.moderation.CreateModerationRequest

/**
 * Request specification for OpenAI Moderation endpoint.
 */
public class OpenaiModerationRequestSpecification :
    ModelRequestSpecification<CreateModerationRequest>() {
    /**
     * Adds a matcher to ensure input contains the given substring.
     */
    public fun inputContains(substring: String): OpenaiModerationRequestSpecification =
        apply { requestBody.add(OpenaiModerationMatchers.inputContains(substring)) }
}
