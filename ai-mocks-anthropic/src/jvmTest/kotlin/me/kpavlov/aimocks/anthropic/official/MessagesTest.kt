package me.kpavlov.aimocks.anthropic.official

import com.anthropic.errors.AnthropicServiceException
import com.anthropic.models.MessageCreateParams
import com.anthropic.models.Metadata
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import me.kpavlov.aimocks.anthropic.anthropic
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTimedValue

internal class MessagesTest : AbstractAnthropicTest() {
    /**
     * Tests that the system correctly responds with error responses for various HTTP status codes and
     * error types. Ensures that the response matches the expected error response for a given scenario.
     *
     * @see [HTTP Errors](https://docs.anthropic.com/en/api/errors#http-errors)
     * @param expectedHttpStatusCode The HTTP status code expected in the response.
     * @param errorType The type of error expected in the response, e.g., "invalid_request_error".
     */
    @ParameterizedTest
    @CsvSource(
        "400, invalid_request_error",
        "401, authentication_error",
        "403, permission_error",
        "404, not_found_error",
        "429, rate_limit_error",
        "500, api_error",
        "529, overloaded_error",
    )
    fun `Should respond with error`(
        expectedHttpStatusCode: Int,
        errorType: String,
    ) {
        val errorResponse =
            // language=json
            """
            {
              "type": "error",
              "error": {
                "type": "$errorType",
                "message": "An unexpected error has occurred internal to Anthropic’s systems."
              }
            }
            """.trimIndent()
        val httpStatusCode = HttpStatusCode.fromValue(expectedHttpStatusCode)
        val expectedDelay = IntRange(50, 100).random().milliseconds
        anthropic.messages {
            temperature = temperatureValue
            userId = userIdValue
            model = modelName
            systemMessageContains("helpful assistant")
            userMessageContains(errorType)
        } respondsError {
            body = errorResponse
            delay = expectedDelay
            httpStatus = httpStatusCode
        }

        val params =
            createChatCompletionRequestParams(
                message = "Respond with HTTP $expectedHttpStatusCode error: $errorType",
            )

        val timedValue =
            measureTimedValue {
                shouldThrow<AnthropicServiceException> {
                    client
                        .messages()
                        .create(params)
                }
            }

        timedValue.duration shouldBeGreaterThan expectedDelay
        val exception = timedValue.value
        exception.statusCode() shouldBe expectedHttpStatusCode
        exception.body() shouldBe errorResponse
    }

    @Test
    fun `Should respond to create Messages`() {
        anthropic.messages {
            temperature = temperatureValue
            userId = userIdValue
            model = modelName
            systemMessageContains("helpful assistant")
            userMessageContains("say 'Hello!'")
        } responds {
            assistantContent = "Hello"
            finishReason = "stop"
            delay = 200.milliseconds
        }

        val params =
            createChatCompletionRequestParams()

        val timedValue =
            measureTimedValue {
                client
                    .messages()
                    .create(params)
            }

        timedValue.duration shouldBeGreaterThan 200.milliseconds
        val result = timedValue.value

        result.validate()

        result.model().asString() shouldBe modelName
        result
            .content()
            .first()
            .asText()
            .text() shouldBe "Hello"
    }

    private fun createChatCompletionRequestParams(
        message: String = "Just say 'Hello!' and nothing else",
    ): MessageCreateParams =
        MessageCreateParams
            .builder()
            .temperature(temperatureValue)
            .maxTokens(maxCompletionTokensValue)
            .system("You are a helpful assistant.")
            .addUserMessage(message)
            .model(modelName)
            .metadata(Metadata.builder().userId(userIdValue).build())
            .build()
}
