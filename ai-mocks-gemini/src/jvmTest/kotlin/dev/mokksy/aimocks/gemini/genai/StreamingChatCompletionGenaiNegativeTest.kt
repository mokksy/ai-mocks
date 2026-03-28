package dev.mokksy.aimocks.gemini.genai

import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.genai.Client
import com.google.genai.errors.ClientException
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.HttpOptions
import com.google.genai.types.Part
import dev.mokksy.aimocks.gemini.MockGemini
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

internal class StreamingChatCompletionGenaiNegativeTest {
    private val mock = MockGemini(verbose = true)

    private val projectId = "1234567890"
    private val locationId = "amsterdam-central1"

    private val client: Client =
        Client
            .builder()
            .project(projectId)
            .location(locationId)
            .credentials(
                GoogleCredentials.create(
                    AccessToken.newBuilder().setTokenValue("dummy-token").build(),
                ),
            ).vertexAI(true)
            .httpOptions(HttpOptions.builder().baseUrl(mock.baseUrl()).build())
            .build()

    private var temperatureValue: Double = -1.0
    private var seedValue: Int = -1
    private var topPValue: Double = -1.0
    private var topKValue: Long = -1
    private var maxCompletionTokensValue: Long = -1
    private lateinit var modelName: String

    @BeforeEach
    fun beforeEach() {
        modelName =
            arrayOf(
                "gemini-2.0-flash",
                "gemini-2.0-flash-lite",
                "gemini-2.5-flash-preview-04-17",
                "gemini-2.5-pro-preview-03-25",
            ).random()
        seedValue = Random.nextInt(1, 100500)
        topPValue = Random.nextDouble(0.1, 1.0)
        topKValue = Random.nextLong(1, 42)
        temperatureValue = Random.nextDouble(0.0, 1.0)
        maxCompletionTokensValue = Random.nextLong(100, 500)
    }

    @ParameterizedTest
    @MethodSource("requestMutators")
    fun `Should miss response when request does not match`(
        mutator: GenerateContentConfig.Builder.() -> Unit,
    ) {
        mock.generateContentStream {
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

        val configBuilder =
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
                        .parts(Part.fromText("You are a helpful pirate"))
                        .build(),
                )
        mutator(configBuilder)

        val exception =
            shouldThrowExactly<ClientException> {
                client.models
                    .generateContentStream(
                        modelName,
                        "Just say 'Hello!'",
                        configBuilder.build(),
                    ).joinToString(separator = "") { it.text().orEmpty() }
            }
        exception.code() shouldBe 404
    }

    companion object {
        @JvmStatic
        fun requestMutators(): Array<GenerateContentConfig.Builder.() -> Unit> =
            arrayOf(
                { topK(Random.nextDouble(0.1, 1.0).toFloat()) },
                { topP(Random.nextDouble(0.1, 1.0).toFloat()) },
                { temperature(Random.nextDouble(0.1, 1.0).toFloat()) },
                { maxOutputTokens(Random.nextInt(100, 500)) },
            )
    }
}
