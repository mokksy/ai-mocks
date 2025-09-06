package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

/**
 * https://github.com/google/A2A/blob/gh-pages/documentation.md#get-a-task
 */
internal class GetTaskTest : AbstractSerializationTest() {
    @Test
    fun `Deserialize and Serialize GetTaskRequest`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "method":"tasks/get",
              "params": {
                "id": "de38c76d-d54c-436c-8b9f-4c2703648d64",
                "historyLength": 10,
                "metadata": {}
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<GetTaskRequest>(payload)
        model.id shouldBe 1
        model.params.id shouldBe "de38c76d-d54c-436c-8b9f-4c2703648d64"
        model.params.historyLength shouldBe 10
    }

    @Test
    fun `Deserialize and Serialize GetTaskResponse`() {
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
                  "state": "completed"
                },
                "artifacts": [{
                  "index": 1,
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

        val model = deserializeAndSerialize<GetTaskResponse>(payload)
        model.id shouldBe 1
        model.result?.id shouldBe "de38c76d-d54c-436c-8b9f-4c2703648d64"
        model.result?.sessionId shouldBe "c295ea44-7543-4f78-b524-7a38915ad6e4"
        model.result?.status?.state shouldBe "completed"
        model.result?.artifacts?.size shouldBe 1
        model.result
            ?.artifacts
            ?.get(0)
            ?.parts
            ?.size shouldBe 1
        val part =
            model.result
                ?.artifacts
                ?.get(0)
                ?.parts
                ?.get(0)
        (part as? TextPart)?.text shouldBe
            "Why did the chicken cross the road? To get to the other side!"
    }
}
