package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

/**
 * https://github.com/google/A2A/blob/gh-pages/documentation.md#get-task-push-notifications
 */
internal class GetTaskPushNotificationTest : AbstractSerializationTest() {
    @Test
    fun `Deserialize and Serialize GetTaskPushNotificationRequest`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "method":"tasks/pushNotification/get",
              "params": {
                "id": "de38c76d-d54c-436c-8b9f-4c2703648d64"
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<GetTaskPushNotificationRequest>(payload)
        model.id shouldBe 1
        model.params.id shouldBe "de38c76d-d54c-436c-8b9f-4c2703648d64"
    }

    @Test
    fun `Deserialize and Serialize GetTaskPushNotificationResponse`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "result": {
                "id": "de38c76d-d54c-436c-8b9f-4c2703648d64",
                "pushNotificationConfig": {
                  "url": "https://example.com/callback",
                  "authentication": {
                    "schemes": ["jwt"]
                  }
                }
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<GetTaskPushNotificationResponse>(payload)
        model.id shouldBe 1
        model.result?.id shouldBe "de38c76d-d54c-436c-8b9f-4c2703648d64"
        model.result?.pushNotificationConfig?.url shouldBe "https://example.com/callback"
        model.result
            ?.pushNotificationConfig
            ?.authentication
            ?.schemes shouldBe listOf("jwt")
    }
}
