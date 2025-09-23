package me.kpavlov.aimocks.a2a.model.serializers

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.a2a.model.TaskArtifactUpdateEvent
import me.kpavlov.aimocks.a2a.model.TaskStatusUpdateEvent
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent
import me.kpavlov.aimocks.a2a.model.TextPart
import kotlin.test.Test

internal class TaskUpdateEventSerializerTest {
    @Test
    fun `Deserialize TaskStatusUpdateEvent as TaskUpdateEvent`() {
        // language=json
        val payload =
            """
            {
              "id": "1",
              "status": {
                "state": "working",
                "timestamp":"2025-04-02T16:59:25.331844Z"
              },
              "final": false
            }
            """.trimIndent()

        // Deserialize as TaskUpdateEvent (polymorphic)
        val model = Json.decodeFromString<TaskUpdateEvent>(payload)

        // Verify it's the correct subclass
        model.shouldBeInstanceOf<TaskStatusUpdateEvent>()

        // Verify properties
        val statusEvent = model
        statusEvent.id shouldBe "1"
        statusEvent.status.state shouldBe "working"
        statusEvent.status.timestamp shouldBe Instant.parse("2025-04-02T16:59:25.331844Z")
        statusEvent.final shouldBe false

        // Verify serialization
        val encoded = Json.encodeToString(TaskUpdateEvent.serializer(), model)
        val decoded = Json.decodeFromString<TaskUpdateEvent>(encoded)
        decoded.shouldBeInstanceOf<TaskStatusUpdateEvent>()
    }

    @Test
    fun `Deserialize TaskArtifactUpdateEvent as TaskUpdateEvent`() {
        // language=json
        val payload =
            """
            {
              "id": "1",
              "artifact": {
                "parts": [
                  {"kind":"text", "text": "<section 1...>"}
                ],
                "index": 0,
                "append": false,
                "lastChunk": false
              }
            }
            """.trimIndent()

        // Deserialize as TaskUpdateEvent (polymorphic)
        val model = Json.decodeFromString<TaskUpdateEvent>(payload)

        // Verify it's the correct subclass
        model.shouldBeInstanceOf<TaskArtifactUpdateEvent>()

        // Verify properties
        val artifactEvent = model
        artifactEvent.id shouldBe "1"
        artifactEvent.artifact.parts.size shouldBe 1
        (artifactEvent.artifact.parts[0] as? TextPart)?.text shouldBe "<section 1...>"
        artifactEvent.artifact.index shouldBe 0
        artifactEvent.artifact.append shouldBe false
        artifactEvent.artifact.lastChunk shouldBe false

        // Verify serialization
        val encoded = Json.encodeToString(TaskUpdateEvent.serializer(), model)
        val decoded = Json.decodeFromString<TaskUpdateEvent>(encoded)
        decoded.shouldBeInstanceOf<TaskArtifactUpdateEvent>()
    }

    @Test
    fun `Error on invalid JSON without status or artifact`() {
        // language=json
        val payload =
            """
            {
              "id": "1"
            }
            """.trimIndent()

        // This should throw an exception because it can't determine the subclass
        try {
            Json.decodeFromString<TaskUpdateEvent>(payload)
            throw AssertionError("Expected SerializationException but no exception was thrown")
        } catch (e: Exception) {
            // Expected exception
            e.message?.contains("neither 'artifact' nor 'status' property found") shouldBe true
        }
    }
}
