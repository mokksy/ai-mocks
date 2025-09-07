package me.kpavlov.aimocks.openai.official.completions

import com.openai.core.JsonValue
import com.openai.errors.InternalServerException
import com.openai.errors.UnexpectedStatusCodeException
import com.openai.models.ResponseFormatJsonSchema
import com.openai.models.chat.completions.ChatCompletionCreateParams
import com.openai.models.chat.completions.ChatCompletionMessageParam
import com.openai.models.chat.completions.ChatCompletionSystemMessageParam
import com.openai.models.chat.completions.ChatCompletionUserMessageParam
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import me.kpavlov.aimocks.openai.official.AbstractOpenaiTest
import me.kpavlov.aimocks.openai.openai
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTimedValue

internal class ChatCompletionOpenaiTest : AbstractOpenaiTest() {
    @Test
    fun `Should respond to Chat Completion`() {
        openai.completion {
            temperature = temperatureValue
            seed = seedValue
            model = modelName
            topP = topPValue
            maxTokens = maxCompletionTokensValue
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
                    .chat()
                    .completions()
                    .create(params)
            }

        timedValue.duration shouldBeGreaterThan 200.milliseconds
        val result = timedValue.value

        result.validate()

        result.model() shouldBe modelName
        result
            .choices()
            .first()
            .message()
            .content()
            .orElseThrow() shouldBe "Hello"
    }

    @Test
    fun `Should respond with unexpected error`() {
        val carambaResponse =
            // language=json
            """
            {
              "type": "error",
              "code": "ERR_SOMETHING",
              "message": "Arrr, blast me barnacles! This be not what ye expect! 🏴‍☠️",
              "param": null
            }
            """.trimIndent()
        openai
            .completion {
                temperature = temperatureValue
                seed = seedValue
                model = modelName
                maxTokens = maxCompletionTokensValue
                systemMessageContains("helpful assistant")
                userMessageContains("say 'Hello!'")
            }.respondsError(String::class) {
                body = carambaResponse
                contentType = ContentType.Text.Plain
                delay = 100.milliseconds
                httpStatus = HttpStatusCode.PreconditionFailed
            }

        val params =
            createChatCompletionRequestParams()

        val timedValue =
            measureTimedValue {
                shouldThrow<UnexpectedStatusCodeException> {
                    client
                        .chat()
                        .completions()
                        .create(params)
                }
            }

        timedValue.duration shouldBeGreaterThan 100.milliseconds
        val exception = timedValue.value
        exception.statusCode() shouldBe HttpStatusCode.PreconditionFailed.value
    }

    @Test
    fun `Should respond with error 500`() {
        val errorResponse =
            // language=json
            """
            {
                "error": {
                   "type": "server_error",
                  "code": "ERR_SOMETHING",
                  "message": "Arrr, blast me barnacles! This be not what ye expect! 🏴‍☠️",
                  "param": "foo"
                }
            }
            """.trimIndent()
        openai
            .completion {
                temperature = temperatureValue
                seed = seedValue
                model = modelName
                maxTokens = maxCompletionTokensValue
                systemMessageContains("helpful assistant")
                userMessageContains("say 'Hello!'")
            }.respondsError(String::class) {
                body = errorResponse
                delay = 150.milliseconds
                contentType = ContentType.Application.Json
                httpStatus = HttpStatusCode.InternalServerError
            }

        val params =
            createChatCompletionRequestParams()

        val timedValue =
            measureTimedValue {
                shouldThrow<InternalServerException> {
                    client
                        .chat()
                        .completions()
                        .create(params)
                }
            }

        timedValue.duration shouldBeGreaterThan 150.milliseconds
        val exception = timedValue.value

        assertSoftly(exception) {
            statusCode() shouldBe HttpStatusCode.InternalServerError.value
            code() shouldBePresent {
                shouldBe("ERR_SOMETHING")
            }
            message shouldBe
                "500: Arrr, blast me barnacles! This be not what ye expect! 🏴‍☠️"
            param() shouldBePresent {
                shouldBe("foo")
            }
            type() shouldBePresent {
                shouldBe("server_error")
            }
        }
    }

    private fun createChatCompletionRequestParams(): ChatCompletionCreateParams {
        val params =
            @Suppress("deprecation")
            ChatCompletionCreateParams
                .builder()
                .temperature(temperatureValue)
                .topP(topPValue)
                .maxCompletionTokens(maxCompletionTokensValue)
                .seed(seedValue.toLong())
                .responseFormat(
                    ResponseFormatJsonSchema
                        .builder()
                        .jsonSchema(
                            ResponseFormatJsonSchema.JsonSchema
                                .builder()
                                .strict(true)
                                .name("result")
                                .schema(
                                    ResponseFormatJsonSchema.JsonSchema.Schema
                                        .builder()
                                        .putAdditionalProperty("a", JsonValue.from("b"))
                                        .build(),
                                ).build(),
                        ).build(),
                ).messages(
                    listOf(
                        ChatCompletionMessageParam.ofSystem(
                            ChatCompletionSystemMessageParam
                                .builder()
                                .content(
                                    "You are a helpful assistant.",
                                ).build(),
                        ),
                        ChatCompletionMessageParam.ofUser(
                            ChatCompletionUserMessageParam
                                .builder()
                                .content("Just say 'Hello!' and nothing else")
                                .build(),
                        ),
                    ),
                ).model(modelName)
                .build()
        return params
    }
}
