package me.kpavlov.aimocks.openai.official

import com.openai.errors.UnexpectedStatusCodeException
import com.openai.models.chat.completions.ChatCompletionCreateParams
import com.openai.models.chat.completions.ChatCompletionMessageParam
import com.openai.models.chat.completions.ChatCompletionSystemMessageParam
import com.openai.models.chat.completions.ChatCompletionUserMessageParam
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import me.kpavlov.aimocks.openai.openai
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTimedValue

internal class ChatCompletionOpenaiTest : AbstractOpenaiTest() {
    @Test
    fun `Should respond to Chat Completion`() {
        openai.completion {
            temperature = temperatureValue
            seed = seedValue
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
    fun `Should respond with error`() {
        val carambaResponse =
            // language=json
            """
            {
              "caramba": "Arrr, blast me barnacles! This be not what ye expect! 🏴‍☠️"
            }
            """.trimIndent()
        openai
            .completion {
                temperature = temperatureValue
                seed = seedValue
                model = modelName
                maxCompletionTokens = maxCompletionTokensValue
                systemMessageContains("helpful assistant")
                userMessageContains("say 'Hello!'")
            }.respondsError(String::class) {
                body = carambaResponse
                delay = 1.seconds
                httpStatus = HttpStatusCode.PaymentRequired
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

        timedValue.duration shouldBeGreaterThan 1.seconds
        val exception = timedValue.value
        exception.statusCode() shouldBe HttpStatusCode.PaymentRequired.value
        exception.body() shouldBe carambaResponse
    }

    private fun createChatCompletionRequestParams(): ChatCompletionCreateParams {
        val params =
            ChatCompletionCreateParams
                .builder()
                .temperature(temperatureValue)
                .maxCompletionTokens(maxCompletionTokensValue)
                .seed(seedValue.toLong())
                .messages(
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
