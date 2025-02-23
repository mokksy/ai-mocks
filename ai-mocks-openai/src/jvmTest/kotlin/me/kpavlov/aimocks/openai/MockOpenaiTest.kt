package me.kpavlov.aimocks.openai

import assertk.assertThat
import assertk.assertions.hasValue
import com.openai.core.JsonValue
import com.openai.models.ChatCompletionCreateParams
import com.openai.models.ChatCompletionMessageParam
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
            } responds {
                textContent = "Hello"
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
                            ChatCompletionMessageParam.ofUser(
                                ChatCompletionUserMessageParam
                                    .builder()
                                    .role(JsonValue.from("user"))
                                    .content(
                                        ChatCompletionUserMessageParam.Content.ofText(
                                            "Just say and nothing else but 'Hello!'",
                                        ),
                                    ).build(),
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
