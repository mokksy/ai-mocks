package me.kpavlov.aimocks.openai.responses

import io.kotest.matchers.string.contain
import me.kpavlov.aimocks.core.ModelRequestSpecification

public open class OpenaiResponsesRequestSpecification(
    public var seed: Int? = null,
) : ModelRequestSpecification<CreateResponseRequest>() {
    public fun seed(value: Int): OpenaiResponsesRequestSpecification =
        apply {
            this.seed =
                value
        }

    override fun systemMessageContains(substring: String) {
        requestBodyString += contain(substring)
//         requestBody.add(OpenAiMatchers.systemMessageContains(substring))
    }

    override fun userMessageContains(substring: String) {
        requestBodyString += contain(substring)
        // requestBody.add(OpenAiMatchers.userMessageContains(substring))
    }
}
