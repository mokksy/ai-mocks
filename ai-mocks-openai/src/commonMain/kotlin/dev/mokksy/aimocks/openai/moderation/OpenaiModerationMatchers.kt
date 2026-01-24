package dev.mokksy.aimocks.openai.moderation

import dev.mokksy.aimocks.openai.model.moderation.CreateModerationRequest
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

/**
 * Matchers for OpenAI Moderation requests.
 */
internal object OpenaiModerationMatchers {
    /**
     * Checks that the CreateModerationRequest.input contains a string that includes [substring].
     */
    fun inputContains(substring: String): Matcher<CreateModerationRequest?> =
        object : Matcher<CreateModerationRequest?> {
            override fun test(value: CreateModerationRequest?): MatcherResult {
                val inputs = value?.input.orEmpty()
                val (passed, reason) =
                    when {
                        value == null -> {
                            false to "request was null"
                        }

                        inputs.isEmpty() -> {
                            false to "input was empty"
                        }

                        else -> {
                            val found = inputs.any { it.contains(substring) }
                            found to if (!found) "no input contained \"$substring\"" else ""
                        }
                    }
                return MatcherResult(
                    passed,
                    {
                        "Expected at least one input to contain \"$substring\"; $reason. actual: $inputs"
                    },
                    { "Expected no input to contain \"$substring\"; actual: $inputs" },
                )
            }

            override fun toString(): String = "Input should contain substring \"$substring\""
        }
}
