package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class TaskStatusBuilderTest {
    @Test
    fun `should build TaskStatus with required parameters`() {
        // when
        val status =
            TaskStatusBuilder()
                .state("completed")
                .build()

        // then
        status.state shouldBe "completed"
        status.message shouldBe null
        status.timestamp shouldBe null
    }

    @Test
    fun `should build TaskStatus with TaskState enum`() {
        for (state in TaskState.entries) {
            // when
            val status =
                TaskStatus.create {
                    state(state)
                }

            // then
            status.state shouldBe state.value
            status.message shouldBe null
            status.timestamp shouldBe null
        }
    }

    @Test
    fun `should build TaskStatus with all parameters`() {
        // given
        val message =
            Message.create {
                role(Message.Role.agent)
                addPart(TextPart(text = "Task completed successfully"))
            }
        val timestamp = Instant.parse("2023-01-01T00:00:00Z")

        // when
        val status =
            taskStatus {
                state = "completed"
                this.message = message
                this.timestamp = timestamp
            }

        // then
        status.state shouldBe "completed"
        status.message shouldBe message
        status.timestamp shouldBe timestamp
    }

    @Test
    fun `should fail when state is not provided`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            TaskStatusBuilder().build()
        }
    }

    @Test
    fun `should fail when state is invalid`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            TaskStatusBuilder()
                .state("invalid-state")
                .build()
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // given
        val message =
            Message.create {
                role(Message.Role.agent)
                addPart(TextPart(text = "Task completed successfully"))
            }
        val timestamp = Instant.parse("2023-01-01T00:00:00Z")

        // when
        val status =
            taskStatus {
                state("completed")
                message(message)
                timestamp(timestamp)
            }

        // then
        status.state shouldBe "completed"
        status.message shouldBe message
        status.timestamp shouldBe timestamp
    }

    @Test
    fun `should build using companion object create function`() {
        // given
        val message =
            Message.create {
                role(Message.Role.agent)
                addPart(TextPart(text = "Task completed successfully"))
            }
        val timestamp = Instant.parse("2023-01-01T00:00:00Z")

        // when
        val status =
            TaskStatus.create {
                state("completed")
                message(message)
                timestamp(timestamp)
            }

        // then
        status.state shouldBe "completed"
        status.message shouldBe message
        status.timestamp shouldBe timestamp
    }
}
