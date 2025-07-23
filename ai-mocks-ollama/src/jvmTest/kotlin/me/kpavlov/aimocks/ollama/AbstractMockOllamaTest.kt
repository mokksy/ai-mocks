package me.kpavlov.aimocks.ollama

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random

val mockOllama = MockOllama(verbose = true)

internal abstract class AbstractMockOllamaTest {
    protected var temperatureValue: Double = -1.0
    protected var seedValue: Int = 42
    protected var topPValue: Double = -1.0
    protected lateinit var modelName: String
    protected lateinit var embeddingModelName: String
    protected val logger = KotlinLogging.logger(name = this::class.simpleName!!)
    protected lateinit var startTimestamp: java.time.Instant

    @BeforeEach
    fun beforeEach() {
        modelName = arrayOf("llama3", "llama3.1", "llama3.2", "mistral", "gemma").random()
        embeddingModelName = arrayOf("llama3", "llama3.1", "llama3.2", "all-minilm").random()
        topPValue = Random.nextDouble(0.1, 1.0)
        temperatureValue = Random.nextDouble(0.0, 1.0)
        seedValue = Random.nextInt(1, 100500)
        startTimestamp = java.time.Instant.now()
    }

    @AfterEach
    fun verifyNoUnmatchedRequests() {
        mockOllama.verifyNoUnmatchedRequests()
    }
}
