package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class SetTaskPushNotificationResponseBuilderTest {
    @Test
    fun `should build SetTaskPushNotificationResponse with id and result`() {
        // when
        val response =
            SetTaskPushNotificationResponseBuilder()
                .apply {
                    id = "request-123"
                    result {
                        id = "task-123"
                        pushNotificationConfig {
                            url = "https://example.org/notifications"
                        }
                    }
                }.build()

        // then
        response.id shouldBe "request-123"
        response.result shouldBe
            TaskPushNotificationConfig(
                id = "task-123",
                pushNotificationConfig =
                    PushNotificationConfig(
                        url = "https://example.org/notifications",
                        token = null,
                    ),
            )
        response.error shouldBe null
        response.jsonrpc shouldBe "2.0"
    }

    @Test
    fun `should build SetTaskPushNotificationResponse with id and error`() {
        // when
        val error =
            JSONRPCError(
                code = 123,
                message = "Error message",
            )
        val response =
            SetTaskPushNotificationResponseBuilder()
                .apply {
                    id = "request-123"
                    error(error)
                }.build()

        // then
        response.id shouldBe "request-123"
        response.result shouldBe null
        response.error shouldBe error
        response.jsonrpc shouldBe "2.0"
    }

    @Test
    fun `should build SetTaskPushNotificationResponse with all parameters`() {
        // when
        val result =
            TaskPushNotificationConfig(
                id = "task-123",
                pushNotificationConfig =
                    PushNotificationConfig(
                        url = "https://example.org/notifications",
                        token = "auth-token",
                    ),
            )
        val error =
            JSONRPCError(
                code = 123,
                message = "Error message",
            )
        val response =
            SetTaskPushNotificationResponseBuilder()
                .apply {
                    id = "request-123"
                    result(result)
                    error(error)
                }.build()

        // then
        response.id shouldBe "request-123"
        response.result shouldBe result
        response.error shouldBe error
        response.jsonrpc shouldBe "2.0"
    }

    @Test
    fun `should build using top-level DSL function`() {
        // when
        val response =
            setTaskPushNotificationResponse {
                id = "request-123"
                result {
                    id = "task-123"
                    pushNotificationConfig {
                        url = "https://example.org/notifications"
                        token = "auth-token"
                    }
                }
            }

        // then
        response.id shouldBe "request-123"
        response.result shouldBe
            TaskPushNotificationConfig(
                id = "task-123",
                pushNotificationConfig =
                    PushNotificationConfig(
                        url = "https://example.org/notifications",
                        token = "auth-token",
                    ),
            )
        response.error shouldBe null
        response.jsonrpc shouldBe "2.0"
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val response =
            SetTaskPushNotificationResponse.create {
                id = "request-123"
                result {
                    id = "task-123"
                    pushNotificationConfig {
                        url = "https://example.org/notifications"
                    }
                }
            }

        // then
        response.id shouldBe "request-123"
        response.result shouldBe
            TaskPushNotificationConfig(
                id = "task-123",
                pushNotificationConfig =
                    PushNotificationConfig(
                        url = "https://example.org/notifications",
                        token = null,
                    ),
            )
        response.error shouldBe null
        response.jsonrpc shouldBe "2.0"
    }
}
