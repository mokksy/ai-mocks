package me.kpavlov.aimocks.anthropic

import com.anthropic.models.MessageCreateParams
import com.anthropic.models.MessageParam
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import kotlin.jvm.optionals.getOrNull

internal object AnthropicAiMatchers {
    fun systemMessageContains(string: String): Matcher<MessageCreateParams.Body?> =
        object : Matcher<MessageCreateParams.Body?> {
            override fun test(value: MessageCreateParams.Body?): MatcherResult =
                MatcherResult(
                    value != null &&
                        value
                            .system()
                            .getOrNull()
                            ?.string()
                            ?.getOrNull()
                            ?.contains(string) == true,
                    { "System message should contain \"$string\"" },
                    { "System message should not contain \"$string\"" },
                )

            override fun toString(): String = "System message should contain \"$string\""
        }

    fun userMessageContains(string: String): Matcher<MessageCreateParams.Body?> =
        object : Matcher<MessageCreateParams.Body?> {
            override fun test(value: MessageCreateParams.Body?): MatcherResult =
                MatcherResult(
                    findUserMessages(value)
                        .any { checkTextBlockContains(it, string) },
                    { "User message should contain \"$string\"" },
                    { "User message should not contain \"$string\"" },
                )

            private fun findUserMessages(
                value: MessageCreateParams.Body?,
            ): List<MessageParam.Content> =
                value
                    ?.messages()
                    ?.filter { it.role() == MessageParam.Role.USER }
                    ?.mapNotNull { it.content() } ?: emptyList()

            override fun toString(): String = "User message should contain \"$string\""
        }

    fun userIdEquals(userId: String): Matcher<MessageCreateParams.Body?> =
        object : Matcher<MessageCreateParams.Body?> {
            override fun test(value: MessageCreateParams.Body?): MatcherResult =
                MatcherResult(
                    value != null &&
                        value
                            .metadata()
                            .getOrNull()
                            ?.userId()
                            ?.getOrNull() == userId,
                    { "metadata.user_id should be \"$userId\"" },
                    { "metadata.user_id should not be \"$userId\"" },
                )

            override fun toString(): String = "metadata.user_id should be \"$userId\""
        }

    private fun checkTextBlockContains(
        content: MessageParam.Content?,
        string: String,
    ): Boolean =
        if (content?.string()?.isPresent == true) {
            content.string().getOrNull()?.contains(string) == true
        } else {
            content
                ?.blockParams()
                ?.getOrNull()
                ?.mapNotNull { it.text().getOrNull() }
                ?.mapNotNull { it.text() }
                ?.any { it.contains(string) == true } == true
        }
}
