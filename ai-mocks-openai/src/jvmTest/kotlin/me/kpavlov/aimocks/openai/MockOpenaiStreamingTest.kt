package me.kpavlov.aimocks.openai

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.openai.core.JsonValue
import com.openai.models.ChatCompletionChunk
import com.openai.models.ChatCompletionCreateParams
import com.openai.models.ChatCompletionMessageParam
import com.openai.models.ChatCompletionStreamOptions
import com.openai.models.ChatCompletionUserMessageParam
import com.openai.models.ChatModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class MockOpenaiStreamingTest : AbstractOpenaiTest() {
    @Test
    fun `Should respond to Streaming Chat Completion`() =
        runTest {
            openai.completion {
                temperature = temperature
                model = "gpt-4o-mini"
            } respondsStream {
                responseChunks = listOf("All", " we", " need", " is", " Love")
                delayBetweenChunksMs = 50
                finishReason = "stop"
            }

            verifyStreamingCall()
        }

    @Test
    fun `Should respond to Streaming Chat Completion with Flow`() =
        runTest {
            openai.completion {
                temperature = temperature
                model = "gpt-4o-mini"
            } respondsStream {
                responseFlow =
                    flow {
                        emit("All")
                        emit(" we")
                        emit(" need")
                        emit(" is")
                        emit(" Love")
                    }
                delayBetweenChunksMs = 50
                finishReason = "stop"
            }

            verifyStreamingCall()
        }

    private fun verifyStreamingCall() {
        val params =
            ChatCompletionCreateParams
                .builder()
                .streamOptions(
                    ChatCompletionStreamOptions.builder().includeUsage(true).build(),
                ).temperature(temperature)
                .seed(1)
                .messages(
                    listOf(
                        ChatCompletionMessageParam.ofUser(
                            ChatCompletionUserMessageParam
                                .builder()
                                .role(JsonValue.from("user"))
                                .content(
                                    ChatCompletionUserMessageParam.Content.ofText(
                                        "What do we need?",
                                    ),
                                ).build(),
                        ),
                    ),
                ).model(ChatModel.GPT_4O_MINI)
                .build()

        val result = StringBuffer()
        client.chat().completions().createStreaming(params).use { messageStreamResponse ->
            messageStreamResponse
                .stream()
                .peek { println("Received: $it") }
                .flatMap { completion: ChatCompletionChunk ->
                    completion.choices().stream()
                }.flatMap { choice: ChatCompletionChunk.Choice ->
                    choice.delta().content().stream()
                }.forEach {
                    result.append(it)
                    print(it)
                }
        }

        assertThat(result.toString()).isEqualTo("All we need is Love")
    }
}
