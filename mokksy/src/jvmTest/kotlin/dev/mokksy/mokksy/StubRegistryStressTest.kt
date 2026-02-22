package dev.mokksy.mokksy

import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.StressOptions
import org.junit.jupiter.api.Disabled
import kotlin.test.Test

@Disabled
class StubRegistryStressTest {
    private val registry = StubRegistry()

    @Operation
    fun `getAll should return a consistent snapshot`() =
        runTest {
            val s1 = createStub<String, String>(name = "s1", requestType = String::class)
            val s2 = createStub<String, String>(name = "s2", requestType = String::class)

            registry.add(s1)
            val snapshot1 = registry.getAll()
            snapshot1 shouldContainExactly listOf(s1)

            registry.add(s2)
            val snapshot2 = registry.getAll()

            snapshot1 shouldContainExactly listOf(s1)
            snapshot2 shouldContainExactly listOf(s1, s2)
        }

    @Test
    fun stressTest(): Unit =
        StressOptions()
            .invocationsPerIteration(50)
            .iterations(10)
            .check(this::class)
}
