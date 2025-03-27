package me.kpavlov.aimocks.anthropic.official

import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.Metadata
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.stream.consumeAsFlow
import kotlinx.coroutines.test.runTest
import me.kpavlov.aimocks.anthropic.anthropic
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTimedValue

internal class AnthropicSdkStreamingMessagesTest : AbstractAnthropicTest() {
    @Test
    fun `Should respond to Streaming Messages Completion with chunk list`() =
        runTest {
            anthropic.messages("anthropic-messages-list") {
                temperature = temperatureValue
                model = modelName
                userId = userIdValue
            } respondsStream {
                responseChunks = listOf("All", " we", " need", " is", " Love")
                delay = 50.milliseconds
                delayBetweenChunks = 10.milliseconds
                stopReason = "end_turn"
            }

            verifyStreamingCall()
        }

    @Test
    fun `Should respond to Streaming Chat Completion with Flow`() =
        runTest {
            anthropic.messages("anthropic-messages-flow") {
                temperature = temperatureValue
                model = modelName
                userId = userIdValue
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
                stopReason = "end_turn"
            }

            verifyStreamingCall()
        }

    private suspend fun verifyStreamingCall() {
        val params =
            MessageCreateParams
                .builder()
                .temperature(temperatureValue)
                .maxTokens(maxCompletionTokensValue)
                .metadata(Metadata.builder().userId(userIdValue).build())
                .system("You are a man from 60s")
                .addUserMessage("What do we need?")
                .model(modelName)
                .build()

        val timedValue =
            measureTimedValue {
                client
                    .messages()
                    .createStreaming(params)
                    .stream()
                    .consumeAsFlow()
                    .onStart { logger.info { "Started streaming" } }
                    .onEach {
                        logger
                            .info { it }
                    }.onCompletion { logger.info { "Completed streaming" } }
                    .count()
            }
        timedValue.duration shouldBeLessThan 10.seconds
        timedValue.value shouldBeLessThanOrEqual 10
    }
}
