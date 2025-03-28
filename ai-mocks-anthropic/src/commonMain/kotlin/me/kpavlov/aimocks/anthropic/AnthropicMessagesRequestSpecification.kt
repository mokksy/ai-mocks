package me.kpavlov.aimocks.anthropic

import com.anthropic.models.messages.MessageCreateParams
import me.kpavlov.aimocks.core.ModelRequestSpecification

public open class AnthropicMessagesRequestSpecification(
    public var userId: String? = null,
) : ModelRequestSpecification<MessageCreateParams.Body>() {
    public fun userId(value: String): AnthropicMessagesRequestSpecification =
        apply { this.userId = value }

    override fun systemMessageContains(substring: String) {
        requestBody.add(AnthropicAiMatchers.systemMessageContains(substring))
    }

    override fun userMessageContains(substring: String) {
        requestBody.add(AnthropicAiMatchers.userMessageContains(substring))
    }
}
