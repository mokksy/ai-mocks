package dev.mokksy.aimocks.openai.springai

import dev.mokksy.aimocks.openai.ChatCompletionRequest
import dev.mokksy.aimocks.openai.openai
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.beOfType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

internal class ChatCompletionSpringAiTest : AbstractSpringAiTest() {
    @Test
    fun `Should respond to Chat Completion`() {
        openai.completion {
            temperature = temperatureValue
            seed = seedValue
            model = modelName
            maxTokens = maxCompletionTokensValue
            systemMessageContains("helpful merchant")
            userMessageContains("say 'Hello!'")
            requestMatchesPredicate {
                !it.stream
            }
        } responds {
            assistantContent = "Hello there! Welcome to our shop! How can I assist you today?"
            finishReason = "stop"
        }

        val response =
            prepareClientRequest("You are a helpful merchant")
                .call()
                .chatResponse()

        response?.result shouldNotBe null
        response?.result?.apply {
            metadata.finishReason shouldBe "STOP"
            output.text shouldBe "Hello there! Welcome to our shop! How can I assist you today?"
        }
    }

    @Test
    fun `Should respond with stream to Chat Completion`() {
        openai.completion {
            temperature = temperatureValue
            seed = seedValue
            model = modelName
            maxTokens = maxCompletionTokensValue
            systemMessageContains("helpful pirate")
            userMessageContains("say 'Hello!'")
            requestMatches(beOfType(ChatCompletionRequest::class))
            requestSatisfies("Should be streamed") {
                it?.stream shouldBe true
            }
        } respondsStream {
            responseFlow =
                flow {
                    emit("Ahoy")
                    emit(" there,")
                    delay(100.milliseconds)
                    emit(" matey!")
                    emit(" Hello!")
                }
            delay = 60.milliseconds
            delayBetweenChunks = 15.milliseconds
            finishReason = "stop"
        }

        val buffer = StringBuffer()
        val chunkCount =
            prepareClientRequest("You are a helpful pirate")
                .stream()
                .chatResponse()
                .doOnNext { chunk ->
                    chunk.result.output.text?.let {
                        buffer.append(it)
                    }
                }.count()
                .block(5.seconds.toJavaDuration())

        chunkCount shouldBe 4 + 2L // 4 data chunks + opening and closing chunks
        buffer.toString() shouldBe "Ahoy there, matey! Hello!"
    }
}
