package me.kpavlov.aimocks.a2a.model

import dev.mokksy.test.utils.deserializeAndSerialize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

/**
 * https://a2a-protocol.org/latest/specification/#71-messagesend
 */
internal class SendMessageResponseTest {
    @Test
    fun `Deserialize and Serialize SendMessageResponse`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "result": {
                "id": "de38c76d-d54c-436c-8b9f-4c2703648d64",
                "contextId": "c295ea44-7543-4f78-b524-7a38915ad6e4",
                "status": {
                  "state": "completed"
                },
                "artifacts": [{
                  "index": 1,
                  "name":"joke",
                  "parts": [{
                      "kind":"text",
                      "text":"Why did the chicken cross the road? To get to the other side!"
                    }]
                  }],
                "kind": "task",
                "metadata": {}
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<SendMessageResponse>(payload)
        model.id shouldBe 1
        model.result shouldNotBeNull {
            id shouldBe "de38c76d-d54c-436c-8b9f-4c2703648d64"
            contextId shouldBe "c295ea44-7543-4f78-b524-7a38915ad6e4"
            status.state shouldBe "completed"
            artifacts?.size shouldBe 1
            artifacts
                ?.get(0)
                ?.name shouldBe "joke"
            artifacts
                ?.get(0)
                ?.parts
                ?.size shouldBe 1
            val part =
                artifacts
                    ?.get(0)
                    ?.parts
                    ?.get(0)
            (part as? TextPart)?.text shouldBe
                "Why did the chicken cross the road? To get to the other side!"
        }
    }
}
