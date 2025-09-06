package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

/**
 * https://github.com/google/A2A/blob/gh-pages/documentation.md#sample-methods-and-json-responses
 */
internal class MessageSendTest : AbstractSerializationTest() {
    @Test
    fun `Deserialize and Serialize with integer id`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "method":"message/send",
              "params": {
                "message": {
                  "role":"user",
                  "parts": [{
                    "kind":"text",
                    "text": "tell me a joke"
                  }]
                },
                "metadata": {}
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<SendMessageRequest>(payload)
        model.id shouldBe 1
    }

    @Test
    fun `Deserialize and Serialize with string id`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": "abc123",
              "method":"message/send",
              "params": {
                "message": {
                  "role":"user",
                  "parts": [{
                    "kind":"text",
                    "text": "tell me a joke"
                  }]
                },
                "metadata": {}
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<SendMessageRequest>(payload)
        model.id shouldBe "abc123"
    }
}
