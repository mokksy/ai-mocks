package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class TaskBuilderTest {
    @Test
    fun `should build Task with required parameters`() {
        // when
        val task =
            TaskBuilder()
                .id("task-123")
                .contextId("context-123")
                .status {
                    state("completed")
                }.build()

        // then
        task.id shouldBe "task-123"
        task.status shouldBe TaskStatus(state = "completed")
        task.contextId shouldBe "context-123"
        task.artifacts shouldBe null
        task.metadata shouldBe null
    }

    @Test
    fun `should build Task with all parameters`() {
        // given
        val status = TaskStatus(state = "completed")
        val textPart = TextPart(text = "Sample text content")
        val artifact = Artifact(name = "artifact-123", parts = listOf(textPart))
        val metadata = Metadata.of("metaKey" to "metaValue")

        // when
        val task =
            TaskBuilder()
                .id("task-123")
                .contextId("ctx-456")
                .status(status)
                .addArtifact(artifact)
                .metadata(metadata)
                .build()

        // then
        task.id shouldBe "task-123"
        task.contextId shouldBe "ctx-456"
        task.status shouldBe status
        task.artifacts shouldBe listOf(artifact)
        task.metadata shouldBe metadata
    }

    @Test
    fun `should build Task with multiple artifacts`() {
        // given
        val textPart1 = TextPart(text = "First text content")
        val textPart2 = TextPart(text = "Second text content")
        val artifact1 = Artifact(name = "artifact-1", parts = listOf(textPart1))
        val artifact2 = Artifact(name = "artifact-2", parts = listOf(textPart2))

        // when
        val task =
            TaskBuilder()
                .id("task-123")
                .contextId("ctx-456")
                .status {
                    state("completed")
                }.addArtifact(artifact1)
                .addArtifact(artifact2)
                .build()

        // then
        task.id shouldBe "task-123"
        task.artifacts shouldBe listOf(artifact1, artifact2)
    }

    @Test
    fun `should set artifacts list`() {
        // given
        val textPart1 = TextPart(text = "First text content")
        val textPart2 = TextPart(text = "Second text content")
        val artifacts =
            listOf(
                Artifact(name = "artifact-1", parts = listOf(textPart1)),
                Artifact(name = "artifact-2", parts = listOf(textPart2)),
            )

        // when
        val task =
            TaskBuilder()
                .id("task-123")
                .contextId("ctx-456")
                .status {
                    state("completed")
                }.artifacts(artifacts)
                .build()

        // then
        task.id shouldBe "task-123"
        task.artifacts shouldBe artifacts
    }

    @Test
    fun `should fail validation when id is not provided`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            TaskBuilder()
                .status {
                    state("completed")
                }.build(validate = true)
        }
    }

    @Test
    fun `should fail validation when status is not provided`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            TaskBuilder()
                .id("task-123")
                .build(validate = true)
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // when
        val task =
            task {
                id("task-123")
                contextId("ctx-456")
                status {
                    state("completed")
                }
            }

        // then
        task.id shouldBe "task-123"
        task.contextId shouldBe "ctx-456"
        task.status shouldBe TaskStatus(state = "completed")
        task.artifacts shouldBe null
        task.metadata shouldBe null
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val task =
            Task.create {
                id("task-123")
                contextId("ctx-456")
                status {
                    state("completed")
                }
            }

        // then
        task.id shouldBe "task-123"
        task.contextId shouldBe "ctx-456"
        task.status shouldBe TaskStatus(state = "completed")
        task.artifacts shouldBe null
        task.metadata shouldBe null
    }
}
