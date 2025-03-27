package me.kpavlov.mokksy.kotest

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
    Matcher<String?> { value ->
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
 * Creates a matcher that checks if a given string does not contain the specified substring.
 *
 * The check also supports null values, treating them as not containing any substring.
 *
 * @param substr The substring to check for.
 * @return A Matcher instance that evaluates the given condition.
 */
public fun doesNotContain(substr: String): Matcher<String?> =
    Matcher<String?> { value ->
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
