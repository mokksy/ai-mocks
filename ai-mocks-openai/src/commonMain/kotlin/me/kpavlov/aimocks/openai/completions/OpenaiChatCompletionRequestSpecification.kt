package me.kpavlov.aimocks.openai.completions

import me.kpavlov.aimocks.core.ModelRequestSpecification
import me.kpavlov.aimocks.openai.ChatCompletionRequest
import me.kpavlov.aimocks.openai.OpenAiMatchers

public open class OpenaiChatCompletionRequestSpecification(
    public var seed: Int? = null,
) : ModelRequestSpecification<ChatCompletionRequest>() {
    public fun seed(value: Int): OpenaiChatCompletionRequestSpecification =
        apply {
            this.seed =
                value
        }

    override fun systemMessageContains(substring: String) {
        requestBody.add(OpenAiMatchers.systemMessageContains(substring))
    }

    override fun userMessageContains(substring: String) {
        requestBody.add(OpenAiMatchers.userMessageContains(substring))
    }
}
