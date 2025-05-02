package me.kpavlov.aimocks.a2a.notifications

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import me.kpavlov.aimocks.a2a.model.TaskId
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for the TaskNotificationHistory class.
 *
 * The class manages a history of task update events for a specific task ID,
 * providing methods for adding, retrieving, searching, and clearing those events.
 */
internal class TaskNotificationHistoryTest {
    private lateinit var history: TaskNotificationHistory
    private lateinit var taskId: TaskId
    private lateinit var event: TaskUpdateEvent
    private lateinit var event1: TaskUpdateEvent
    private lateinit var event2: TaskUpdateEvent

    @BeforeTest
    fun beforeEach() {
        taskId = UUID.randomUUID().toString()
        history = TaskNotificationHistory(taskId)
        event = mockk<TaskUpdateEvent>()
        event1 = mockk<TaskUpdateEvent>()
        event2 = mockk<TaskUpdateEvent>()
    }

    @Test
    fun `add should store a new event in the event history`() {
        history.add(event)

        history.events() shouldContainExactly listOf(event)
    }

    @Test
    fun `clear should remove all stored events`() {
        history.add(event1)
        history.add(event2)
        history.clear()

        history.isEmpty() shouldBe true
        history.events() shouldHaveSize 0
    }

    @Test
    fun `events should return all added events in insertion order`() {
        history.add(event1)
        history.add(event2)

        history.events() shouldContainExactly listOf(event1, event2)
    }

    @Test
    fun `isEmpty should return true if the event history is empty`() {
        assertTrue(history.isEmpty())
    }

    @Test
    fun `isEmpty should return false if the event history is not empty`() {
        history.add(event)

        assertFalse(history.isEmpty())
    }

    @Test
    fun `isNotEmpty should return true if the event history is not empty`() {
        history.add(event)

        history.isNotEmpty() shouldBe true
    }

    @Test
    fun `find should return the first matching event`() {
        history.add(event1)
        history.add(event2)

        val result = history.find { it == event1 }

        result shouldBe event1
    }

    @Test
    fun `find should return null if no event matches the predicate`() {
        history.add(event)

        val result = history.find { it.toString() == "nonexistent" }

        result shouldBe null
    }

    @Test
    fun `extract should remove and return all events matching the predicate`() {
        history.add(event1)
        history.add(event2)

        val extracted = history.extract { it == event1 }

        extracted shouldContainExactly listOf(event1)
        history.events() shouldContainExactly listOf(event2)
    }

    @Test
    fun `extract should return an empty list if no events match the predicate`() {
        history.add(event)

        val extracted = history.extract { it.toString() == "nonexistent" }

        extracted shouldHaveSize 0
        history.events() shouldContainExactly listOf(event)
    }
}
