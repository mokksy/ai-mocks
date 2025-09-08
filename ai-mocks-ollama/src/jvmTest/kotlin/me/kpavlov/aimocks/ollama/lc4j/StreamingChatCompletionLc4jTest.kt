package me.kpavlov.aimocks.ollama.lc4j

import assertk.assertThat
import assertk.assertions.isGreaterThanOrEqualTo
import dev.langchain4j.data.message.UserMessage.userMessage
import dev.langchain4j.kotlin.model.chat.StreamingChatModelReply
import dev.langchain4j.kotlin.model.chat.chatFlow
import dev.langchain4j.model.ollama.OllamaStreamingChatModel
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import me.kpavlov.aimocks.ollama.AbstractMockOllamaTest
import me.kpavlov.aimocks.ollama.mockOllama
import kotlin.test.Test
import kotlin.test.fail
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

internal class StreamingChatCompletionLc4jTest : AbstractMockOllamaTest() {
    private val model by lazy {
        OllamaStreamingChatModel
            .builder()
            .customHeaders(
                mapOf(
                    "Content-Type" to "application/json", // add lost header
                ),
            ).baseUrl(mockOllama.baseUrl())
            .modelName(modelName)
            .temperature(temperatureValue)
            .logRequests(true)
            .logResponses(true)
            .topP(topPValue)
            .seed(seedValue)
            .topK(topKValue.toInt())
            .build()
    }

    @Test
    fun `Should respond to Streaming Chat Completion`() =
        runTest {
            // Configure mock response
            val expectedResponse = "Hello, how can I help you today?"

            val delayBetweenChunks = 100.milliseconds
            val initialDelay = 500.milliseconds

            mockOllama.chat {
                model = modelName
                seed = seedValue
                temperature = temperatureValue
                userMessageContains("Hello")
                topP = topPValue
                topK = topKValue
                stream = true
            } respondsStream {
                responseFlow =
                    expectedResponse
                        .splitToSequence(" ")
                        .asFlow()
                        .map { "$it " }
                this.delay = initialDelay
                this.delayBetweenChunks = delayBetweenChunks
            }

            // Use langchain4j Ollama client to send a request
            val tokens = mutableListOf<String>()
            val executionTime =
                measureTime {
                    model
                        .chatFlow {
                            messages += userMessage("Hello")
                        }.collect { reply ->
                            when (reply) {
                                is StreamingChatModelReply.PartialResponse -> {
                                    tokens += reply.partialResponse
                                }

                                is StreamingChatModelReply.CompleteResponse -> {
                                    reply.response.modelName() shouldBe modelName
                                }

                                is StreamingChatModelReply.Error -> {
                                    fail("Error: $reply", reply.cause)
                                }
                            }
                        }
                    tokens
                        .joinToString("")
                        .removeSuffix(" ") shouldBe expectedResponse
                }

            assertThat(executionTime).isGreaterThanOrEqualTo(
                initialDelay + delayBetweenChunks * tokens.size,
            )
        }
}
