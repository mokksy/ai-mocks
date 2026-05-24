package dev.mokksy.aimocks.gemini.genai

import dev.mokksy.aimocks.gemini.gemini
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTimedValue

/**
 * Some examples:
 * - [Streaming_REST.ipynb](https://github.com/google-gemini/cookbook/blob/main/quickstarts/rest/Streaming_REST.ipynb)
 */
internal class StreamingChatCompletionGenaiTest : AbstractGenaiTest() {
    @Test
    fun `Should respond with stream to generateContentStream`() {
        val systemMessage = "You are a helpful pirate."
        val initialDelay = 60.milliseconds
        val flowDelay = 100.milliseconds
        val delayBetweenChunks = 15.milliseconds
        val tokens = listOf("Ahoy", " there,", " matey!", " Hello!")
        gemini.generateContentStream {
            temperature = temperatureValue
            apiVersion = "v1beta1"
            location = locationId
            maxOutputTokens(maxCompletionTokensValue)
            model = modelName
            project = projectId
            seed = seedValue
            systemMessageContains(systemMessage)
            topK = topKValue
            topP = topPValue
            userMessageContains("Just say 'Hello!'")
        } respondsStream {
            responseFlow =
                flow {
                    tokens.forEachIndexed { index, token ->
                        if (index > 0 && index == 2) {
                            delay(flowDelay)
                        }
                        emit(token)
                    }
                }
            delay = initialDelay
            this.delayBetweenChunks = delayBetweenChunks
        }

        val timedValue =
            measureTimedValue {
                client.models
                    .generateContentStream(
                        modelName,
                        "Just say 'Hello!'",
                        generateContentConfig(systemMessage)
                            .build(),
                    ).toList()
            }

        val expectedMinDuration = initialDelay + flowDelay + delayBetweenChunks * (tokens.size - 1)
        assertSoftly(timedValue) {
            val chunkTexts = value.mapNotNull { chunk -> chunk.text() }
            chunkTexts shouldContainExactly listOf("Ahoy", " there,", " matey!", " Hello!", "")
            duration shouldBeGreaterThanOrEqualTo expectedMinDuration
        }
    }
}
