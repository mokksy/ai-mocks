package me.kpavlov.aimocks.openai

import me.kpavlov.aimocks.core.ChatRequestSpecification

public open class OpenaiChatRequestSpecification(
    public var seed: Int? = null,
) : ChatRequestSpecification<ChatCompletionRequest>() {
    public fun seed(value: Int): OpenaiChatRequestSpecification = apply { this.seed = value }

    override fun systemMessageContains(substring: String) {
        requestBody.add(OpenAiMatchers.systemMessageContains(substring))
    }

    override fun userMessageContains(substring: String) {
        requestBody.add(OpenAiMatchers.userMessageContains(substring))
    }
}
