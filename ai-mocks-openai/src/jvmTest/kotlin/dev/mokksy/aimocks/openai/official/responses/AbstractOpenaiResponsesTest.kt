package dev.mokksy.aimocks.openai.official.responses

import dev.mokksy.aimocks.openai.official.AbstractOpenaiTest
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.longs.shouldBeNonNegative
import io.kotest.matchers.longs.shouldBePositive
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith

internal abstract class AbstractOpenaiResponsesTest : AbstractOpenaiTest() {
    protected fun verifyResponse(response: com.openai.models.responses.Response) {
        response.id() shouldStartWith "resp_"

        response
            .model()
            .chat()
            .orElseThrow()
            .asString() shouldBe modelName

        response.temperature().orElseThrow() shouldBe temperatureValue
        response.maxOutputTokens().orElseThrow() shouldBe maxCompletionTokensValue

        response.createdAt() shouldBeGreaterThanOrEqualTo startTimestamp.epochSecond.toDouble()

        response.usage() shouldBePresent {
            it.outputTokens().shouldBeLessThanOrEqual(maxCompletionTokensValue)
            it.outputTokens().shouldBePositive()
            it.inputTokens().shouldBePositive()
            it.inputTokensDetails().cachedTokens().shouldBeNonNegative()
            it.outputTokensDetails().reasoningTokens().shouldBePositive()
        }
    }
}
