package me.kpavlov.aimocks.openai

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import me.kpavlov.aimocks.openai.model.ChatCompletionRole

internal object OpenAiMatchers {
    fun systemMessageContains(string: String): Matcher<ChatCompletionRequest?> =
        object : Matcher<ChatCompletionRequest?> {
            override fun test(value: ChatCompletionRequest?): MatcherResult =
                MatcherResult(
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
                MatcherResult(
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
