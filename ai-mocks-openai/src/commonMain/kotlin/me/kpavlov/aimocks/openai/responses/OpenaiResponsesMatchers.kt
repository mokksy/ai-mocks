package me.kpavlov.aimocks.openai.responses

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import me.kpavlov.aimocks.openai.model.responses.CreateResponseRequest
import me.kpavlov.aimocks.openai.model.responses.InputContent
import me.kpavlov.aimocks.openai.model.responses.InputImage
import me.kpavlov.aimocks.openai.model.responses.InputItems
import me.kpavlov.aimocks.openai.model.responses.InputText
import me.kpavlov.aimocks.openai.model.responses.Text

internal object OpenaiResponsesMatchers {
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

    private inline fun <reified T : InputContent> extractInputItem(
        value: CreateResponseRequest,
    ): List<T>? =
        value
            .input
            .let {
                value.input as? InputItems
            }?.items
            ?.flatMap { it.content }
            ?.mapNotNull {
                @Suppress("UNCHECKED_CAST")
                it as? T
            }

    fun containsInputImageWithUrl(imageUrl: String): Matcher<CreateResponseRequest?> =
        object : Matcher<CreateResponseRequest?> {
            override fun test(value: CreateResponseRequest?): MatcherResult {
                val passed =
                    if (value == null) {
                        false
                    } else if ((value.input is InputItems) == false) {
                        false
                    } else {
                        val images = extractInputItem<InputImage>(value)
                        images?.any { it.imageUrl == imageUrl } == true
                    }

                return MatcherResult(
                    passed,
                    { "Instructions should contain \"$imageUrl\"" },
                    { "Instructions should not contain \"$imageUrl\"" },
                )
            }

            override fun toString(): String = "InputImage should have URL \"$imageUrl\""
        }

    fun userMessageContains(subString: String): Matcher<CreateResponseRequest?> =
        object : Matcher<CreateResponseRequest?> {
            override fun test(value: CreateResponseRequest?): MatcherResult {
                val passed =
                    if (value == null) {
                        false
                    } else if (value.input is Text) {
                        value.input.text.contains(subString)
                    } else {
                        extractInputItem<InputText>(value)
                            ?.any { it.text.contains(subString) } == true
                    }
                return MatcherResult(
                    passed,
                    { "User message should contain \"$subString\"" },
                    { "User message should not contain \"$subString\"" },
                )
            }

            override fun toString(): String = "User message should contain \"$subString\""
        }
}
