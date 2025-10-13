package dev.mokksy.aimocks.openai.lc4j

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.langchain4j.data.message.UserMessage.userMessage
import dev.langchain4j.kotlin.model.chat.StreamingChatModelReply
import dev.langchain4j.kotlin.model.chat.chatFlow
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler
import dev.langchain4j.model.openai.OpenAiChatRequestParameters
import dev.langchain4j.model.openai.OpenAiStreamingChatModel
import dev.langchain4j.model.output.FinishReason
import dev.mokksy.aimocks.openai.AbstractMockOpenaiTest
import dev.mokksy.aimocks.openai.openai
import io.kotest.assertions.failure
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference

private const val FLOW_BUFFER_SIZE = 8096

internal class StreamingChatCompletionLc4jTest : AbstractMockOpenaiTest() {
    private val model: OpenAiStreamingChatModel =
        OpenAiStreamingChatModel
            .builder()
            .apiKey("foo")
            .baseUrl("http://127.0.0.1:${openai.port()}/v1")
            .build()

    @Test
    fun `Should respond to Streaming Chat Completion`() =
        runTest {
            openai.completion("lc4j-openai-completions-list") {
                temperature = temperatureValue
                model = modelName
                seed = seedValue
                userMessageContains("What do we need?")
            } respondsStream {
                responseChunks = listOf("All", " we", " need", " is", " Love")
                finishReason = "stop"

                // send "[DONE]" as last message to finish the stream in openai4j
                sendDone = true
            }

            verifyStreamingKotlinFlow("What do we need?", "All we need is Love")
        }

    @Test
    fun `Should respond to Streaming Chat Completion with Flow`() =
        runTest {
            openai.completion("lc4j-openai-completions-flow") {
                temperature = temperatureValue
                model = modelName
                seed = seedValue
                userMessageContains("What is in the sea?")
            } respondsStream {
                responseFlow =
                    flow {
                        emit("Yellow")
                        emit(" submarine")
                    }
                finishReason = "stop"
            }

            verifyLC4JStreamingCall("What is in the sea?", "Yellow submarine")
        }

    private fun verifyLC4JStreamingCall(
        userMessage: String,
        expectedResponse: String,
    ) {
        val partialResults = ConcurrentLinkedQueue<String>()
        val responseRef = AtomicReference<ChatResponse>()
        model.chat(
            ChatRequest
                .builder()
                .parameters(
                    OpenAiChatRequestParameters
                        .builder()
                        .temperature(temperatureValue)
                        .modelName(modelName)
                        .seed(seedValue)
                        .build(),
                ).messages(userMessage(userMessage))
                .build(),
            object : StreamingChatResponseHandler {
                override fun onCompleteResponse(completeResponse: ChatResponse) {
                    logger.info { "Received CompleteResponse: $completeResponse" }
                    responseRef.set(completeResponse)
                }

                override fun onPartialResponse(partialResponse: String) {
                    logger.info { "Received partial response: $partialResponse" }
                    partialResults.add(partialResponse)
                }

                override fun onError(error: Throwable) {
                    logger.info { "Received error: $error" }
                    failure("Unexpected error", error)
                }
            },
        )

        await.untilAsserted {
            responseRef.get().shouldNotBeNull()
        }
        val chatResponse = responseRef.get()
        chatResponse.finishReason() shouldBe FinishReason.STOP
        chatResponse.aiMessage().text() shouldBeEqual expectedResponse
    }

    private suspend fun verifyStreamingKotlinFlow(
        userMessage: String,
        expectedResponse: String,
    ) {
        val result = ConcurrentLinkedQueue<String>()
        val finishReason = AtomicReference<FinishReason>()
        model
            .chatFlow {
                parameters =
                    OpenAiChatRequestParameters
                        .builder()
                        .temperature(temperatureValue)
                        .modelName(modelName)
                        .seed(seedValue)
                        .build()
                messages += userMessage(userMessage)
            }.buffer(capacity = FLOW_BUFFER_SIZE)
            .collect {
                when (it) {
                    is StreamingChatModelReply.PartialResponse -> {
                        result.add(it.partialResponse)
                        logger.info { "token = ${it.partialResponse}" }
                    }

                    is StreamingChatModelReply.CompleteResponse -> {
                        logger.info { "Completed: $it" }
                        finishReason.set(it.response.finishReason())
                    }

                    is StreamingChatModelReply.Error -> {
                        logger.info { "Error: $it" }
                        it.cause.printStackTrace()
                    }
                }
            }

        await.untilAsserted {
            result.joinToString("") shouldBeEqual expectedResponse
        }
        assertThat(finishReason.get()).isEqualTo(FinishReason.STOP)
    }
}
