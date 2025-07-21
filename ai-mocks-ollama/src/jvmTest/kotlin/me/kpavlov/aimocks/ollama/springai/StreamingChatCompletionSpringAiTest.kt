package me.kpavlov.aimocks.ollama.springai

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import me.kpavlov.aimocks.ollama.mockOllama
import org.junit.jupiter.api.Disabled
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Disabled("todo: Make it work")
internal class StreamingChatCompletionSpringAiTest : AbstractSpringAiTest() {
    @Test
    fun `Should respond with stream to Chat Completion`() {
        mockOllama.chat {
            temperature = temperatureValue
            seed = seedValue
            model = modelName
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
//            finishReason = "stop"
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
