package me.kpavlov.aimocks.openai.responses

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import me.kpavlov.aimocks.openai.model.responses.CreateResponseRequest
import me.kpavlov.aimocks.openai.model.responses.InputContent
import me.kpavlov.aimocks.openai.model.responses.InputFile
import me.kpavlov.aimocks.openai.model.responses.InputImage
import me.kpavlov.aimocks.openai.model.responses.InputItems
import me.kpavlov.aimocks.openai.model.responses.InputText
import me.kpavlov.aimocks.openai.model.responses.Text
import me.kpavlov.mokksy.utils.ellipsizeMiddle

/**
 * OpenaiResponsesMatchers is a utility object that provides matchers for validating properties of
 * a [CreateResponseRequest] object.
 *
 * These matchers are primarily used to evaluate specific aspects of the `CreateResponseRequest`
 * to ensure it contains the expected data or behaviors.
 * @author Konstantin Pavlov
 */
internal object OpenaiResponsesMatchers {
    fun instructionsContains(string: String): Matcher<CreateResponseRequest?> =
        object : Matcher<CreateResponseRequest?> {
            override fun test(value: CreateResponseRequest?): MatcherResult =
                MatcherResult(
                    value != null &&
                        value.instructions?.contains(string) == true,
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
                    { "Input should contain image with url \"$imageUrl\"" },
                    { "Input should NOT contain image with url \"$imageUrl\"" },
                )
            }

            override fun toString(): String =
                "InputImage should have URL \"${imageUrl.ellipsizeMiddle(256)}\""
        }

    fun containsInputFileNamed(filename: String): Matcher<CreateResponseRequest?> =
        object : Matcher<CreateResponseRequest?> {
            override fun test(value: CreateResponseRequest?): MatcherResult {
                val passed =
                    if (value == null) {
                        false
                    } else if ((value.input is InputItems) == false) {
                        false
                    } else {
                        val files = extractInputItem<InputFile>(value)
                        files?.any { it.filename == filename } == true
                    }

                return MatcherResult(
                    passed,
                    { "Request should contain file named \"$filename\"" },
                    { "Request should NOT contain file named  \"$filename\"" },
                )
            }

            override fun toString(): String = "Request should contain file named \"$filename\""
        }

    fun containsInputFileWithId(fileId: String): Matcher<CreateResponseRequest?> =
        object : Matcher<CreateResponseRequest?> {
            override fun test(value: CreateResponseRequest?): MatcherResult {
                val passed =
                    if (value == null) {
                        false
                    } else if ((value.input is InputItems) == false) {
                        false
                    } else {
                        val files = extractInputItem<InputFile>(value)
                        files?.any { it.fileId == fileId } == true
                    }

                return MatcherResult(
                    passed,
                    { "Request should contain file with ID \"$fileId\"" },
                    { "Request should NOT contain file with ID  \"$fileId\"" },
                )
            }

            override fun toString(): String = "Request should contain file with ID \"$fileId\""
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
