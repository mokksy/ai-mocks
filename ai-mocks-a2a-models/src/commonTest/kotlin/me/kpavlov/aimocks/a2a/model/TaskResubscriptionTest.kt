package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

/**
 * https://github.com/google/A2A/blob/gh-pages/documentation.md#resubscribe-to-task
 */
internal class TaskResubscriptionTest : AbstractSerializationTest() {
    @Test
    fun `Deserialize and Serialize TaskResubscriptionRequest`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "method":"tasks/resubscribe",
              "params": {
                "id": "de38c76d-d54c-436c-8b9f-4c2703648d64",
                "metadata": {}
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<TaskResubscriptionRequest>(payload)
        model.method shouldBe "tasks/resubscribe"
        model.params.id shouldBe "de38c76d-d54c-436c-8b9f-4c2703648d64"
    }
}
