package me.kpavlov.aimocks.openai

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.langchain4j.data.message.UserMessage.userMessage
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler
import dev.langchain4j.model.openai.OpenAiChatRequestParameters
import dev.langchain4j.model.openai.OpenAiStreamingChatModel
import dev.langchain4j.model.output.FinishReason
import io.kotest.assertions.failure
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import me.kpavlov.langchain4j.kotlin.model.chat.StreamingChatLanguageModelReply
import me.kpavlov.langchain4j.kotlin.model.chat.StreamingChatLanguageModelReply.CompleteResponse
import me.kpavlov.langchain4j.kotlin.model.chat.StreamingChatLanguageModelReply.PartialResponse
import me.kpavlov.langchain4j.kotlin.model.chat.chatFlow
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference

internal class MockOpenaiLC4jStreamingTest : AbstractMockOpenaiTest() {
    private val model: OpenAiStreamingChatModel =
        OpenAiStreamingChatModel
            .builder()
            .apiKey("foo")
            .baseUrl("http://127.0.0.1:${openai.port()}/v1")
            .build()

    @Test
    fun `Should respond to Streaming Chat Completion`() =
        runTest {
            openai.completion("lc4j-openai-completion-list") {
                temperature = temperatureValue
                model = modelName
                seed = seedValue
                requestBodyContains("What do we need?")
//                userMessage("What do we need?")
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
            openai.completion("lc4j-openai-completion-flow") {
                temperature = temperatureValue
                model = modelName
                seed = seedValue
                requestBodyContains("What is in the sea?")
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
                    println("Received CompleteResponse: $completeResponse")
                    responseRef.set(completeResponse)
                }

                override fun onPartialResponse(partialResponse: String) {
                    println("Received partial response: $partialResponse")
                    partialResults.add(partialResponse)
                }

                override fun onError(error: Throwable) {
                    println("Received error: $error")
                    failure("Unexpected error", error)
                }
            },
        )

        await.untilAsserted {
            responseRef.get().shouldNotBeNull()
//          partialResults.joinToString("") shouldBeEqual expectedResponse
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
            }.collect {
                when (it) {
                    is PartialResponse -> {
                        result.add(it.token)
                        println("token = ${it.token}")
                    }

                    is CompleteResponse -> {
                        println("Completed: $it")
                        finishReason.set(it.response.finishReason())
                    }

                    is StreamingChatLanguageModelReply.Error -> {
                        println("Error: $it")
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
