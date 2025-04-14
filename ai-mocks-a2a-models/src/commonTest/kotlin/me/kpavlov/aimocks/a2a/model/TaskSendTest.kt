package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

/**
 * https://github.com/google/A2A/blob/gh-pages/documentation.md#sample-methods-and-json-responses
 */
internal class TaskSendTest : AbstractSerializationTest() {
    @Test
    fun `Deserialize and Serialize with integer id`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "method":"tasks/send",
              "params": {
                "id": "de38c76d-d54c-436c-8b9f-4c2703648d64",
                "message": {
                  "role":"user",
                  "parts": [{
                    "type":"text",
                    "text": "tell me a joke"
                  }]
                },
                "metadata": {}
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<SendTaskRequest>(payload)
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
              "method":"tasks/send",
              "params": {
                "id": "de38c76d-d54c-436c-8b9f-4c2703648d64",
                "message": {
                  "role":"user",
                  "parts": [{
                    "type":"text",
                    "text": "tell me a joke"
                  }]
                },
                "metadata": {}
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<SendTaskRequest>(payload)
        model.id shouldBe "abc123"
    }
}
