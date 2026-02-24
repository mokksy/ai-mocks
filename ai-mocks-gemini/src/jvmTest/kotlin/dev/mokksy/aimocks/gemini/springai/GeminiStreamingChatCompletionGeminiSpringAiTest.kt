package dev.mokksy.aimocks.gemini.springai

import dev.mokksy.aimocks.gemini.gemini
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactor.awaitSingle
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

/**
 * Some examples:
 * - [Streaming_REST.ipynb](https://github.com/google-gemini/cookbook/blob/main/quickstarts/rest/Streaming_REST.ipynb)
 */
internal class GeminiStreamingChatCompletionGeminiSpringAiTest : AbstractGeminiSpringAiTest() {
    @Test
    suspend fun `Should respond with stream to generateContentStream`() {
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
                        emit(" matey!")
                        emit(" Hello!")
                    }
                delay = 60.milliseconds
                delayBetweenChunks = 50.milliseconds
            }

        val chunks =
            prepareClientRequest(systemMessage)
                .stream()
                .chatResponse()
                .collectList()
                .awaitSingle()

        chunks shouldHaveSize (4 + 1) // 4 data chunks + 1 final chunk
        chunks.map { it.result.output.text } shouldBe
            listOf(
                "Ahoy",
                " there,",
                " matey!",
                " Hello!",
                "",
            )
    }
}
