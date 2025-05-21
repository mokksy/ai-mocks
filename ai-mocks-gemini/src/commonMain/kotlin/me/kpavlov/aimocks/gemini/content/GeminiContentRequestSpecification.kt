package me.kpavlov.aimocks.gemini.content

import me.kpavlov.aimocks.core.ModelRequestSpecification
import me.kpavlov.aimocks.gemini.GenerateContentRequest

/**
 * Represents the specification for a Gemini content generation request.
 *
 * Extends [ModelRequestSpecification] specifically for configuring and validating
 * parameters related to `GenerateContentRequest` objects. This includes overriding
 * methods to add matchers for specific conditions such as ensuring messages
 * from the system or user contain specified substrings.
 *
 * @constructor Creates an instance with optional parameters for initializing configuration.
 * @property temperature An optional temperature value for controlling randomness in content generation.
 * @property maxOutputTokens An optional maximum number of tokens to generate.
 *
 * @author Konstantin Pavlov
 */
public open class GeminiContentRequestSpecification(
    public var project: String? = null,
    public var location: String? = null,
    public var path: String? = null,
    maxOutputTokens: Int? = null,
) : ModelRequestSpecification<GenerateContentRequest>() {
    public var maxOutputTokens: Int? = maxOutputTokens
        private set

    public fun maxOutputTokens(value: Int): GeminiContentRequestSpecification =
        apply {
            this.maxOutputTokens = value
        }

    public fun project(value: String): GeminiContentRequestSpecification =
        apply {
            this.project = value
        }

    public fun location(value: String): GeminiContentRequestSpecification =
        apply {
            this.location = value
        }

    override fun systemMessageContains(substring: String) {
        requestBody.add(GeminiContentMatchers.systemMessageContains(substring))
    }

    override fun userMessageContains(substring: String) {
        requestBody.add(GeminiContentMatchers.userMessageContains(substring))
    }

    public fun path(value: String) {
        apply {
            this.path = value
        }
    }
}
