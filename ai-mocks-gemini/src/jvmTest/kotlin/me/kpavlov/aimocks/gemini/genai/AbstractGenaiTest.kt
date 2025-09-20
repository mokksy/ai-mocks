package me.kpavlov.aimocks.gemini.genai

import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.genai.Client
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.HttpOptions
import com.google.genai.types.Part
import me.kpavlov.aimocks.gemini.AbstractMockGeminiTest
import me.kpavlov.aimocks.gemini.gemini
import kotlin.random.Random

internal abstract class AbstractGenaiTest : AbstractMockGeminiTest() {
    protected val client =
        Client
            .builder()
            .project(projectId)
            .location(locationId)
            .credentials(
                GoogleCredentials.create(
                    AccessToken.newBuilder().setTokenValue("dummy-token").build(),
                ),
            ).vertexAI(true)
            .httpOptions(HttpOptions.builder().baseUrl(gemini.baseUrl()).build())
            .build()

    protected fun generateContentConfig(systemPrompt: String): GenerateContentConfig.Builder =
        GenerateContentConfig
            .builder()
            .seed(seedValue)
            .maxOutputTokens(maxCompletionTokensValue.toInt())
            .temperature(temperatureValue.toFloat())
            .topK(topKValue.toFloat())
            .topP(topPValue.toFloat())
            .systemInstruction(
                Content
                    .builder()
                    .role("system")
                    .parts(Part.fromText(systemPrompt))
                    .build(),
            )

    protected fun requestMutators(): Array<GenerateContentConfig.Builder.() -> Unit> =
        arrayOf(
            { topK(Random.nextDouble(0.1, 1.0).toFloat()) },
            { topP(Random.nextDouble(0.1, 1.0).toFloat()) },
            { temperature(Random.nextDouble(0.1, 1.0).toFloat()) },
            { maxOutputTokens(Random.nextInt(100, 500)) },
        )
}
