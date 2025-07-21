package me.kpavlov.aimocks.ollama.embed

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

/**
 * Matcher for string embedding requests.
 *
 * @param input The string input to match
 */
internal class StringInputMatcher(private val input: String) : Matcher<Any?> {
    override fun test(value: Any?): MatcherResult {
        val passed = when (value) {
//            is EmbeddingsRequest -> value.input == input
            else -> false
        }

        return MatcherResult(
            passed,
            { "Request should have input \"$input\"" },
            { "Request should not have input \"$input\"" }
        )
    }
}

/**
 * Matcher for string list embedding requests.
 *
 * @param inputs The list of string inputs to match
 */
internal class StringListInputMatcher(private val inputs: List<String>) : Matcher<Any?> {
    override fun test(value: Any?): MatcherResult {
        val passed = when (value) {
//            is StringListEmbeddingsRequest -> value.input == inputs
            else -> false
        }

        return MatcherResult(
            passed,
            { "Request should have inputs $inputs" },
            { "Request should not have inputs $inputs" }
        )
    }
}
