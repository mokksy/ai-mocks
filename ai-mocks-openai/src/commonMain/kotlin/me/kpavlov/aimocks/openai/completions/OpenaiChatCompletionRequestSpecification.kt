package me.kpavlov.aimocks.openai.completions

import me.kpavlov.aimocks.core.ModelRequestSpecification
import me.kpavlov.aimocks.openai.ChatCompletionRequest

/**
 * Represents the specification for an OpenAI chat completion request.
 *
 * Extends [ModelRequestSpecification] specifically for configuring and validating
 * parameters related to `ChatCompletionRequest` objects. This includes overriding
 * methods to add matchers for specific conditions such as ensuring messages
 * from the system or user contain specified substrings.
 *
 * @constructor Creates an instance with optional parameters for initializing configuration.
 * @property seed An optional seed value for deterministic behavior in chat completions.
 *
 * @author Konstantin Pavlov
 */
public open class OpenaiChatCompletionRequestSpecification(
    public var seed: Int? = null,
) : ModelRequestSpecification<ChatCompletionRequest>() {
    public fun seed(value: Int): OpenaiChatCompletionRequestSpecification =
        apply {
            this.seed = value
        }

    override fun systemMessageContains(substring: String) {
        requestBody.add(OpenaiCompletionsMatchers.systemMessageContains(substring))
    }

    override fun userMessageContains(substring: String) {
        requestBody.add(OpenaiCompletionsMatchers.userMessageContains(substring))
    }
}
