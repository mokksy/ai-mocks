package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import kotlin.test.Test

/**
 * https://github.com/google/A2A/blob/gh-pages/documentation.md#streaming-support
 */
internal class SendTaskStreamingTest : AbstractSerializationTest() {
    @Test
    fun `Deserialize and Serialize SendTaskStreamingRequest`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 2,
              "method":"tasks/sendSubscribe",
              "params": {
                "id": "de38c76d-d54c-436c-8b9f-4c2703648d64",
                "sessionId": "c295ea44-7543-4f78-b524-7a38915ad6e4",
                "message": {
                  "role":"user",
                  "parts": [{
                    "type":"text",
                    "text": "write a long paper describing the attached pictures"
                  },{
                    "type":"file",
                    "file": {
                       "mimeType": "image/png",
                       "bytes": "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII="
                    }
                  }]
                },
                "metadata": {
                  "foo": "bar",
                  "fee": 42
                }
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<SendTaskStreamingRequest>(payload)
        model.method shouldBe "tasks/sendSubscribe"
        model.params.id shouldBe "de38c76d-d54c-436c-8b9f-4c2703648d64"
        model.params.sessionId shouldBe "c295ea44-7543-4f78-b524-7a38915ad6e4"
        model.params.message.role.name shouldBe "user"
        model.params.message.parts.size shouldBe 2
        (model.params.message.parts[0] as? TextPart)?.text shouldBe
            "write a long paper describing the attached pictures"
        (model.params.message.parts[1] as? FilePart)?.file?.mimeType shouldBe "image/png"
        model.params.metadata?.get("foo") shouldBe "bar"
        model.params.metadata?.get("fee") shouldBe 42
    }

    @Test
    fun `Deserialize and Serialize TaskStatusUpdateEvent`() {
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

        val model = deserializeAndSerialize<TaskStatusUpdateEvent>(payload)
        model.id shouldBe "1"
        model.status.state shouldBe "working"
        model.status.timestamp shouldBe Instant.parse("2025-04-02T16:59:25.331844Z")
        model.final shouldBe false
    }

    @Test
    fun `Deserialize and Serialize TaskArtifactUpdateEvent`() {
        // language=json
        val payload =
            """
            {
              "id": "1",
              "artifact": {
                "parts": [
                  {"type":"text", "text": "<section 1...>"}
                ],
                "index": 0,
                "append": false,
                "lastChunk": false
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<TaskArtifactUpdateEvent>(payload)
        model.id shouldBe "1"
        model.artifact.parts.size shouldBe 1
        (model.artifact.parts[0] as? TextPart)?.text shouldBe "<section 1...>"
        model.artifact.index shouldBe 0
        model.artifact.append shouldBe false
        model.artifact.lastChunk shouldBe false
    }
}
