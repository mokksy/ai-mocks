package dev.mokksy.aimocks.core

import dev.mokksy.mokksy.kotest.doesNotContain
import dev.mokksy.mokksy.kotest.doesNotContainIgnoringCase
import dev.mokksy.mokksy.kotest.objectEquals
import dev.mokksy.mokksy.request.predicateMatcher
import dev.mokksy.mokksy.request.successCallMatcher
import io.kotest.assertions.json.CompareJsonOptions
import io.kotest.assertions.json.equalJson
import io.kotest.matchers.Matcher
import io.kotest.matchers.string.contain
import io.kotest.matchers.string.containIgnoringCase

/**
 * Represents a specification model for processing and asserting certain conditions on a request body.
 *
 * This abstract class allows configuring conditions for matching and validating the attributes
 * or structure of the request body, either as a whole object or in its string representation.
 * Suitable for cases where flexible request validation is needed.
 *
 * @param P The type parameter specifying the type of object the request body is expected to be.
 */
@Suppress("TooManyFunctions")
public abstract class ModelRequestSpecification<P>(
    public var model: String? = null,
    public val requestBody: MutableList<Matcher<P?>> = mutableListOf(),
    public val requestBodyString: MutableList<Matcher<String?>> = mutableListOf(),
) {
    public fun model(model: String): ModelRequestSpecification<P> = apply { this.model = model }

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
     * @return The current instance of [ModelRequestSpecification] with the updated condition.
     */
    public fun requestBodyContainsIgnoringCase(substring: String): ModelRequestSpecification<P> =
        apply {
            requestBodyString += containIgnoringCase(substring)
        }

    /**
     * Adds a condition to ensure the request body does not contain the specified substring,
     * ignoring case sensitivity.
     *
     * @param substring The substring that the request body should not contain, case-insensitive.
     * @return The current instance of [ModelRequestSpecification] with the updated condition.
     */
    public fun requestBodyDoesNotContainsIgnoringCase(
        substring: String,
    ): ModelRequestSpecification<P> =
        apply {
            requestBodyString += doesNotContainIgnoringCase(substring)
        }

    /**
     * Adds a condition to ensure the request body does not contain the specified substring.
     *
     * @param substring The substring that the request body should not contain, case-sensitive.
     * @return The current instance of [ModelRequestSpecification] with the updated condition.
     */
    public fun requestBodyDoesNotContains(substring: String): ModelRequestSpecification<P> =
        apply {
            requestBodyString += doesNotContain(substring)
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
