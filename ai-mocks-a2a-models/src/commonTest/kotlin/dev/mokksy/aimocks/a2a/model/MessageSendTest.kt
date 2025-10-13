package dev.mokksy.aimocks.a2a.model

import dev.mokksy.test.utils.deserializeAndSerialize
import io.kotest.matchers.shouldBe
import kotlin.test.Test

/**
 * https://a2a-protocol.org/latest/specification/#71-messagesend
 */
internal class MessageSendTest {
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
