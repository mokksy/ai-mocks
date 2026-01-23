package dev.mokksy.aimocks.a2a.model

import dev.mokksy.test.utils.deserializeAndSerialize
import io.kotest.matchers.shouldBe
import kotlin.test.Test

/**
 * https://a2a-protocol.org/latest/specification/#78-taskspushnotificationconfigdelete
 */
internal class DeleteTaskPushNotificationConfigTest {
    @Test
    fun `Deserialize and Serialize DeleteTaskPushNotificationConfigRequest`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "method": "tasks/pushNotificationConfig/delete",
              "params": {
                "id": "task_12345"
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<DeleteTaskPushNotificationConfigRequest>(payload)
        model.id shouldBe 1
        model.method shouldBe "tasks/pushNotificationConfig/delete"
        model.params?.id shouldBe "task_12345"
        model.params?.metadata shouldBe null
    }

    @Test
    fun `Deserialize and Serialize DeleteTaskPushNotificationConfigRequest with metadata`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": "request-123",
              "method": "tasks/pushNotificationConfig/delete",
              "params": {
                "id": "task_67890",
                "metadata": {
                  "source": "client",
                  "version": 1
                }
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<DeleteTaskPushNotificationConfigRequest>(payload)
        model.id shouldBe "request-123"
        model.method shouldBe "tasks/pushNotificationConfig/delete"
        model.params?.id shouldBe "task_67890"
        model.params?.metadata?.size shouldBe 2
        model.params?.metadata?.containsKey("source") shouldBe true
        model.params?.metadata?.containsKey("version") shouldBe true
    }

    @Test
    fun `Deserialize and Serialize DeleteTaskPushNotificationConfigRequest without params`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "method": "tasks/pushNotificationConfig/delete"
            }
            """.trimIndent()

        val model = deserializeAndSerialize<DeleteTaskPushNotificationConfigRequest>(payload)
        model.id shouldBe 1
        model.method shouldBe "tasks/pushNotificationConfig/delete"
        model.params shouldBe null
    }

    @Test
    fun `Deserialize and Serialize DeleteTaskPushNotificationConfigResponse with success`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1
            }
            """.trimIndent()

        val model = deserializeAndSerialize<DeleteTaskPushNotificationConfigResponse>(payload)
        model.id shouldBe 1
        model.result shouldBe null
        model.error shouldBe null
    }

    @Test
    fun `Deserialize and Serialize DeleteTaskPushNotificationConfigResponse with error`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": 1,
              "error": {
                "code": -32001,
                "message": "Task not found"
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<DeleteTaskPushNotificationConfigResponse>(payload)
        model.id shouldBe 1
        model.result shouldBe null
        model.error?.code shouldBe -32001
        model.error?.message shouldBe "Task not found"
    }

    @Test
    fun `Serializing DeleteTaskPushNotificationConfigResponse with AuthenticatedExtendedCardNotConfiguredError`() {
        // language=json
        val payload =
            """
            {
              "jsonrpc": "2.0",
              "id": "test-123",
              "error": {
                "code": -32007,
                "message": "Authenticated Extended Card not configured"
              }
            }
            """.trimIndent()

        val model = deserializeAndSerialize<DeleteTaskPushNotificationConfigResponse>(payload)
        model.id shouldBe "test-123"
        model.result shouldBe null
        model.error?.code shouldBe -32007
        model.error?.message shouldBe "Authenticated Extended Card not configured"
    }
}
