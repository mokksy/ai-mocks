package dev.mokksy.aimocks.openai.official.responses

import com.openai.models.responses.ResponseCreateParams
import dev.mokksy.aimocks.openai.openai
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTimedValue

internal class ResponsesTextTest : AbstractOpenaiResponsesTest() {
    @Test
    fun `Should respond to Responses`() {
        val instructions = "Please be ultra-brief in your response."
        val expectedInstructions = "be ultra-brief"
        val userInput = "How to start business?"
        openai.responses {
            temperature = temperatureValue
            model = modelName
            maxTokens = maxCompletionTokensValue
            systemMessageContains(expectedInstructions)
            userMessageContains(userInput)
        } responds {
            assistantContent = "Find. Create. Sell."
            finishReason = "stop"
            delay = 200.milliseconds
        }

        val params =
            createResponseCreateRequestParams(instructions, userInput)

        val timedValue =
            measureTimedValue {
                client
                    .responses()
                    .create(params)
            }

        timedValue.duration shouldBeGreaterThan 200.milliseconds
        val response = timedValue.value

        val message = response.output().first().asMessage()
        logger.info { "Response message: $message" }
        message
            .content()
            .first()
            .asOutputText()
            .text() shouldBe "Find. Create. Sell."

        verifyResponse(response)
    }

    private fun createResponseCreateRequestParams(
        instructions: String,
        userInput: String,
    ): ResponseCreateParams {
        val params =
            ResponseCreateParams
                .builder()
                .temperature(temperatureValue)
                .maxOutputTokens(maxCompletionTokensValue)
                .model(modelName)
                .instructions(instructions)
                .input(userInput)
                .build()
        return params
    }
}
