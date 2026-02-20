package dev.mokksy.mokksy.request

import dev.mokksy.mokksy.JournalMode
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

/**
 * Records [RecordedRequest] snapshots for post-test verification,
 * differentiating between requests matched by a stub and those that were not.
 *
 * Recording behaviour is controlled by [JournalMode]:
 * - [dev.mokksy.mokksy.JournalMode.LEAN]: only unmatched requests are recorded (lower overhead).
 * - [dev.mokksy.mokksy.JournalMode.FULL]: all requests are recorded, matched and unmatched alike.
 *
 * All operations are thread-safe and lock-free.
 * @author Konstantin Pavlov
 */
internal class RequestJournal(
    private val mode: JournalMode = JournalMode.LEAN,
) {
    private val matched: AtomicRef<PersistentList<RecordedRequest>> = atomic(persistentListOf())
    private val unmatched: AtomicRef<PersistentList<RecordedRequest>> = atomic(persistentListOf())

    /**
     * Records a request that was successfully matched by a stub.
     * No-op in [JournalMode.LEAN].
     */
    fun recordMatched(request: RecordedRequest) {
        if (mode == JournalMode.FULL) {
            matched.update { it.add(request) }
        }
    }

    /** Records a request for which no stub was found. */
    fun recordUnmatched(request: RecordedRequest) {
        unmatched.update { it.add(request) }
    }

    /**
     * Returns a consistent snapshot of all requests matched by a stub.
     * Always empty in [JournalMode.LEAN].
     */
    fun getMatched(): List<RecordedRequest> = matched.value

    /** Returns a consistent snapshot of all requests that had no matching stub. */
    fun getUnmatched(): List<RecordedRequest> = unmatched.value

    /** Clears both matched and unmatched request histories. */
    fun clear() {
        matched.value = persistentListOf()
        unmatched.value = persistentListOf()
    }
}
