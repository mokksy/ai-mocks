package me.kpavlov.aimocks.gemini.genai

import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Part
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import me.kpavlov.aimocks.gemini.gemini
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds


/**
 * Some examples:
 * - [Streaming_REST.ipynb](https://github.com/google-gemini/cookbook/blob/main/quickstarts/rest/Streaming_REST.ipynb)
 */
internal class StreamingChatCompletionGenaiTest : AbstractGenaiTest() {
    @Test
    fun `Should respond with stream to generateContentStream`() {
        gemini.generateContentStream {
            temperature = temperatureValue
            apiVersion = "v1beta1"
            model = modelName
            project = projectId
            location = locationId
            systemMessageContains("You are a helpful pirate")
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
                "Just say 'Hello!'", GenerateContentConfig.builder()
                    .seed(seedValue)
                    .temperature(temperatureValue.toFloat())
                    .systemInstruction(
                        Content.builder().role("system")
                            .parts(Part.fromText("You are a helpful pirate")).build()
                    )
                    .build()
            )

        response.joinToString(separator = "") {
            it.text() ?: ""
        } shouldBe "Ahoy there, matey! Hello!"
    }
}
