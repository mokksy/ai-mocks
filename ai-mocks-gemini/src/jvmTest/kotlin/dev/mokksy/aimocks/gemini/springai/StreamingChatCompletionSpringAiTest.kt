package dev.mokksy.aimocks.gemini.springai

import dev.mokksy.aimocks.gemini.gemini
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
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
        val systemMessage = "You are a helpful pirate. $seedValue"
        gemini
            .generateContentStream {
                temperature = temperatureValue
                model = modelName
                project = projectId
                location = locationId
                systemMessageContains(systemMessage)
                userMessageContains("Just say 'Hello!'")
            }.respondsStream(sse = false) {
                responseFlow =
                    flow {
                        emit("Ahoy")
                        emit(" there,")
                        delay(300.milliseconds)
                        emit(" matey!")
                        emit(" Hello!")
                    }
                delay = 60.milliseconds
                delayBetweenChunks = 50.milliseconds
            }

        val chunkList =
            prepareClientRequest(systemMessage)
                .stream()
                .chatResponse()
                .doOnNext { chunk ->
                    logger.debug { "✅ Received chunk: $chunk" }
                }.collectList()
                .block(5.seconds.toJavaDuration())!!

        chunkList.map { it.result.output.text } shouldBe
            listOf(
                "Ahoy",
                " there,",
                " matey!",
                " Hello!",
                "",
            )
    }
}
