package me.kpavlov.aimocks.anthropic

import me.kpavlov.aimocks.anthropic.model.MessageCreateParams
import me.kpavlov.aimocks.core.AbstractInferenceRequestSpecification

public open class AnthropicMessagesRequestSpecification(
    public var userId: String? = null,
) : AbstractInferenceRequestSpecification<MessageCreateParams>() {
    public fun userId(value: String): AnthropicMessagesRequestSpecification = apply { this.userId = value }

    override fun systemMessageContains(substring: String) {
        requestBody.add(AnthropicAiMatchers.systemMessageContains(substring))
    }

    override fun userMessageContains(substring: String) {
        requestBody.add(AnthropicAiMatchers.userMessageContains(substring))
    }
}
