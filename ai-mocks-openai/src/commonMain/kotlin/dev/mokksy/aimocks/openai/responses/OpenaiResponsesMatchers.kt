package dev.mokksy.aimocks.openai.responses

import dev.mokksy.aimocks.openai.model.responses.CreateResponseRequest
import dev.mokksy.aimocks.openai.model.responses.InputContent
import dev.mokksy.aimocks.openai.model.responses.InputFile
import dev.mokksy.aimocks.openai.model.responses.InputImage
import dev.mokksy.aimocks.openai.model.responses.InputItems
import dev.mokksy.aimocks.openai.model.responses.InputText
import dev.mokksy.aimocks.openai.model.responses.Text
import dev.mokksy.mokksy.utils.ellipsizeMiddle
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

private const val IMAGE_URL_MAX_LENGTH = 256

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

    private inline fun <reified T : InputContent> extractInputItem(value: CreateResponseRequest): List<T>? =
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

    /**
     * Returns a matcher that checks whether a `CreateResponseRequest` contains an `InputImage` with the specified URL.
     *
     * The matcher succeeds if the request's input includes an `InputImage`
     * whose `imageUrl` exactly matches the provided value.
     *
     * @param imageUrl The URL to match against the `InputImage` items in the request.
     * @return A Kotest matcher for validating the presence of an image with the given URL.
     */
    fun containsInputImageWithUrl(imageUrl: String): Matcher<CreateResponseRequest?> =
        object : Matcher<CreateResponseRequest?> {
            override fun test(value: CreateResponseRequest?): MatcherResult {
                val passed =
                    if (value == null) {
                        false
                    } else if (value.input !is InputItems) {
                        false
                    } else {
                        val images = extractInputItem<InputImage>(value)
                        images?.any { it.imageUrl == imageUrl } == true
                    }

                return MatcherResult(
                    passed,
                    {
                        "Input should contain image with url \"${
                            imageUrl.ellipsizeMiddle(
                                IMAGE_URL_MAX_LENGTH,
                            )
                        }\""
                    },
                    {
                        "Input should NOT contain image with url \"${
                            imageUrl.ellipsizeMiddle(
                                IMAGE_URL_MAX_LENGTH,
                            )
                        }\""
                    },
                )
            }

            override fun toString(): String =
                "InputImage should have URL \"${imageUrl.ellipsizeMiddle(IMAGE_URL_MAX_LENGTH)}\""
        }

    /**
     * Returns a matcher that checks if a `CreateResponseRequest` contains an input file with the specified filename.
     *
     * The matcher succeeds if any `InputFile` in the request's input has a filename matching the provided value.
     *
     * @param filename The name of the file to search for in the request input.
     * @return A matcher that verifies the presence of a file with the given filename.
     */
    fun containsInputFileNamed(filename: String): Matcher<CreateResponseRequest?> =
        object : Matcher<CreateResponseRequest?> {
            override fun test(value: CreateResponseRequest?): MatcherResult {
                val passed =
                    if (value == null) {
                        false
                    } else if (value.input !is InputItems) {
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
                    } else if (value.input !is InputItems) {
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
