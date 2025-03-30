package me.kpavlov.aimocks.openai.responses

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

internal object OpenAiResponsesMatchers {
    fun instructionsContains(string: String): Matcher<CreateResponseRequest?> =
        object : Matcher<CreateResponseRequest?> {
            override fun test(value: CreateResponseRequest?): MatcherResult =
                MatcherResult(
                    value != null &&
                        value.instructions
                            ?.contains(string) == true,
                    { "Instructions should contain \"$string\"" },
                    { "Instructions should not contain \"$string\"" },
                )

            override fun toString(): String = "Instructions should contain \"$string\""
        }

    fun containsInputImageWithUrl(string: String): Matcher<CreateResponseRequest?> =
        object : Matcher<CreateResponseRequest?> {
            override fun test(value: CreateResponseRequest?): MatcherResult {
                val passed =
                    if (value != null) {
                        false
                    } else {
                        TODO()
                    }

                return MatcherResult(
                    passed,
                    { "Instructions should contain \"$string\"" },
                    { "Instructions should not contain \"$string\"" },
                )
            }

            override fun toString(): String = "InputImage should have URL \"$string\""
        }
}
