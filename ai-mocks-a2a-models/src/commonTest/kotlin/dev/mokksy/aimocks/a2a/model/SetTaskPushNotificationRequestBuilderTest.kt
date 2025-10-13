package dev.mokksy.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class SetTaskPushNotificationRequestBuilderTest {
    @Test
    fun `should build SetTaskPushNotificationRequest with required parameters`() {
        // when
        val request =
            SetTaskPushNotificationRequestBuilder()
                .apply {
                    params {
                        id = "task-123"
                        pushNotificationConfig {
                            url = "https://example.org/notifications"
                        }
                    }
                }.build()

        // then
        request.id shouldBe null
        request.params shouldBe
            TaskPushNotificationConfig(
                id = "task-123",
                pushNotificationConfig =
                    PushNotificationConfig(
                        url = "https://example.org/notifications",
                        token = null,
                    ),
            )
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/pushNotificationConfig/set"
    }

    @Test
    fun `should build SetTaskPushNotificationRequest with all parameters`() {
        // when
        val request =
            SetTaskPushNotificationRequestBuilder()
                .apply {
                    id = "request-123"
                    params {
                        id = "task-123"
                        pushNotificationConfig {
                            url = "https://example.org/notifications"
                            token = "auth-token"
                        }
                    }
                }.build()

        // then
        request.id shouldBe "request-123"
        request.params shouldBe
            TaskPushNotificationConfig(
                id = "task-123",
                pushNotificationConfig =
                    PushNotificationConfig(
                        url = "https://example.org/notifications",
                        token = "auth-token",
                    ),
            )
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/pushNotificationConfig/set"
    }

    @Test
    fun `should throw exception when params is missing`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            SetTaskPushNotificationRequestBuilder()
                .apply {
                    id = "request-123"
                }.build()
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // when
        val request =
            setTaskPushNotificationRequest {
                id = "request-123"
                params {
                    id = "task-123"
                    pushNotificationConfig {
                        url = "https://example.org/notifications"
                        token = "auth-token"
                    }
                }
            }

        // then
        request.id shouldBe "request-123"
        request.params shouldBe
            TaskPushNotificationConfig(
                id = "task-123",
                pushNotificationConfig =
                    PushNotificationConfig(
                        url = "https://example.org/notifications",
                        token = "auth-token",
                    ),
            )
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/pushNotificationConfig/set"
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val request =
            SetTaskPushNotificationRequest.create {
                id = "request-123"
                params {
                    id = "task-123"
                    pushNotificationConfig {
                        url = "https://example.org/notifications"
                    }
                }
            }

        // then
        request.id shouldBe "request-123"
        request.params shouldBe
            TaskPushNotificationConfig(
                id = "task-123",
                pushNotificationConfig =
                    PushNotificationConfig(
                        url = "https://example.org/notifications",
                        token = null,
                    ),
            )
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/pushNotificationConfig/set"
    }
}
