package me.kpavlov.aimocks.openai.embeddings

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import me.kpavlov.aimocks.openai.model.embeddings.CreateEmbeddingsRequest

internal object OpenaiEmbeddingsMatchers {
    fun inputContains(string: String): Matcher<CreateEmbeddingsRequest?> =
        object : Matcher<CreateEmbeddingsRequest?> {
            override fun test(value: CreateEmbeddingsRequest?): MatcherResult =
                MatcherResult.Companion(
                    value != null &&
                        value.input
                            .firstOrNull {
                                it.contains(string)
                            } != null,
                    { "Input should contain \"$string\"" },
                    { "Input should contain not contain \"$string\"" },
                )

            override fun toString(): String = "Input should contain contain \"$string\""
        }
}
