package me.kpavlov.aimocks.openai.official

import com.openai.models.responses.ResponseCreateParams
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import me.kpavlov.aimocks.openai.openai
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTimedValue

internal class ResponsesOpenaiTest : AbstractOpenaiTest() {
    @Test
    fun `Should respond to Responses`() {
        openai.completion {
            temperature = temperatureValue
            model = modelName
            maxCompletionTokens = maxCompletionTokensValue
            systemMessageContains("helpful assistant")
            userMessageContains("say 'Hello!'")
        } responds {
            assistantContent = "Hello"
            finishReason = "stop"
            delay = 200.milliseconds
        }

        val params =
            createResponseCreateRequestParams()

        val timedValue =
            measureTimedValue {
                client
                    .responses()
                    .create(params)
            }

        timedValue.duration shouldBeGreaterThan 200.milliseconds
        val result = timedValue.value

        result.validate()

        result.model() shouldBe modelName
        result.text().orElseThrow() shouldBe "Hello"
    }

    private fun createResponseCreateRequestParams(): ResponseCreateParams {
        val params =
            ResponseCreateParams
                .builder()
                .temperature(temperatureValue)
                .maxOutputTokens(maxCompletionTokensValue)
                .model(modelName)
                .instructions("You are a helpful assistant.")
                .input("Just say 'Hello!' and nothing else")
                .build()
        return params
    }
}
