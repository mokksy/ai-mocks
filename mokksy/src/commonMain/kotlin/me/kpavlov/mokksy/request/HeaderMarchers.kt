package me.kpavlov.mokksy.request

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.ktor.http.Headers

/**
 * Custom matcher to verify that the Ktor [Headers] object contains a header with the specified name and value.
 */
internal fun containsHeader(
    name: String,
    value: String,
): Matcher<Headers> =
    object : Matcher<Headers> {
        override fun test(valueUnderTest: Headers): MatcherResult {
            val actualValue = valueUnderTest[name]
            return MatcherResult(
                actualValue == value, // Check if the actual header value matches the expected value
                {
                    "Headers should contain a header '$name' with value '$value', but was '$actualValue'."
                },
                { "Headers should not contain a header '$name' with value '$value', but it does." },
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
