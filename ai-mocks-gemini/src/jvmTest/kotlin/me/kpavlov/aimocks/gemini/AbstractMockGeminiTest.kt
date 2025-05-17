package me.kpavlov.aimocks.gemini

import com.google.cloud.vertexai.VertexAI
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import java.time.Instant
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

val gemini = MockGemini(verbose = true)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal abstract class AbstractMockGeminiTest {
    protected var temperatureValue: Double = -1.0
    protected var seedValue: Int = -1
    protected var topPValue: Double = -1.0
    protected var topKValue: Long = -1
    protected var maxCompletionTokensValue: Long = -1
    protected lateinit var modelName: String
    protected val logger = KotlinLogging.logger(name = this::class.simpleName!!)
    protected lateinit var startTimestamp: Instant

    protected lateinit var projectId: String
    protected lateinit var locationId: String

    protected lateinit var vertexAI: VertexAI

    @BeforeAll
    fun beforeAll() {
        projectId = "1234567890"
        locationId = "us-central1"

        vertexAI =
            createTestVertexAI(
                endpoint = gemini.baseUrl(),
                projectId = projectId,
                location = locationId,
                timeout = 5.seconds,
            )
    }

    @BeforeEach
    fun beforeEach() {
        modelName = arrayOf("gemini-2.0-flash").random()
        seedValue = Random.nextInt(1, 100500)
        topPValue = Random.nextDouble(0.1, 1.0)
        topKValue = Random.nextLong(1, 42)
        temperatureValue = Random.nextDouble(0.0, 1.0)
        maxCompletionTokensValue = Random.nextLong(100, 500)
        startTimestamp = Instant.now()
    }

    @AfterEach
    fun verifyNoUnmatchedRequests() {
        gemini.verifyNoUnmatchedRequests()
    }
}
