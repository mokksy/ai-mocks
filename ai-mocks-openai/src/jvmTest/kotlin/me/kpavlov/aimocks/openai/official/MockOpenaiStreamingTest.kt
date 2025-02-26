package me.kpavlov.aimocks.openai

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.openai.core.JsonValue
import com.openai.models.ChatCompletionChunk
import com.openai.models.ChatCompletionCreateParams
import com.openai.models.ChatCompletionMessageParam
import com.openai.models.ChatCompletionStreamOptions
import com.openai.models.ChatCompletionUserMessageParam
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

internal class MockOpenaiStreamingTest : AbstractOpenaiTest() {
    @Test
    fun `Should respond to Streaming Chat Completion`() =
        runTest {
            openai.completion("openai-completion-list") {
                temperature = temperatureValue
                model = modelName
            } respondsStream {
                responseChunks = listOf("All", " we", " need", " is", " Love")
                delay = 50.milliseconds
                delayBetweenChunks = 10.milliseconds
                finishReason = "stop"
            }

            verifyStreamingCall()
        }

    @Test
    fun `Should respond to Streaming Chat Completion with Flow`() =
        runTest {
            openai.completion("openai-completion-flow") {
                temperature = temperatureValue
                model = modelName
            } respondsStream {
                responseFlow =
                    flow {
                        emit("All")
                        emit(" we")
                        emit(" need")
                        emit(" is")
                        emit(" Love")
                    }
                delay = 60.milliseconds
                delayBetweenChunks = 15.milliseconds
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
                ).temperature(temperatureValue)
                .seed(seedValue.toLong())
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
                ).model(modelName)
                .build()

        // when
        val result = StringBuffer()
        client
            .chat()
            .completions()
            .createStreaming(params)
            .use { messageStreamResponse ->
                messageStreamResponse
                    .stream()
                    .peek { println("Received: $it") }
                    .peek {
                        // validation
                        it.model() shouldBe modelName
                    }.flatMap { completion: ChatCompletionChunk ->
                        completion.choices().stream()
                    }.flatMap { choice: ChatCompletionChunk.Choice ->
                        choice.delta().content().stream()
                    }.forEach {
                        result.append(it)
                        print(it)
                    }
            }

        // then
        assertThat(result.toString()).isEqualTo("All we need is Love")
    }
}
