package me.kpavlov.aimocks.anthropic.official

import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.Metadata
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import me.kpavlov.aimocks.anthropic.anthropic
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

@Disabled("TODO: Fix me ")
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
                    " $seed,",
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
                systemMessageContains(seed)
            } respondsStream {
                responseFlow =
                    flow {
                        tokens.forEach { emit(it) }
                    }.buffer(Channel.UNLIMITED)
                delay = 60.milliseconds
                delayBetweenChunks = 15.milliseconds
                stopReason = "end_turn"
            }

            verifyStreamingCall(tokens)
        }

    private fun verifyStreamingCall(tokens: List<String>) {
        val params =
            MessageCreateParams
                .builder()
                .temperature(temperatureValue)
                .maxTokens(maxTokensValue)
                .metadata(Metadata.builder().userId(userIdValue).build())
                .system("You are a person from 60s $seed")
                .addUserMessage("What do we need?")
                .model(modelName)
                .build()

        val buffer =
            client
                .messages()
                .createStreaming(params)
                .stream()
                .filter { it.isContentBlockDelta() }
                .map {
                            it
                                .asContentBlockDelta()
                                .delta()
                                .asText()
                                .text()
                }.toList()

        logger.info { buffer.joinToString("") }
        buffer shouldContainExactly tokens
    }
}
