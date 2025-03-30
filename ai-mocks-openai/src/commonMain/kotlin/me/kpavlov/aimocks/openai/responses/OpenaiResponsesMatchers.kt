package me.kpavlov.aimocks.openai.responses

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import me.kpavlov.aimocks.openai.model.responses.CreateResponseRequest
import me.kpavlov.aimocks.openai.model.responses.InputImage
import me.kpavlov.aimocks.openai.model.responses.InputItems

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

    fun containsInputImageWithUrl(imageUrl: String): Matcher<CreateResponseRequest?> =
        object : Matcher<CreateResponseRequest?> {
            override fun test(value: CreateResponseRequest?): MatcherResult {
                val passed =
                    if (value == null) {
                        false
                    } else if ((value.input is InputItems) == false) {
                        false
                    } else {
                        value
                            .input
                            .items
                            .flatMap { it.content }
                            .filter { it is InputImage }
                            .map { it as InputImage }
                            .any { it.imageUrl == imageUrl }
                    }

                return MatcherResult(
                    passed,
                    { "Instructions should contain \"$imageUrl\"" },
                    { "Instructions should not contain \"$imageUrl\"" },
                )
            }

            override fun toString(): String = "InputImage should have URL \"$imageUrl\""
        }
}
