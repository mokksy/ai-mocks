package dev.mokksy.aimocks.gemini.content

import dev.mokksy.aimocks.core.AbstractInferenceRequestSpecification
import dev.mokksy.aimocks.core.ModelRequestSpecification
import dev.mokksy.aimocks.gemini.GenerateContentRequest

/**
 * Represents the specification for a Gemini content generation request.
 *
 * Extends [ModelRequestSpecification] specifically for configuring and validating
 * parameters related to `GenerateContentRequest` objects. This includes overriding
 * methods to add matchers for specific conditions, such as ensuring messages
 * from the system or user contain specified substrings.
 *
 * @constructor Creates an instance with optional parameters for initializing configuration.
 * @property project Google project ID
 * @property location Google location
 * @property path full request path
 * @property apiVersion Either `v1` or `v1beta1`. Default is `v1`.
 *          See [API versions explained](https://ai.google.dev/gemini-api/docs/api-versions)
 * @property maxOutputTokens An optional maximum number of tokens to generate.
 *
 * @author Konstantin Pavlov
 */
public open class GeminiContentRequestSpecification(
    public var project: String? = null,
    public var location: String? = null,
    public var apiVersion: String = "v1",
    public var path: String? = null,
    public var seed: Number? = null,
    maxOutputTokens: Int? = null,
) : AbstractInferenceRequestSpecification<GenerateContentRequest>() {
    public var maxOutputTokens: Int? = maxOutputTokens
        private set

    public fun maxOutputTokens(value: Number): GeminiContentRequestSpecification =
        apply {
            this.maxOutputTokens = value.toInt()
        }

    /**
     * Configures the API version to "v1beta1" for Gemini content-related requests.
     *
     * @return An instance of `GeminiContentRequestSpecification` configured with the beta API version.
     */
    public fun betaApi(): GeminiContentRequestSpecification = apiVersion("v1beta1")

    /**
     * Configures the API version to "v1" for Gemini content-related requests.
     *
     * @return An instance of `GeminiContentRequestSpecification` configured with the stable API version.
     */
    public fun stableApi(): GeminiContentRequestSpecification = apiVersion("v1")

    public fun apiVersion(value: String): GeminiContentRequestSpecification =
        apply {
            this.apiVersion = value
        }

    public fun project(value: String): GeminiContentRequestSpecification =
        apply {
            this.project = value
        }

    public fun seed(value: Number): GeminiContentRequestSpecification =
        apply {
            this.seed = value
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
