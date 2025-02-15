package me.kpavlov.aimocks.openai

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random

internal abstract class AbstractMockOpenaiTest {
    protected val openai = MockOpenai(verbose = true)

    protected var temperature: Double = -1.0
    protected var seedValue: Int = -1
    protected var maxCompletionTokens: Long = -1

    @BeforeEach
    fun beforeEach() {
        seedValue = Random.nextInt(1, 100500)
        temperature = Random.nextDouble(0.0, 1.0)
        maxCompletionTokens = Random.nextLong(100, 500)
    }

    @AfterEach
    fun verifyNoUnmatchedRequests() {
        openai.verifyNoUnmatchedRequests()
    }
}
