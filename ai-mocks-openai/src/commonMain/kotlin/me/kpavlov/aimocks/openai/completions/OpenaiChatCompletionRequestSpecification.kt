package me.kpavlov.aimocks.openai.completions

import me.kpavlov.aimocks.core.ModelRequestSpecification
import me.kpavlov.aimocks.openai.ChatCompletionRequest

public open class OpenaiChatCompletionRequestSpecification(
    public var seed: Int? = null,
) : ModelRequestSpecification<ChatCompletionRequest>() {
    public fun seed(value: Int): OpenaiChatCompletionRequestSpecification =
        apply {
            this.seed =
                value
        }

    override fun systemMessageContains(substring: String) {
        requestBody.add(OpenaiCompletionsMatchers.systemMessageContains(substring))
    }

    override fun userMessageContains(substring: String) {
        requestBody.add(OpenaiCompletionsMatchers.userMessageContains(substring))
    }
}
