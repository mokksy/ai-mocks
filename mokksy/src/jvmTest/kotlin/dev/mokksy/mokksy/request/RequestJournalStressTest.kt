package dev.mokksy.mokksy.request

import dev.mokksy.mokksy.JournalMode
import io.ktor.http.HttpMethod
import org.jetbrains.lincheck.datastructures.ModelCheckingOptions
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.StressOptions
import org.junit.jupiter.api.Disabled
import kotlin.test.Test

@Disabled
class RequestJournalStressTest {
    private val journal = RequestJournal(JournalMode.FULL)

    // Pre-create requests to avoid lincheck transforming Ktor classes
    private val matchedRequest = RecordedRequest(HttpMethod.Get, "/matched", emptyMap())
    private val unmatchedRequest = RecordedRequest(HttpMethod.Get, "/unmatched", emptyMap())

    @Operation
    fun recordMatched() {
        journal.recordMatched(matchedRequest)
    }

    @Operation
    fun recordUnmatched() {
        journal.recordUnmatched(unmatchedRequest)
    }

    @Operation
    fun getMatchedCount(): Int = journal.getMatched().size

    @Operation
    fun getUnmatchedCount(): Int = journal.getUnmatched().size

    @Test
    fun stressTest() {
        StressOptions()
            .invocationsPerIteration(100)
            .iterations(10)
            .check(this::class)
    }

    @Test
    fun modelCheckingTest() {
        ModelCheckingOptions()
            .invocationsPerIteration(100)
            .iterations(10)
            .check(this::class)
    }
}
