package dev.mokksy.aimocks.anthropic

import dev.mokksy.aimocks.anthropic.model.MessageCreateParams
import dev.mokksy.aimocks.core.AbstractInferenceRequestSpecification

public open class AnthropicMessagesRequestSpecification(
    public var userId: String? = null,
) : AbstractInferenceRequestSpecification<MessageCreateParams>() {
    public fun userId(value: String): AnthropicMessagesRequestSpecification =
        apply { this.userId = value }

    override fun systemMessageContains(substring: String) {
        requestBody.add(AnthropicAiMatchers.systemMessageContains(substring))
    }

    override fun userMessageContains(substring: String) {
        requestBody.add(AnthropicAiMatchers.userMessageContains(substring))
    }
}
