package me.kpavlov.aimocks.openai

import com.openai.models.ChatCompletionCreateParams
import com.openai.models.ChatCompletionMessageParam
import com.openai.models.ChatCompletionSystemMessageParam
import com.openai.models.ChatCompletionUserMessageParam
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class MockOpenaiTest : AbstractOpenaiTest() {
    @Test
    fun `Should respond to Chat Completion`() =
        runTest {
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
            }

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

            val result =
                client
                    .chat()
                    .completions()
                    .create(params)

            result.validate()

            result.model() shouldBe modelName
            result
                .choices()
                .first()
                .message()
                .content()
                .orElseThrow() shouldBe "Hello"
        }
}
