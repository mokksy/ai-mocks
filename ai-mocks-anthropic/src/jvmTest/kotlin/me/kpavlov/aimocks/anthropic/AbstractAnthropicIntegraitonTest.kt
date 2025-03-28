package me.kpavlov.aimocks.anthropic

import com.anthropic.models.messages.Model
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.util.UUID
import kotlin.random.Random

val anthropic = MockAnthropic(verbose = true)

internal abstract class AbstractAnthropicIntegraitonTest {
    protected var temperatureValue: Double = -1.0
    protected lateinit var userIdValue: String
    protected var maxTokensValue: Long = -1
    protected lateinit var modelName: String
    protected val logger = logger(name = this::class.simpleName!!)

    @BeforeEach
    fun beforeEach() {
        modelName =
            arrayOf(
                Model.CLAUDE_3_7_SONNET_LATEST.asString(),
                Model.CLAUDE_3_5_HAIKU_LATEST.asString(),
                Model.CLAUDE_3_OPUS_LATEST.asString(),
                Model.CLAUDE_3_7_SONNET_20250219.asString(),
            ).random()
        userIdValue = UUID.randomUUID().toString()
        temperatureValue = Random.nextDouble(0.0, 1.0)
        maxTokensValue = Random.nextLong(100, 500)
    }

    @AfterEach
    fun verifyNoUnmatchedRequests() {
        anthropic.verifyNoUnmatchedRequests()
    }
}
