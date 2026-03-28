package dev.mokksy.aimocks.gemini.genai

import dev.mokksy.aimocks.gemini.gemini
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

/**
 * Some examples:
 * - [Streaming_REST.ipynb](https://github.com/google-gemini/cookbook/blob/main/quickstarts/rest/Streaming_REST.ipynb)
 */
internal class StreamingChatCompletionGenaiTest : AbstractGenaiTest() {
    @Test
    fun `Should respond with stream to generateContentStream`() {
        val systemMessage = "You are a helpful pirate. $seedValue"
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
                    emit("Ahoy")
                    emit(" there,")
                    delay(100.milliseconds)
                    emit(" matey!")
                    emit(" Hello!")
                }
            delay = 60.milliseconds
            delayBetweenChunks = 15.milliseconds
        }

        val response =
            client.models.generateContentStream(
                modelName,
                "Just say 'Hello!'",
                generateContentConfig(systemMessage)
                    .build(),
            )

        response.joinToString(separator = "") {
            it.text().orEmpty()
        } shouldBe "Ahoy there, matey! Hello!"
    }

}
