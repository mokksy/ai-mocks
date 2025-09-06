package me.kpavlov.aimocks.ollama.lc4j

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
import org.junit.jupiter.api.Disabled
import kotlin.test.Test
import kotlin.test.fail
import kotlin.time.Duration.Companion.milliseconds

@Disabled("todo: Make it work")
internal class StreamingChatCompletionLc4jTest : AbstractMockOllamaTest() {
    private val model by lazy {
        OllamaStreamingChatModel
            .builder()
            .baseUrl(mockOllama.baseUrl())
            .modelName(modelName)
            .temperature(temperatureValue)
            .logRequests(true)
            .logResponses(true)
            .topP(topPValue)
            .build()
    }

    @Test
    fun `Should respond to Streaming Chat Completion`() =
        runTest {
            // Configure mock response
            val expectedResponse = "Hello, how can I help you today?"
            mockOllama.chat {
                model = modelName
                seed = seedValue
                temperature = temperatureValue
                userMessageContains("Hello")
            } respondsStream {
                responseFlow =
                    expectedResponse
                        .splitToSequence(" ")
                        .asFlow()
                        .map { "$it " }
                delay = 42.milliseconds
            }

            // Use langchain4j Ollama client to send a request
            val tokens = mutableListOf<String>()
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
                .joinToString()
                .removeSuffix(" ") shouldBe expectedResponse
        }
}
