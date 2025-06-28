package me.kpavlov.aimocks.anthropic.official

import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.Metadata
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import me.kpavlov.aimocks.anthropic.anthropic
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal class AnthropicSdkStreamingMessagesTest : AbstractAnthropicTest() {
    @Test
    fun `Should respond to Streaming Messages Completion with chunk list`() =
        runTest {
            val tokens = listOf("All", " we", " need", " is", " Love")
            anthropic.messages("openai-completions-list") {
                temperature = temperatureValue
                model = modelName
                userId = userIdValue
            } respondsStream {
                responseChunks = tokens
                delay = 50.milliseconds
                delayBetweenChunks = 10.milliseconds
                stopReason = "end_turn"
            }

            verifyStreamingCall(tokens)
        }

    @Test
    fun `Should respond to Streaming Chat Completion with Flow`() =
        runTest {
            val tokens =
                listOf(
                    "What",
                    " we",
                    " need",
                    " is",
                    " a",
                    " revolution",
                    " of",
                    " love,",
                    " $seedValue,",
                    " understanding,",
                    " and",
                    " social",
                    " change.",
                    " ✌️☮️🪷",
                )
            anthropic.messages("openai-completions-flow") {
                temperature = temperatureValue
                model = modelName
                userId = userIdValue
                systemMessageContains(seedValue)
            } respondsStream {
                responseFlow =
                    flow {
                        tokens.forEach { emit(it) }
                    }.buffer(Channel.UNLIMITED)
                delay = 60.milliseconds
                delayBetweenChunks = 1.seconds
                stopReason = "end_turn"
            }

            verifyStreamingCall(tokens)
        }

    @OptIn(FlowPreview::class)
    private suspend fun verifyStreamingCall(tokens: List<String>) {
        val params =
            MessageCreateParams
                .builder()
                .temperature(temperatureValue)
                .maxTokens(maxTokensValue)
                .metadata(Metadata.builder().userId(userIdValue).build())
                .system("You are a person from 60s $seedValue")
                .addUserMessage("What do we need?")
                .model(modelName)
                .build()

        val buffer = mutableListOf<String>()
        client
            .messages()
            .createStreaming(params).use { streamResponse ->
                streamResponse.stream()
                    .peek { chunk ->
                        logger.trace { "✅ $chunk" }
                    }
                    .filter { it.isContentBlockDelta() }
                    .forEachOrdered { chunk ->
                        chunk
                            .asContentBlockDelta()
                            .delta()
                            .asText()
                            .text()
                            .let(buffer::add)
                    }
            }

        logger.info { buffer.joinToString("") }
        buffer shouldContainExactly tokens
    }
}
