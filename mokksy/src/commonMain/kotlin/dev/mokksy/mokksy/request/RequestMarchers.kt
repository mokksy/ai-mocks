package dev.mokksy.mokksy.request

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.ktor.http.Headers
import io.ktor.http.HttpMethod

/**
 * Custom matcher to verify that the Ktor [Headers] object contains a header with the specified name and value.
 */
internal fun containsHeader(
    name: String,
    expectedValue: String,
): Matcher<Headers> =
    object : Matcher<Headers> {
        override fun test(value: Headers): MatcherResult {
            val actualValue = value[name]
            return MatcherResult(
                actualValue contentEquals expectedValue,
                {
                    "Headers should contain a header '$name' with value '$expectedValue', but was '$actualValue'."
                },
                {
                    "Headers should NOT contain a header '$name' with value '$expectedValue', but it does."
                },
            )
        }
    }

/**
 * Extension function for easier usage.
 */
public infix fun Headers.shouldHaveHeader(header: Pair<String, String>) {
    this should containsHeader(header.first, header.second)
}

/**
 * Extension function to assert that the headers should not contain a specific header.
 */
public infix fun Headers.shouldNotHaveHeader(header: Pair<String, String>) {
    this shouldNot containsHeader(header.first, header.second)
}

/**
 * Creates a matcher that evaluates objects against a specified predicate.
 *
 * @param T the type of the object being matched
 * @param description description of the predicate
 * @param predicate the predicate to evaluate objects against
 * @return a [Matcher] that applies the given predicate to objects for evaluation
 */
public fun <T> predicateMatcher(
    description: String? = null,
    predicate: (T?) -> Boolean,
): Matcher<T?> =
    object : Matcher<T?> {
        override fun test(value: T?): MatcherResult =
            MatcherResult(
                predicate.invoke(value),
                {
                    "Object '$value' should match predicate '$predicate'"
                },
                {
                    "Object '$value' should NOT match predicate '$predicate'"
                },
            )

        override fun toString(): String = description ?: "PredicateMatcher($predicate)"
    }

/**
 * Creates a matcher that tests whether the specified function `call` can execute successfully
 * without throwing an exception when invoked with a given input value.
 *
 * @param T The type of the input value being tested.
 *  @param description description of the call matcher
 * @param call A function that performs an operation using the input value of type `T?`.
 *             The matcher tests whether this function can execute successfully without errors.
 * @return A [Matcher] that evaluates if the `call` function can successfully execute when invoked
 *         with an input value of type `T?`.
 */
public fun <T> successCallMatcher(
    description: String? = null,
    call: (T?) -> Unit,
): Matcher<T?> =
    object : Matcher<T?> {
        override fun test(value: T?): MatcherResult {
            val passed =
                try {
                    call.invoke(value)
                    true
                } catch (_: Throwable) {
                    false
                }
            return MatcherResult(
                passed,
                {
                    "Object '$value' should satisfy '$call'"
                },
                {
                    "Object '$value' should NOT satisfy '$call'"
                },
            )
        }

        override fun toString(): String = description ?: "successCallMatcher($call)"
    }

internal fun pathEqual(expected: String): Matcher<String> =
    object : Matcher<String> {
        override fun test(value: String) =
            MatcherResult(
                value == expected,
                { "Path '$value' should be equal to '$expected'" },
                { "Path '$value' should not be equal to '$expected'" },
            )

        override fun toString(): String = "'$expected'"
    }

internal fun methodEqual(expected: HttpMethod): Matcher<HttpMethod> =
    object : Matcher<HttpMethod> {
        override fun test(value: HttpMethod) =
            MatcherResult(
                value == expected,
                { "Method $value should be equal to $expected" },
                { "Method $value should not be equal to $expected" },
            )

        override fun toString(): String = "$expected"
    }
