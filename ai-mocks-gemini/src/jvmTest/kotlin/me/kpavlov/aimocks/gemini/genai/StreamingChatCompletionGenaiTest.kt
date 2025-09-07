package me.kpavlov.aimocks.gemini.genai

import com.google.genai.errors.ClientException
import com.google.genai.types.GenerateContentConfig
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import me.kpavlov.aimocks.gemini.gemini
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
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
            it.text() ?: ""
        } shouldBe "Ahoy there, matey! Hello!"
    }

    @ParameterizedTest
    @MethodSource("requestMutators")
    fun `Should miss response when request does not match`(mutator: GenerateContentConfig.Builder.() -> Unit) {
        gemini.generateContentStream {
            apiVersion = "v1beta1"
            location = locationId
            maxOutputTokens(maxCompletionTokensValue)
            model = modelName
            project = projectId
            seed = seedValue
            systemMessageContains("You are a helpful pirate")
            temperature = temperatureValue
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

        val configBuilder = generateContentConfig("You are a helpful pirate")
        mutator(configBuilder)

        val exception =
            shouldThrowExactly<ClientException> {
                client.models.generateContentStream(
                    modelName,
                    "Just say 'Hello!'",
                    configBuilder
                        .build(),
                )
            }
        exception.code() shouldBe 404
    }
}
