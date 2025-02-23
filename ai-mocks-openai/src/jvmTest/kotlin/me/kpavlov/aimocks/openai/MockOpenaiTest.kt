package me.kpavlov.aimocks.openai

import assertk.assertThat
import assertk.assertions.hasValue
import com.openai.models.ChatCompletionCreateParams
import com.openai.models.ChatCompletionMessageParam
import com.openai.models.ChatCompletionSystemMessageParam
import com.openai.models.ChatCompletionUserMessageParam
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class MockOpenaiTest : AbstractOpenaiTest() {
    @Test
    fun `Should respond to Chat Completion`() =
        runTest {
            openai.completion {
                temperature = temperature
                seed = seedValue
                model = modelName
                maxCompletionTokens = maxCompletionTokens
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
                    .maxCompletionTokens(maxCompletionTokens)
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

            println(result)
            assertThat(
                result
                    .choices()
                    .first()
                    .message()
                    .content(),
            ).hasValue("Hello")
        }
}
