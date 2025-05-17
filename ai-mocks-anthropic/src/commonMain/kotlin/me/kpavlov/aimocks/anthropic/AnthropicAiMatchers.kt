package me.kpavlov.aimocks.anthropic

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import me.kpavlov.aimocks.anthropic.model.MessageCreateParams

internal object AnthropicAiMatchers {

    fun systemMessageContains(string: String): Matcher<MessageCreateParams?> =
        object : Matcher<MessageCreateParams?> {
            override fun test(value: MessageCreateParams?): MatcherResult {
                val passed =
                    value?.system?.any { it.text.contains(string) } == true
                return MatcherResult(
                    passed,
                    { "System message should contain \"$string\"" },
                    { "System message should not contain \"$string\"" },
                )
            }

            override fun toString(): String = "System message should contain \"$string\""
        }

    fun userMessageContains(string: String): Matcher<MessageCreateParams?> =
        object : Matcher<MessageCreateParams?> {
            override fun test(value: MessageCreateParams?): MatcherResult {
                val passed =
                    findUserMessages(value)
                        .any { checkTextBlockContains(it, string) }
                return MatcherResult(
                    passed,
                    { "User message should contain \"$string\"" },
                    { "User message should not contain \"$string\"" },
                )
            }

            private fun findUserMessages(
                value: MessageCreateParams?,
            ): List<MessageCreateParams.Content> =
                value
                    ?.messages
                    ?.filter { it.role == "user" }
                    ?.map { it.content } ?: emptyList()

            override fun toString(): String = "User message should contain \"$string\""
        }

    fun userIdEquals(userId: String): Matcher<MessageCreateParams?> =

        object : Matcher<MessageCreateParams?> {
            override fun test(value: MessageCreateParams?): MatcherResult =
                MatcherResult(
                    value?.metadata?.userId == userId,
                    { "metadata.user_id should be \"$userId\"" },
                    { "metadata.user_id should not be \"$userId\"" },
                )

            override fun toString(): String = "metadata.user_id should be \"$userId\""
        }

    fun topPEquals(topP: Double): Matcher<MessageCreateParams?> =

        object : Matcher<MessageCreateParams?> {
            override fun test(value: MessageCreateParams?): MatcherResult =
                MatcherResult(
                    value?.topP == topP,
                    { "top_p should be $topP" },
                    { "top_p should not be $topP" },
                )

            override fun toString(): String = "top_p should be $topP"
        }

    fun topKEquals(topK: Long): Matcher<MessageCreateParams?> =
        object : Matcher<MessageCreateParams?> {
            override fun test(value: MessageCreateParams?): MatcherResult =
                MatcherResult(
                    value?.topK?.toLong() == topK,
                    { "top_k should be $topK" },
                    { "top_k should not be $topK" },
                )

            override fun toString(): String = "top_k should be $topK"
        }

    fun modelEquals(model: String): Matcher<MessageCreateParams?> =
        object : Matcher<MessageCreateParams?> {
            override fun test(value: MessageCreateParams?): MatcherResult =
                MatcherResult(
                    value?.model == model,
                    { "model should be \"$model\"" },
                    { "model should not be \"$model\"" },
                )

            override fun toString(): String = "model should be \"$model\""
        }

    fun temperatureEquals(temperature: Double): Matcher<MessageCreateParams?> =
        object : Matcher<MessageCreateParams?> {
            override fun test(value: MessageCreateParams?): MatcherResult =
                MatcherResult(
                    value?.temperature == temperature,
                    { "temperature should be $temperature" },
                    { "temperature should not be $temperature" },
                )

            override fun toString(): String = "temperature should be $temperature"
        }

    fun maxTokensEquals(maxTokens: Long): Matcher<MessageCreateParams?> =
        object : Matcher<MessageCreateParams?> {
            override fun test(value: MessageCreateParams?): MatcherResult =
                MatcherResult(
                    value?.maxTokens?.toLong() == maxTokens,
                    { "maxTokens should be $maxTokens" },
                    { "maxTokens should not be $maxTokens" },
                )

            override fun toString(): String = "maxTokens should be $maxTokens"
        }

    private fun checkTextBlockContains(
        content: MessageCreateParams.Content?,
        string: String,
    ): Boolean =
        when (content) {
            is MessageCreateParams.TextContent -> {
                content.text?.contains(string) == true
            }

            is MessageCreateParams.ContentList -> {
                content.blocks.any {
                    (it as? MessageCreateParams.TextBlock)?.text?.contains(string) == true
                }
            }

            else -> {
                TODO()
            }
        }

}
