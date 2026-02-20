package dev.mokksy.mokksy.request

import dev.mokksy.mokksy.JournalMode
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import kotlin.test.Test

internal class RequestJournalTest {
    private val request1 = RecordedRequest(HttpMethod.Get, "/test1", emptyMap())
    private val request2 =
        RecordedRequest(
            HttpMethod.Post,
            "/test2",
            mapOf("Content-Type" to listOf("application/json")),
        )

    @Test
    fun `LEAN mode should only record unmatched requests`() {
        val journal = RequestJournal(JournalMode.LEAN)

        journal.recordMatched(request1)
        journal.recordUnmatched(request2)

        journal.getMatched().shouldBeEmpty()
        journal.getUnmatched() shouldBe listOf(request2)
    }

    @Test
    fun `FULL mode should record both matched and unmatched requests`() {
        val journal = RequestJournal(JournalMode.FULL)

        journal.recordMatched(request1)
        journal.recordUnmatched(request2)

        journal.getMatched() shouldBe listOf(request1)
        journal.getUnmatched() shouldBe listOf(request2)
    }

    @Test
    fun `clear should empty both matched and unmatched history`() {
        val journal = RequestJournal(JournalMode.FULL)

        journal.recordMatched(request1)
        journal.recordUnmatched(request2)

        journal.getMatched() shouldHaveSize 1
        journal.getUnmatched() shouldHaveSize 1

        journal.clear()

        journal.getMatched().shouldBeEmpty()
        journal.getUnmatched().shouldBeEmpty()
    }
}
