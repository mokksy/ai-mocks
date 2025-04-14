package me.kpavlov.mokksy.request

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
 * @param predicate the predicate to evaluate objects against
 * @return a [Matcher] that applies the given predicate to objects for evaluation
 */
internal fun <T> predicateMatcher(predicate: (T?) -> Boolean): Matcher<T?> =
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

        override fun toString(): String = "PredicateMatcher($predicate)"
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
