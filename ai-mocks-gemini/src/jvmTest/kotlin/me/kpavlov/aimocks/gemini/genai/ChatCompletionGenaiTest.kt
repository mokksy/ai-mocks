package me.kpavlov.aimocks.gemini.genai

import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Part
import io.kotest.matchers.shouldBe
import me.kpavlov.aimocks.gemini.gemini
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
            seed = seedValue
            model = modelName
            project = projectId
            location = locationId
            apiVersion = "v1beta1"
            systemMessageContains("You are a helpful pirate")
            userMessageContains("Just say 'Hello!'")
        } responds {
            content = "Ahoy there, matey! Hello!"
            delay = 60.milliseconds
        }

        val response =
            client.models.generateContent(
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


//        val chunkCount =
//            prepareClientRequest()
//                .stream()
//                .chatResponse()
//                .doOnNext { chunk ->
//                    chunk.result.output.text?.let(buffer::append)
//                }.count()
//                .block(5.seconds.toJavaDuration())
//
//        chunkCount shouldBe 4 + 2L // 4 data chunks + opening and closing chunks
        response.text() shouldBe "Ahoy there, matey! Hello!"
    }
}
