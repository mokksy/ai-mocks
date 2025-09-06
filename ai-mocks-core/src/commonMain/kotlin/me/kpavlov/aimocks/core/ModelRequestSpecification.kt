package me.kpavlov.aimocks.core

import io.kotest.assertions.json.CompareJsonOptions
import io.kotest.assertions.json.equalJson
import io.kotest.matchers.Matcher
import io.kotest.matchers.string.contain
import io.kotest.matchers.string.containIgnoringCase
import me.kpavlov.mokksy.kotest.doesNotContain
import me.kpavlov.mokksy.kotest.doesNotContainIgnoringCase
import me.kpavlov.mokksy.kotest.objectEquals
import me.kpavlov.mokksy.request.predicateMatcher
import me.kpavlov.mokksy.request.successCallMatcher

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
    public fun temperature(temperature: Double): ModelRequestSpecification<P> = apply { this.temperature = temperature }

    public fun model(model: String): ModelRequestSpecification<P> = apply { this.model = model }

    public fun maxTokens(value: Long): ModelRequestSpecification<P> = apply { this.maxTokens = value }

    public fun maxTokens(value: Number): ModelRequestSpecification<P> = apply { this.maxTokens = value.toLong() }

    public fun topK(value: Long): ModelRequestSpecification<P> = apply { this.topK = value }

    public fun topK(value: Number): ModelRequestSpecification<P> = apply { this.topK = value.toLong() }

    public fun topP(value: Double): ModelRequestSpecification<P> = apply { this.topP = value }

    public fun topP(value: Number): ModelRequestSpecification<P> = apply { this.topP = value.toDouble() }

    /**
     * Adds a matcher to require that the request body string contains the specified substring.
     *
     * @param substring The substring that must be present in the request body.
     * @return This specification instance for fluent chaining.
     */
    public fun requestBodyContains(substring: String): ModelRequestSpecification<P> =
        apply {
            requestBodyString += contain(substring)
        }

    /**
     * Adds a matcher to require the request body object to be exactly equal to the specified object.
     *
     * @param requestObject The object that the request body must equal.
     * @return This specification instance for fluent chaining.
     */
    public fun requestBodyEquals(requestObject: P): ModelRequestSpecification<P> =
        apply {
            requestBody += objectEquals(requestObject, name = "request body")
        }

    /**
     * Adds a matcher that requires the request body string to exactly match the specified JSON payload.
     *
     * @param payload The expected JSON string for the request body.
     * @return This specification instance for fluent chaining.
     */
    public fun requestBodyEqualsJson(payload: String): ModelRequestSpecification<P> =
        apply {
            requestBodyString +=
                equalJson(
                    expected = payload,
                    options = CompareJsonOptions(),
                )
        }

    /**
     * Adds a matcher to require the request body string to be exactly equal to the specified value.
     *
     * @param payload The exact string the request body must match.
     * @return This specification instance for fluent chaining.
     */
    public fun requestBodyEquals(payload: String): ModelRequestSpecification<P> =
        apply {
            requestBodyString +=
                objectEquals(
                    payload,
                    name = "request body",
                )
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
    public fun requestBodyDoesNotContainsIgnoringCase(substring: String): ModelRequestSpecification<P> =
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

    /**
     * Adds a condition to verify that the request matches the specified predicate.
     *
     * This method allows users to define a custom predicate that will be applied
     * to the request, enabling more flexible and granular request matching.
     *
     * @param predicate A function that takes a request of type P as an input
     * and returns a Boolean indicating whether the condition is satisfied.
     * If request is `null` - it returns `false`.
     */
    public fun requestMatchesPredicate(predicate: (P) -> Boolean) {
        requestBody += predicateMatcher { it != null && predicate.invoke(it) }
    }

    /**
     * Adds a condition to verify that the request matches the specified matcher.
     *
     * This method allows users to define a custom matcher that will be applied
     * to the request, enabling more flexible and granular request matching.
     *
     * @param matcher A matcher that will be applied to the request.
     */
    public fun requestMatches(matcher: Matcher<P?>) {
        requestBody += matcher
    }

    /**
     * Adds a condition to verify that the request satisfies the specified call.
     *
     * This method allows users to define a custom call that will be applied
     * to the request, enabling more flexible and granular request matching.
     * The call is a function that takes a request of type P as an input
     * and should return successfully to satisfy the condition.
     *
     * @param description call description
     * @param call A function that takes a request of type P as an input.
     */
    public fun requestSatisfies(
        description: String? = null,
        call: (P?) -> Unit,
    ) {
        requestBody += successCallMatcher(description = description, call = call)
    }
}
