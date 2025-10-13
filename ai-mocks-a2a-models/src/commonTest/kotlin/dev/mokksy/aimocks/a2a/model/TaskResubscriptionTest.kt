package dev.mokksy.aimocks.a2a.model

import dev.mokksy.test.utils.deserializeAndSerialize
import io.kotest.matchers.shouldBe
import kotlin.test.Test

/**
 * https://a2a-protocol.org/latest/specification/#79-tasksresubscribe
 */
internal class TaskResubscriptionTest {
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
