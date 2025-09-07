package me.kpavlov.aimocks.openai.official.completions

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.openai.core.JsonValue
import com.openai.models.chat.completions.ChatCompletionChunk
import com.openai.models.chat.completions.ChatCompletionCreateParams
import com.openai.models.chat.completions.ChatCompletionMessageParam
import com.openai.models.chat.completions.ChatCompletionStreamOptions
import com.openai.models.chat.completions.ChatCompletionUserMessageParam
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flow
import me.kpavlov.aimocks.openai.official.AbstractOpenaiTest
import me.kpavlov.aimocks.openai.openai
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

internal class StreamingChatCompletionOpenaiTest : AbstractOpenaiTest() {
    @Test
    fun `Should respond to Streaming Chat Completion`() {
        openai.completion("openai-completions-list") {
            temperature = temperatureValue
            model = modelName
            topP = topPValue
        } respondsStream {
            responseChunks = listOf("All", " we", " need", " is", " Love")
            delay = 50.milliseconds
            delayBetweenChunks = 10.milliseconds
            finishReason = "stop"
        }

        verifyStreamingCall()
    }

    @Test
    fun `Should respond to Streaming Chat Completion with Flow`() {
        openai.completion("openai-completions-flow") {
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
            @Suppress("deprecation")
            ChatCompletionCreateParams
                .builder()
                .streamOptions(
                    ChatCompletionStreamOptions.builder().includeUsage(true).build(),
                ).temperature(temperatureValue)
                .topP(topPValue)
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
