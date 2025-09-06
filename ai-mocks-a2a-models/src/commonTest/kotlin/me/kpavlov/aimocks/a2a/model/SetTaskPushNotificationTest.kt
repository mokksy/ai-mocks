package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

/**
 * https://github.com/google/A2A/blob/gh-pages/documentation.md#set-task-push-notifications
 */
internal class SetTaskPushNotificationTest : AbstractSerializationTest() {
    @Test
    fun `Deserialize and Serialize SetTaskPushNotificationRequest`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "method":"tasks/pushNotificationConfig/set",
              "params": {
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

        val model = deserializeAndSerialize<SetTaskPushNotificationRequest>(payload)
        model.id shouldBe 1
        model.params.id shouldBe "de38c76d-d54c-436c-8b9f-4c2703648d64"
        model.params.pushNotificationConfig.url shouldBe "https://example.com/callback"
        model.params.pushNotificationConfig.authentication
            ?.schemes shouldBe listOf("jwt")
    }

    @Test
    fun `Deserialize and Serialize SetTaskPushNotificationResponse`() {
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

        val model = deserializeAndSerialize<SetTaskPushNotificationResponse>(payload)
        model.id shouldBe 1
        model.result?.id shouldBe "de38c76d-d54c-436c-8b9f-4c2703648d64"
        model.result?.pushNotificationConfig?.url shouldBe "https://example.com/callback"
        model.result
            ?.pushNotificationConfig
            ?.authentication
            ?.schemes shouldBe listOf("jwt")
    }
}
