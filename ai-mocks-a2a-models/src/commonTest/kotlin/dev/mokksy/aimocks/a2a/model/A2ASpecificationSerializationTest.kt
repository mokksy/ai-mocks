package dev.mokksy.aimocks.a2a.model

import dev.mokksy.test.utils.deserializeAndSerialize
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random
import kotlin.test.Test

/**
 * Serialization tests based on examples from A2A Protocol Specification v0.3.0
 * These tests ensure our models can correctly serialize/deserialize the exact JSON formats
 * shown in the official specification examples.
 */
@OptIn(ExperimentalEncodingApi::class)
internal class A2ASpecificationSerializationTest {
    @Test
    fun `Deserialize and Serialize message send request with messageId from spec`() {
        // Example from Section 9.2 Basic Execution (Synchronous / Polling Style)
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "method": "message/send",
              "params": {
                "message": {
                  "role": "user",
                  "parts": [
                    {
                      "kind": "text",
                      "text": "tell me a joke"
                    }
                  ],
                  "messageId": "9229e770-767c-417b-a0b0-f0741243c589"
                },
                "metadata": {}
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<SendMessageRequest>(payload)
        model.id shouldBe 1
        model.method shouldBe "message/send"
        model.params.message.role.name shouldBe "user"
        model.params.message.parts.size shouldBe 1
        (model.params.message.parts[0] as? TextPart)?.text shouldBe "tell me a joke"
    }

    @Test
    @Suppress("LongMethod")
    fun `Deserialize and Serialize message send response with task from spec`() {
        // Example from Section 9.2 Basic Execution - task response
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "result": {
                "id": "363422be-b0f9-4692-a24d-278670e7c7f1",
                "contextId": "c295ea44-7543-4f78-b524-7a38915ad6e4",
                "status": {
                  "state": "completed"
                },
                "artifacts": [
                  {
                    "artifactId": "9b6934dd-37e3-4eb1-8766-962efaab63a1",
                    "name": "joke",
                    "parts": [
                      {
                        "kind": "text",
                        "text": "Why did the chicken cross the road? To get to the other side!"
                      }
                    ]
                  }
                ],
                "history": [
                  {
                    "role": "user",
                    "parts": [
                      {
                        "kind": "text",
                        "text": "tell me a joke"
                      }
                    ],
                    "messageId": "9229e770-767c-417b-a0b0-f0741243c589",
                    "taskId": "363422be-b0f9-4692-a24d-278670e7c7f1",
                    "contextId": "c295ea44-7543-4f78-b524-7a38915ad6e4"
                  }
                ],
                "kind": "task",
                "metadata": {}
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<SendMessageResponse>(payload)
        model.id shouldBe 1
        model.result?.id shouldBe "363422be-b0f9-4692-a24d-278670e7c7f1"
        model.result?.status?.state shouldBe "completed"
        model.result?.artifacts?.size shouldBe 1
        model.result
            ?.artifacts
            ?.get(0)
            ?.name shouldBe "joke"
        (
            model.result
                ?.artifacts
                ?.get(0)
                ?.parts
                ?.get(0) as? TextPart
        )?.text shouldBe
            "Why did the chicken cross the road? To get to the other side!"
    }

    /*
    @Test
    fun `Deserialize and Serialize message send response without task from spec`() {
        // Example from Section 9.2 Basic Execution - direct message response
        // Note: This test is commented out because it requires polymorphic handling
        // The specification shows that message/send can return either a Task or a direct Message
        // This would require significant architectural changes to support both response types
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "result": {
                "messageId": "363422be-b0f9-4692-a24d-278670e7c7f1",
                "contextId": "c295ea44-7543-4f78-b524-7a38915ad6e4",
                "parts": [
                  {
                    "kind": "text",
                    "text": "Why did the chicken cross the road? To get to the other side!"
                  }
                ],
                "kind": "message",
                "metadata": {}
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<SendMessageResponse>(payload)
        model.id shouldBe 1
        // Additional assertions would depend on how we model direct message responses
    }
     */

    @Test
    fun `Deserialize and Serialize message stream request from spec`() {
        // Example from Section 9.3 Streaming Task Execution (SSE)
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "method": "message/stream",
              "params": {
                "message": {
                  "role": "user",
                  "parts": [
                    {
                      "kind": "text",
                      "text": "write a long paper describing the attached pictures"
                    },
                    {
                      "kind": "file",
                      "file": {
                        "mimeType": "image/png",
                        "bytes": "${Base64.Mime.encode(Random.nextBytes(10))}"
                      }
                    }
                  ],
                  "messageId": "bbb7dee1-cf5c-4683-8a6f-4114529da5eb"
                },
                "metadata": {}
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
    }

    @Test
    fun `Deserialize and Serialize SSE task status update from spec`() {
        // Example from Section 9.3 Streaming Task Execution - SSE event
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "result": {
                "id": "225d6247-06ba-4cda-a08b-33ae35c8dcfa",
                "contextId": "05217e44-7e9f-473e-ab4f-2c2dde50a2b1",
                "status": {
                  "state": "submitted",
                  "timestamp": "2025-04-02T16:59:25.331844Z"
                },
                "history": [
                  {
                    "role": "user",
                    "parts": [
                      {
                        "kind": "text",
                        "text": "write a long paper describing the attached pictures"
                      },
                      {
                        "kind": "file",
                        "file": {
                          "mimeType": "image/png",
                          "bytes": "${Base64.Mime.encode(Random.nextBytes(10))}"
                        }
                      }
                    ],
                    "messageId": "bbb7dee1-cf5c-4683-8a6f-4114529da5eb",
                    "taskId": "225d6247-06ba-4cda-a08b-33ae35c8dcfa",
                    "contextId": "05217e44-7e9f-473e-ab4f-2c2dde50a2b1"
                  }
                ],
                "kind": "task",
                "metadata": {}
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<SendMessageResponse>(payload)
        model.id shouldBe 1
        model.result?.id shouldBe "225d6247-06ba-4cda-a08b-33ae35c8dcfa"
        model.result?.status?.state shouldBe "submitted"
        model.result?.status?.timestamp shouldBe Instant.parse("2025-04-02T16:59:25.331844Z")
    }

    /*
    // Note: This test is commented out because the AgentGetAuthenticatedExtendedCardRequest model
    // doesn't exist yet - this would be needed for complete specification compliance

    @Test
    fun `Deserialize and Serialize agent getAuthenticatedExtendedCard request from spec`() {
        // Example from Section 9.1 Authentication Flow
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "method": "agent/getAuthenticatedExtendedCard"
            }
            """.trimIndent()

        val model = deserializeAndSerialize<AgentGetAuthenticatedExtendedCardRequest>(payload)
        model.id shouldBe 1
        model.method shouldBe "agent/getAuthenticatedExtendedCard"
    }
     */
}
