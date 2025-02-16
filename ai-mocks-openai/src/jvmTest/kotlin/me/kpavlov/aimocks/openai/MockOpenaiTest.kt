package me.kpavlov.aimocks.openai

import assertk.assertThat
import assertk.assertions.hasValue
import com.openai.core.JsonValue
import com.openai.models.ChatCompletionCreateParams
import com.openai.models.ChatCompletionMessageParam
import com.openai.models.ChatCompletionUserMessageParam
import com.openai.models.ChatModel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class MockOpenaiTest : AbstractOpenaiTest() {
    @Test
    fun `Should respond to Chat Completion`() =
        runTest {
            openai.completion {
                temperature = temperature
                seed = seedValue
                model = "gpt-4o-mini"
                maxCompletionTokens = maxCompletionTokens
            } responds {
                textContent = "Hello"
                finishReason = "stop"
            }

            val params =
                ChatCompletionCreateParams
                    .builder()
                    .temperature(temperature)
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
                    ).model(ChatModel.GPT_4O_MINI)
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
