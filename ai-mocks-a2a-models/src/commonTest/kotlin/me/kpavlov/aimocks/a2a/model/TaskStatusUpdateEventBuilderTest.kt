package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class TaskStatusUpdateEventBuilderTest {
    @Test
    fun `should build TaskStatusUpdateEvent with required parameters`() {
        // given
        val status = TaskStatus(state = "completed")

        // when
        val event =
            TaskStatusUpdateEventBuilder()
                .id("task-123")
                .status(status)
                .build()

        // then
        event.id shouldBe "task-123"
        event.status shouldBe status
        event.final shouldBe false
        event.metadata shouldBe null
    }

    @Test
    fun `should build TaskStatusUpdateEvent with all parameters`() {
        // given
        val status = TaskStatus(state = "completed")
        val metadata = Metadata.of("metaKey" to "metaValue")

        // when
        val event =
            TaskStatusUpdateEventBuilder()
                .id("task-123")
                .status(status)
                .isFinal(true)
                .metadata(metadata)
                .build()

        // then
        event.id shouldBe "task-123"
        event.status shouldBe status
        event.final shouldBe true
        event.metadata shouldBe metadata
    }

    @Test
    fun `should build TaskStatusUpdateEvent with status builder`() {
        // when
        val event =
            TaskStatusUpdateEventBuilder()
                .id("task-123")
                .status {
                    state("completed")
                }.build()

        // then
        event.id shouldBe "task-123"
        event.status shouldBe TaskStatus(state = "completed")
        event.final shouldBe false
        event.metadata shouldBe null
    }

    @Test
    fun `should fail when id is not provided`() {
        // given
        val status = TaskStatus(state = "completed")

        // when/then
        assertFailsWith<IllegalArgumentException> {
            TaskStatusUpdateEventBuilder()
                .status(status)
                .build()
        }
    }

    @Test
    fun `should fail when status is not provided`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            TaskStatusUpdateEventBuilder()
                .id("task-123")
                .build()
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // when
        val event =
            taskStatusUpdateEvent {
                id("task-123")
                status {
                    state("completed")
                }
                isFinal(true)
            }

        // then
        event.id shouldBe "task-123"
        event.status shouldBe TaskStatus(state = "completed")
        event.final shouldBe true
        event.metadata shouldBe null
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val event =
            TaskStatusUpdateEvent.create {
                id("task-123")
                status {
                    state("completed")
                }
                isFinal(true)
            }

        // then
        event.id shouldBe "task-123"
        event.status shouldBe TaskStatus(state = "completed")
        event.final shouldBe true
        event.metadata shouldBe null
    }
}
