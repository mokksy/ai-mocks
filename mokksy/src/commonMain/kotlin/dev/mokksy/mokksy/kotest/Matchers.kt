package dev.mokksy.mokksy.kotest

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

/**
 * Creates a matcher that checks if a given string does not contain the specified substring,
 * ignoring case sensitivity.
 *
 * The check also supports null values, treating them as not containing any substring.
 *
 * @param substr The substring to check for, ignoring case sensitivity.
 * @return A Matcher instance that evaluates the given condition.
 */
public fun doesNotContainIgnoringCase(substr: String): Matcher<String?> =
    Matcher { value ->
        MatcherResult(
            value == null || value.lowercase().indexOf(substr.lowercase()) == -1,
            {
                "${value.print().value} should not contain the substring ${substr.print().value} (case insensitive)"
            },
            {
                "${value.print().value} should contain the substring ${substr.print().value} (case insensitive)"
            },
        )
    }

/**
 * Returns a matcher that verifies a nullable string does not contain the specified substring, using case-sensitive comparison.
 *
 * Null values are considered as not containing any substring.
 *
 * @param substr The substring to check for.
 * @return A matcher that succeeds if the substring is absent from the string.
 */
public fun doesNotContain(substr: String): Matcher<String?> =
    Matcher { value ->
        MatcherResult(
            value == null || value.indexOf(substr) == -1,
            {
                "${value.print().value} should not contain the substring ${substr.print().value} (case sensitive)"
            },
            {
                "${value.print().value} should contain the substring ${substr.print().value} (case sensitive)"
            },
        )
    }

/**
 * Returns a matcher that verifies whether the actual value is equal to the specified expected object.
 *
 * The provided name is used in failure messages to identify the parameter being checked.
 *
 * @param request The expected object to compare against.
 * @param name The identifier used in error messages.
 * @return A matcher that succeeds if the actual value equals the expected object.
 */
public fun <T : Any> objectEquals(
    request: T?,
    name: String,
): Matcher<T?> =
    object : Matcher<T?> {
        override fun test(value: T?): MatcherResult =
            MatcherResult(
                value == request,
                { "$name should be equal to $request" },
                { "$name should NOT be equal to $request" },
            )

        /**
         * Returns a string describing the expected equality condition for use in matcher output.
         *
         * @return A message indicating that the value should be equal to the expected object.
         */
        override fun toString(): String = "$name should be equal to $request"
    }
