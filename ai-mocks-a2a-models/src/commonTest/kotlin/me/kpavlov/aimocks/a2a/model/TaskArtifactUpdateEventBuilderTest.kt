package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class TaskArtifactUpdateEventBuilderTest {
    @Test
    fun `should build TaskArtifactUpdateEvent with required parameters`() {
        // given
        val textPart = TextPart(text = "Sample text content")
        val artifact = Artifact(name = "artifact-name", parts = listOf(textPart))

        // when
        val event =
            TaskArtifactUpdateEventBuilder()
                .id("task-123")
                .artifact(artifact)
                .build()

        // then
        event.id shouldBe "task-123"
        event.artifact shouldBe artifact
        event.metadata shouldBe null
    }

    @Test
    fun `should build TaskArtifactUpdateEvent with all parameters`() {
        // given
        val textPart = TextPart(text = "Sample text content")
        val artifact = Artifact(name = "artifact-name", parts = listOf(textPart))
        val metadata = Metadata.of("metaKey" to "metaValue")

        // when
        val event =
            TaskArtifactUpdateEventBuilder()
                .id("task-123")
                .artifact(artifact)
                .metadata(metadata)
                .build()

        // then
        event.id shouldBe "task-123"
        event.artifact shouldBe artifact
        event.metadata shouldBe metadata
    }

    @Test
    fun `should build TaskArtifactUpdateEvent with artifact builder`() {
        // when
        val event =
            TaskArtifactUpdateEventBuilder()
                .id("task-123")
                .artifact {
                    name("artifact-name")
                    addPart(TextPart(text = "Sample text content"))
                }.build()

        // then
        event.id shouldBe "task-123"
        event.artifact.name shouldBe "artifact-name"
        event.artifact.parts.size shouldBe 1
        (event.artifact.parts[0] as TextPart).text shouldBe "Sample text content"
        event.metadata shouldBe null
    }

    @Test
    fun `should fail when id is not provided`() {
        // given
        val textPart = TextPart(text = "Sample text content")
        val artifact = Artifact(name = "artifact-name", parts = listOf(textPart))

        // when/then
        assertFailsWith<IllegalArgumentException> {
            TaskArtifactUpdateEventBuilder()
                .artifact(artifact)
                .build()
        }
    }

    @Test
    fun `should fail when artifact is not provided`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            TaskArtifactUpdateEventBuilder()
                .id("task-123")
                .build()
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // when
        val event =
            taskArtifactUpdateEvent {
                id("task-123")
                artifact {
                    name("artifact-name")
                    addPart(TextPart(text = "Sample text content"))
                }
            }

        // then
        event.id shouldBe "task-123"
        event.artifact.name shouldBe "artifact-name"
        event.artifact.parts.size shouldBe 1
        (event.artifact.parts[0] as TextPart).text shouldBe "Sample text content"
        event.metadata shouldBe null
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val event =
            TaskArtifactUpdateEvent.create {
                id("task-123")
                artifact {
                    name("artifact-name")
                    addPart(TextPart(text = "Sample text content"))
                }
            }

        // then
        event.id shouldBe "task-123"
        event.artifact.name shouldBe "artifact-name"
        event.artifact.parts.size shouldBe 1
        (event.artifact.parts[0] as TextPart).text shouldBe "Sample text content"
        event.metadata shouldBe null
    }
}
