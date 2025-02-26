package me.kpavlov.aimocks.openai.springai

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import me.kpavlov.aimocks.openai.openai
import me.kpavlov.aimocks.openai.springai.AbstractSpringAiTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

internal class StreamingChatCompletionSpringAiTest : AbstractSpringAiTest() {
    @Test
    fun `Should respond with stream to Chat Completion`() =
        runTest {
            openai.completion {
                temperature = temperatureValue
                seed = seedValue
                model = modelName
                maxCompletionTokens = maxCompletionTokensValue
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
