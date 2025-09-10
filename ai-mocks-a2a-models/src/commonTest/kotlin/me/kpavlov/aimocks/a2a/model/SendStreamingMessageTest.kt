package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import kotlin.test.Test

/**
 * https://a2a-protocol.org/latest/specification/#72-messagestream
 */
internal class SendStreamingMessageTest : AbstractSerializationTest() {
    @Test
    fun `Deserialize and Serialize SendStreamingMessageRequest`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 2,
              "method":"message/stream",
              "params": {
                "message": {
                  "role":"user",
                  "parts": [{
                    "kind":"text",
                    "text": "write a long paper describing the attached pictures"
                  },{
                    "kind":"file",
                    "file": {
                       "mimeType": "image/png",
                       "bytes": "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII="
                    }
                  }]
                },
                "configuration": {
                  "blocking": false
                }
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<SendStreamingMessageRequest>(payload)
        model.method shouldBe "message/stream"
        model.params.message.role.name shouldBe "user"
        model.params.message.parts.size shouldBe 2
        (model.params.message.parts[0] as? TextPart)?.text shouldBe
            "write a long paper describing the attached pictures"
        (model.params.message.parts[1] as? FilePart)?.file?.mimeType shouldBe "image/png"
        model.params.configuration?.blocking shouldBe false
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
                  {"kind":"text", "text": "<section 1...>"}
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
