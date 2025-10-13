package dev.mokksy.aimocks.core

import io.kotest.matchers.Matcher

/**
 * Abstract class for defining specifications for inference requests.
 * Provides functionality for configuring parameters and specifying conditions
 * related to system and user messages within the request.
 *
 * @param P The type of the request body.
 * @property temperature Optional temperature parameter to control randomness in the inference process.
 * @property maxTokens Optional maximum number of tokens to generate in the response.
 * @property topK Optional top-k sampling parameter specifying how many top tokens to consider during sampling.
 * @property topP Optional top-p (nucleus) sampling parameter to control the probability mass of tokens to consider.
 * @param model Optional model identifier.
 * @param requestBody A mutable list of matchers representing conditions on the request body.
 * @param requestBodyString A mutable list of matchers representing conditions on the request body string.
 */
@Suppress("LongParameterList", "TooManyFunctions")
public abstract class AbstractInferenceRequestSpecification<P>(
    public var temperature: Double? = null,
    public var maxTokens: Long? = null,
    public var topK: Long? = null,
    public var topP: Double? = null,
    model: String? = null,
    requestBody: MutableList<Matcher<P?>> = mutableListOf(),
    requestBodyString: MutableList<Matcher<String?>> = mutableListOf(),
) : ModelRequestSpecification<P>(
        model = model,
        requestBody = requestBody,
        requestBodyString = requestBodyString,
    ) {
    public fun temperature(temperature: Double): AbstractInferenceRequestSpecification<P> =
        apply { this.temperature = temperature }

    public fun maxTokens(value: Long): AbstractInferenceRequestSpecification<P> =
        apply { this.maxTokens = value }

    public fun maxTokens(value: Number): AbstractInferenceRequestSpecification<P> =
        apply { this.maxTokens = value.toLong() }

    public fun topK(value: Long): AbstractInferenceRequestSpecification<P> =
        apply { this.topK = value }

    public fun topK(value: Number): AbstractInferenceRequestSpecification<P> =
        apply { this.topK = value.toLong() }

    public fun topP(value: Double): AbstractInferenceRequestSpecification<P> =
        apply { this.topP = value }

    public fun topP(value: Number): AbstractInferenceRequestSpecification<P> =
        apply { this.topP = value.toDouble() }

    /**
     * Specifies that the system/developer message must contain the provided string.
     *
     * @param substring The substring that the system message should contain.
     */
    public abstract fun systemMessageContains(substring: String)

    /**
     * Specifies that the system/developer message must contain a dynamically constructed string.
     * The string can be built using a lambda with a [StringBuilder] receiver.
     *
     * @param builderAction A lambda with a receiver of type [StringBuilder], used to build the
     * desired content that the system message should contain.
     */
    public inline fun systemMessageContains(builderAction: StringBuilder.() -> Unit) {
        val stringBuilder = StringBuilder()
        builderAction.invoke(stringBuilder)
        systemMessageContains(stringBuilder.toString())
    }

    /**
     * Specifies that the user's message must contain the provided substring.
     *
     * @param substring The substring that the user's message should contain.
     */
    public abstract fun userMessageContains(substring: String)

    /**
     * Specifies a condition that the user's message must contain a dynamically constructed string.
     * The string can be built using a lambda with a [StringBuilder] receiver.
     *
     * @param builderAction A lambda with a receiver of type [StringBuilder], used to build the desired content
     * that the user's message should contain.
     */
    public inline fun userMessageContains(builderAction: StringBuilder.() -> Unit) {
        val stringBuilder = StringBuilder()
        builderAction.invoke(stringBuilder)
        userMessageContains(stringBuilder.toString())
    }
}
