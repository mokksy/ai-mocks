package dev.mokksy.aimocks.gemini.content

import dev.mokksy.aimocks.gemini.GenerateContentRequest
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

/**
 * Utility object providing custom matchers for testing and validating attributes of
 * `GenerateContentRequest` instances. These matchers focus on checking the contents of the
 * messages within the `GenerateContentRequest` for specific roles such as "system" or "user".
 * @author Konstantin Pavlov
 */
internal object GeminiContentMatchers {
    fun systemMessageContains(string: String): Matcher<GenerateContentRequest?> =
        object : Matcher<GenerateContentRequest?> {
            override fun test(value: GenerateContentRequest?): MatcherResult =
                MatcherResult.Companion(
                    value != null &&
                        value.systemInstruction
                            ?.parts
                            ?.any { it.text?.contains(string) == true } == true,
                    { "System message should contain \"$string\"" },
                    { "System message should not contain \"$string\"" },
                )

            override fun toString(): String = "System message should contain \"$string\""
        }

    fun userMessageContains(string: String): Matcher<GenerateContentRequest?> =
        object : Matcher<GenerateContentRequest?> {
            override fun test(value: GenerateContentRequest?): MatcherResult =
                MatcherResult.Companion(
                    value != null &&
                        value.contents.any {
                            it.parts.any { part ->
                                part.text?.contains(string) == true
                            }
                        },
                    { "User message should contain \"$string\"" },
                    { "User message should not contain \"$string\"" },
                )

            override fun toString(): String = "User message should contain \"$string\""
        }
}
