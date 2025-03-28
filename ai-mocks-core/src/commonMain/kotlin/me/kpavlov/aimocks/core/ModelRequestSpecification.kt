package me.kpavlov.aimocks.core

import io.kotest.matchers.Matcher
import io.kotest.matchers.string.contain
import io.kotest.matchers.string.containIgnoringCase
import me.kpavlov.mokksy.kotest.doesNotContain
import me.kpavlov.mokksy.kotest.doesNotContainIgnoringCase

@Suppress("LongParameterList", "TooManyFunctions")
public abstract class ModelRequestSpecification<P>(
    public var temperature: Double? = null,
    public var maxTokens: Long? = null,
    public var topK: Long? = null,
    public var topP: Double? = null,
    public var model: String? = null,
    public val requestBody: MutableList<Matcher<P?>> = mutableListOf(),
    public val requestBodyString: MutableList<Matcher<String?>> = mutableListOf(),
) {
    public fun temperature(temperature: Double): ModelRequestSpecification<P> =
        apply { this.temperature = temperature }

    public fun model(model: String): ModelRequestSpecification<P> = apply { this.model = model }

    public fun maxTokens(value: Long): ModelRequestSpecification<P> =
        apply { this.maxTokens = value }

    public fun topK(value: Long): ModelRequestSpecification<P> = apply { this.topK = value }

    public fun topK(value: Number): ModelRequestSpecification<P> =
        apply { this.topP = value.toDouble() }

    /**
     * Adds a condition to ensure the request body contains the specified substring.
     *
     * @param substring The substring that the request body should contain.
     * @return The current instance of ChatRequestSpecification with the updated condition.
     */
    public fun requestBodyContains(substring: String): ModelRequestSpecification<P> =
        apply {
            requestBodyString += contain(substring)
        }

    /**
     * Adds a condition to ensure the request body contains the specified substring,
     * ignoring case sensitivity.
     *
     * @param substring The substring that the request body should contain, case-insensitive.
     * @return The current instance of ChatRequestSpecification with the updated condition.
     */
    public fun requestBodyContainsIgnoringCase(substring: String): ModelRequestSpecification<P> =
        apply {
            requestBodyString += containIgnoringCase(substring)
        }

    /**
     * Adds a condition to ensure the request body contains the specified substring,
     * ignoring case sensitivity.
     *
     * @param substring The substring that the request body should contain, case-insensitive.
     * @return The current instance of ChatRequestSpecification with the updated condition.
     */
    public fun requestBodyDoesNotContainsIgnoringCase(
        substring: String,
    ): ModelRequestSpecification<P> =
        apply {
            requestBodyString += doesNotContainIgnoringCase(substring)
        }

    /**
     * Adds a condition to ensure the request body contains the specified substring.
     *
     * @param substring The substring that the request body should contain, case-sensitive.
     * @return The current instance of ChatRequestSpecification with the updated condition.
     */
    public fun requestBodyDoesNotContains(substring: String): ModelRequestSpecification<P> =
        apply {
            requestBodyString += doesNotContain(substring)
        }

    /**
     * Specifies that the system/developer message must contain the provided string.
     *
     * @param substring The substring that the system message should contain.
     */
    public abstract fun systemMessageContains(substring: String)

    /**
     * Specifies that the user's message must contain the provided substring.
     *
     * @param substring The substring that the user's message should contain.
     */
    public abstract fun userMessageContains(substring: String)
}
