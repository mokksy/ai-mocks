package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

/**
 * https://a2a-protocol.org/latest/specification/#74-taskscancel
 */
internal class CancelTaskTest : AbstractSerializationTest() {
    @Test
    fun `Deserialize and Serialize CancelTaskRequest`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "method":"tasks/cancel",
              "params": {
                "id": "de38c76d-d54c-436c-8b9f-4c2703648d64",
                "metadata": {}
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<CancelTaskRequest>(payload)
        model.id shouldBe 1
        model.params.id shouldBe "de38c76d-d54c-436c-8b9f-4c2703648d64"
    }

    @Test
    fun `Deserialize and Serialize CancelTaskResponse`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "result": {
                "id": "de38c76d-d54c-436c-8b9f-4c2703648d64",
                "sessionId": "c295ea44-7543-4f78-b524-7a38915ad6e4",
                "status": {
                  "state": "canceled"
                },
                "kind": "task",
                "metadata": {}
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<CancelTaskResponse>(payload)
        model.id shouldBe 1
        model.result?.id shouldBe "de38c76d-d54c-436c-8b9f-4c2703648d64"
        model.result?.sessionId shouldBe "c295ea44-7543-4f78-b524-7a38915ad6e4"
        model.result?.status?.state shouldBe "canceled"
    }
}
