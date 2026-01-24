package dev.mokksy.aimocks.openai.embeddings

import dev.mokksy.aimocks.openai.model.embeddings.CreateEmbeddingsRequest
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

internal object OpenaiEmbeddingsMatchers {
    fun inputContains(string: String): Matcher<CreateEmbeddingsRequest?> =
        object : Matcher<CreateEmbeddingsRequest?> {
            override fun test(value: CreateEmbeddingsRequest?): MatcherResult =
                MatcherResult.Companion(
                    value != null &&
                        value.input
                            .any {
                                it.contains(string)
                            },
                    { "Input should contain \"$string\"" },
                    { "Input should not contain \"$string\"" },
                )

            override fun toString(): String = "Input should contain \"$string\""
        }
}
