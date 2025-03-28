package me.kpavlov.aimocks.anthropic

import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.MessageParam
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import kotlin.jvm.optionals.getOrNull

internal object AnthropicAiMatchers {
    fun systemMessageContains(string: String): Matcher<MessageCreateParams.Body?> =
        object : Matcher<MessageCreateParams.Body?> {
            override fun test(value: MessageCreateParams.Body?): MatcherResult {
                val passed =
                    if (value == null) {
                        false
                    } else if (value.system().isPresent) {
                        val system = value.system().orElseThrow()
                        if (system.isString()) {
                            system.asString().contains(string) == true
                        } else if (system.isTextBlockParams()) {
                            system.asTextBlockParams().any { it.text().contains(string) }
                        } else {
                            false
                        }
                    } else {
                        false
                    }

                return MatcherResult(
                    passed,
                    { "System message should contain \"$string\"" },
                    { "System message should not contain \"$string\"" },
                )
            }

            override fun toString(): String = "System message should contain \"$string\""
        }

    fun userMessageContains(string: String): Matcher<MessageCreateParams.Body?> =
        object : Matcher<MessageCreateParams.Body?> {
            override fun test(value: MessageCreateParams.Body?): MatcherResult {
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

    fun topPEquals(topP: Double): Matcher<MessageCreateParams.Body?> =
        object : Matcher<MessageCreateParams.Body?> {
            override fun test(value: MessageCreateParams.Body?): MatcherResult =
                MatcherResult(
                    value != null &&
                        value
                            .topP()
                            .getOrNull() == topP,
                    { "top_p should be \"$topP\"" },
                    { "top_p should not be \"$topP\"" },
                )

            override fun toString(): String = "top_p should be \"$topP\""
        }

    fun topKEquals(topK: Long): Matcher<MessageCreateParams.Body?> =
        object : Matcher<MessageCreateParams.Body?> {
            override fun test(value: MessageCreateParams.Body?): MatcherResult =
                MatcherResult(
                    value != null &&
                        value
                            .topK()
                            .getOrNull() == topK,
                    { "top_k should be \"$topK\"" },
                    { "top_k should not be \"$topK\"" },
                )

            override fun toString(): String = "top_k should be \"$topK\""
        }

    fun modelEquals(model: String): Matcher<MessageCreateParams.Body?> =
        object : Matcher<MessageCreateParams.Body?> {
            override fun test(value: MessageCreateParams.Body?): MatcherResult =
                MatcherResult(
                    value != null &&
                        value
                            .model()
                            .asString() == model,
                    { "model should be \"$model\"" },
                    { "model should not be \"$model\"" },
                )

            override fun toString(): String = "model should be \"$model\""
        }

    fun temperatureEquals(temperature: Double): Matcher<MessageCreateParams.Body?> =
        object : Matcher<MessageCreateParams.Body?> {
            override fun test(value: MessageCreateParams.Body?): MatcherResult =
                MatcherResult(
                    value != null &&
                        value
                            .temperature()
                            .getOrNull() == temperature,
                    { "temperature should be \"$temperature\"" },
                    { "temperature should not be \"$temperature\"" },
                )

            override fun toString(): String = "temperature should be \"$temperature\""
        }

    fun maxTokensEquals(maxTokens: Long): Matcher<MessageCreateParams.Body?> =
        object : Matcher<MessageCreateParams.Body?> {
            override fun test(value: MessageCreateParams.Body?): MatcherResult =
                MatcherResult(
                    value != null &&
                        value
                            .maxTokens() == maxTokens,
                    { "maxTokens should be \"$maxTokens\"" },
                    { "maxTokens should not be \"$maxTokens\"" },
                )

            override fun toString(): String = "maxTokens should be \"$maxTokens\""
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
