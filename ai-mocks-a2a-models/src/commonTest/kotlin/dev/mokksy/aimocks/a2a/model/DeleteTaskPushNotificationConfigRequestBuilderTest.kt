package dev.mokksy.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test

internal class DeleteTaskPushNotificationConfigRequestBuilderTest {
    @Test
    fun `should build using top-level DSL function`() {
        // given
        val metadata =
            mapOf<String, JsonElement>(
                "key1" to JsonPrimitive("value1"),
            )

        // when
        val request =
            deleteTaskPushNotificationConfigRequest {
                id = "request-123"
                params {
                    id = "task-123"
                    this.metadata = metadata
                }
            }

        // then
        request.id shouldBe "request-123"
        request.params shouldBe
            DeleteTaskPushNotificationConfigParams(
                id = "task-123",
                metadata = metadata,
            )
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/pushNotificationConfig/delete"
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val request =
            DeleteTaskPushNotificationConfigRequest.create {
                id = "request-123"
                params {
                    id = "task-123"
                }
            }

        // then
        request.id shouldBe "request-123"
        request.params shouldBe
            DeleteTaskPushNotificationConfigParams(
                id = "task-123",
                metadata = null,
            )
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/pushNotificationConfig/delete"
    }

    @Test
    fun `should build using Consumer factory method`() {
        // given
        val metadata =
            mapOf<String, JsonElement>(
                "key1" to JsonPrimitive("value1"),
            )

        // when
        val request =
            deleteTaskPushNotificationConfigRequest { builder ->
                builder.id = "request-789"
                builder.params { paramsBuilder ->
                    paramsBuilder.id = "task-789"
                    paramsBuilder.metadata = metadata
                }
            }

        // then
        request.id shouldBe "request-789"
        request.params shouldBe
            DeleteTaskPushNotificationConfigParams(
                id = "task-789",
                metadata = metadata,
            )
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/pushNotificationConfig/delete"
    }

    @Test
    fun `should build using Consumer companion create method`() {
        // when
        val request =
            DeleteTaskPushNotificationConfigRequest.create { builder ->
                builder.id = "request-999"
                builder.params { paramsBuilder ->
                    paramsBuilder.id = "task-999"
                }
            }

        // then
        request.id shouldBe "request-999"
        request.params shouldBe
            DeleteTaskPushNotificationConfigParams(
                id = "task-999",
                metadata = null,
            )
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/pushNotificationConfig/delete"
    }
}
