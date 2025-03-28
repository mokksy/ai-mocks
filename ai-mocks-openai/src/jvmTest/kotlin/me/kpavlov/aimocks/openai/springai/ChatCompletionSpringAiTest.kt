package me.kpavlov.aimocks.openai.springai

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import me.kpavlov.aimocks.openai.openai
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
            systemMessageContains("helpful pirate")
            userMessageContains("say 'Hello!'")
        } responds {
            assistantContent = "Ahoy there, matey! Hello!"
            finishReason = "stop"
        }

        val response =
            prepareClientRequest()
                .call()
                .chatResponse()

        response?.result shouldNotBe null
        response?.result?.apply {
            metadata.finishReason shouldBe "STOP"
            output?.text shouldBe "Ahoy there, matey! Hello!"
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
            prepareClientRequest()
                .stream()
                .chatResponse()
                .doOnNext {
                    it.result.output.text?.let {
                        buffer.append(it)
                    }
                }.count()
                .block(5.seconds.toJavaDuration())

        chunkCount shouldBe 4 + 2L // 4 data chunks + opening and closing chunks
        buffer.toString() shouldBe "Ahoy there, matey! Hello!"
    }
}
