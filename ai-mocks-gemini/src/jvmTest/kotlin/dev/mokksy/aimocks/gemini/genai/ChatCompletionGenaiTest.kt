package dev.mokksy.aimocks.gemini.genai

import dev.mokksy.aimocks.gemini.gemini
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

/**
 * Some examples:
 * - https://github.com/googleapis/java-genai?tab=readme-ov-file#generate-content
 */
internal class ChatCompletionGenaiTest : AbstractGenaiTest() {
    @Test
    fun `Should respond with stream to generateContent`() {
        val systemMessage = "You are a helpful pirate."
        gemini.generateContent {
            temperature = temperatureValue
            model = modelName
            project = projectId
            location = locationId
            apiVersion = "v1beta1"
            seed = seedValue
            topK = topKValue
            topP = topPValue
            maxOutputTokens(maxCompletionTokensValue)
            systemMessageContains(systemMessage)
            userMessageContains("Just say 'Hello!'")
        } responds {
            content = "Ahoy there, matey! Hello!"
            delay = 60.milliseconds
        }

        val response =
            client.models.generateContent(
                modelName,
                "Just say 'Hello!' as you do it",
                generateContentConfig(systemMessage)
                    .build(),
            )

        response.text() shouldBe "Ahoy there, matey! Hello!"
    }
}
