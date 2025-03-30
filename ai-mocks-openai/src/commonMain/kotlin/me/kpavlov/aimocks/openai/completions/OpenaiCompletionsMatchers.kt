package me.kpavlov.aimocks.openai.completions

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import me.kpavlov.aimocks.openai.ChatCompletionRequest
import me.kpavlov.aimocks.openai.model.ChatCompletionRole

/**
 * Utility object providing custom matchers for testing and validating attributes of
 * `ChatCompletionRequest` instances. These matchers focus on checking the contents of the
 * messages within the `ChatCompletionRequest` for specific roles such as SYSTEM, DEVELOPER, or USER.
 * @author Konstantin Pavlov
 */
internal object OpenaiCompletionsMatchers {
    fun systemMessageContains(string: String): Matcher<ChatCompletionRequest?> =
        object : Matcher<ChatCompletionRequest?> {
            override fun test(value: ChatCompletionRequest?): MatcherResult =
                MatcherResult.Companion(
                    value != null &&
                        value.messages
                            .find {
                                it.role == ChatCompletionRole.SYSTEM ||
                                    it.role == ChatCompletionRole.DEVELOPER
                            }?.content
                            ?.contains(string) == true,
                    { "System message should contain \"$string\"" },
                    { "System message should not contain \"$string\"" },
                )

            override fun toString(): String = "System message should contain \"$string\""
        }

    fun userMessageContains(string: String): Matcher<ChatCompletionRequest?> =
        object : Matcher<ChatCompletionRequest?> {
            override fun test(value: ChatCompletionRequest?): MatcherResult =
                MatcherResult.Companion(
                    value != null &&
                        value.messages
                            .find { it.role == ChatCompletionRole.USER }
                            ?.content
                            ?.contains(string) == true,
                    { "User message should contain \"$string\"" },
                    { "User message should not contain \"$string\"" },
                )

            override fun toString(): String = "User message should contain \"$string\""
        }
}
