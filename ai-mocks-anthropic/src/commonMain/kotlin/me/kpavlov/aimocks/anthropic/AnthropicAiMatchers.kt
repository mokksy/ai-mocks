package me.kpavlov.aimocks.anthropic

import com.anthropic.models.MessageCreateParams
import com.anthropic.models.MessageParam
import com.anthropic.models.TextBlockParam
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
            override fun test(value: MessageCreateParams.Body?): MatcherResult {
                val content = findUserMessageContent(value)
                val result: Boolean =
                    content != null &&
                        findTextContent(content) {
                            it?.text()?.contains(string) == true
                        } != null
                return MatcherResult(
                    result,
                    { "User message should contain \"$string\"" },
                    { "User message should not contain \"$string\"" },
                )
            }

            private fun findUserMessageContent(
                value: MessageCreateParams.Body?,
            ): MessageParam.Content? =
                value
                    ?.messages()
                    ?.find { it.role() == MessageParam.Role.USER }
                    ?.content()

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

    private fun findTextContent(
        content: MessageParam.Content?,
        predicate: (TextBlockParam?) -> Boolean,
    ): TextBlockParam? =
        content
            ?.blockParams()
            ?.getOrNull()
            ?.mapNotNull { it.text().getOrNull() }
            ?.firstOrNull(predicate)
}
