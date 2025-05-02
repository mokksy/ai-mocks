package me.kpavlov.aimocks.anthropic.official

import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.Metadata
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.stream.consumeAsFlow
import kotlinx.coroutines.test.runTest
import me.kpavlov.aimocks.anthropic.anthropic
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

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
            } respondsStream {
                responseFlow =
                    flow {
                        tokens.forEach { emit(it) }
                    }
                delay = 60.milliseconds
                delayBetweenChunks = 15.milliseconds
                stopReason = "end_turn"
            }

            verifyStreamingCall(tokens)
        }

    private suspend fun verifyStreamingCall(tokens: List<String>) {
        val params =
            MessageCreateParams
                .builder()
                .temperature(temperatureValue)
                .maxTokens(maxTokensValue)
                .metadata(Metadata.builder().userId(userIdValue).build())
                .system("You are a person from 60s")
                .addUserMessage("What do we need?")
                .model(modelName)
                .build()

        val buffer =
            client
                .messages()
                .createStreaming(params)
                .stream()
                .consumeAsFlow()
                .buffer(capacity = 8096)
                .filter { it.isContentBlockDelta() }
                .map {
                    it
                        .asContentBlockDelta()
                        .delta()
                        .asText()
                        .text()
                }.toList(mutableListOf())

        logger.info { buffer.joinToString("") }
        buffer shouldContainExactly tokens
    }
}
