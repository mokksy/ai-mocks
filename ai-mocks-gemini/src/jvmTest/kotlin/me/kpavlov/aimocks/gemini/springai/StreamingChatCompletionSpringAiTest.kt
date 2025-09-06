package me.kpavlov.aimocks.gemini.springai

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import me.kpavlov.aimocks.gemini.gemini
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

/**
 * Some examples:
 * - [Streaming_REST.ipynb](https://github.com/google-gemini/cookbook/blob/main/quickstarts/rest/Streaming_REST.ipynb)
 */
internal class StreamingChatCompletionSpringAiTest : AbstractSpringAiTest() {
    @Test
    fun `Should respond with stream to generateContentStream`() {
        gemini
            .generateContentStream {
                temperature = temperatureValue
                model = modelName
                project = projectId
                location = locationId
                systemMessageContains("You are a helpful pirate")
                userMessageContains("Just say 'Hello!'")
            }.respondsStream(sse = false) {
                responseFlow =
                    flow {
                        emit("Ahoy")
                        emit(" there,")
                        delay(100.milliseconds)
                        emit(" matey!")
                        emit(" Hello!")
                    }
                delay = 60.milliseconds
                delayBetweenChunks = 50.milliseconds
            }

        val buffer = StringBuffer()
        val chunkCount =
            prepareClientRequest()
                .stream()
                .chatResponse()
                .doOnNext { chunk ->
                    logger.debug { "✅ Received chunk: $chunk" }
                    chunk.result.output.text
                        ?.let(buffer::append)
                }.count()
                .block(5.seconds.toJavaDuration())

        chunkCount shouldBe 5
        buffer.toString() shouldBe "Ahoy there, matey! Hello!"
    }
}
