package me.kpavlov.aimocks.gemini.genai

import com.google.genai.errors.ClientException
import com.google.genai.types.GenerateContentConfig
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import me.kpavlov.aimocks.gemini.gemini
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

/**
 * Some examples:
 * - https://github.com/googleapis/java-genai?tab=readme-ov-file#generate-content
 */
internal class ChatCompletionGenaiTest : AbstractGenaiTest() {
    @Test
    fun `Should respond with stream to generateContent`() {
        gemini.generateContent {
            temperature = temperatureValue
            model = modelName
            project = projectId
            location = locationId
            apiVersion = "v1beta1"
            systemMessageContains("You are a helpful pirate. $seedValue")
            userMessageContains("Just say 'Hello!'")
        } responds {
            content = "Ahoy there, matey! Hello!"
            delay = 60.milliseconds
        }

        val response =
        client.models.generateContent(
                modelName,
                "Just say 'Hello!'",
                generateContentConfig("You are a helpful pirate. $seedValue")
                    .build(),
            )

        response.text() shouldBe "Ahoy there, matey! Hello!"
    }

    @ParameterizedTest
    @MethodSource("requestMutators")
    fun `Should miss response when request does not match`(
        mutator: GenerateContentConfig.Builder.() -> Unit,
    ) {
        gemini.generateContent {
            temperature = temperatureValue
            seed = seedValue
            topK = topKValue
            topP = topPValue
            model = modelName
            project = projectId
            location = locationId
            apiVersion = "v1beta1"
            maxOutputTokens(maxCompletionTokensValue)
            systemMessageContains("You are a helpful pirate")
            userMessageContains("Just say 'Hello!'")
            requestMatchesPredicate { it.generationConfig?.topP == topPValue }
        } responds {
            content = "Ahoy there, matey! Hello!"
            delay = 60.milliseconds
        }

        val configBuilder = generateContentConfig("You are a helpful pirate")
        mutator(configBuilder)

        val exception =
            shouldThrowExactly<ClientException> {
                client.models.generateContent(
                    modelName,
                    "Just say 'Hello!'",
                    configBuilder
                        .build(),
                )
            }
        exception.code() shouldBe 404
    }
}
