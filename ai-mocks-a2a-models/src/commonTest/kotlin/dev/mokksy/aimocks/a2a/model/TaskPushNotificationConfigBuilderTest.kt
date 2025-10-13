package dev.mokksy.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class TaskPushNotificationConfigBuilderTest {
    @Test
    fun `should build TaskPushNotificationConfig with required parameters`() {
        // when
        val config =
            TaskPushNotificationConfigBuilder()
                .apply {
                    id = "task-123"
                    pushNotificationConfig {
                        url = "https://example.org/notifications"
                    }
                }.build()

        // then
        config.id shouldBe "task-123"
        config.pushNotificationConfig shouldBe
            PushNotificationConfig(
                url = "https://example.org/notifications",
                token = null,
            )
    }

    @Test
    fun `should build TaskPushNotificationConfig with all parameters`() {
        // when
        val config =
            TaskPushNotificationConfigBuilder()
                .apply {
                    id = "task-123"
                    pushNotificationConfig {
                        url = "https://example.org/notifications"
                        token = "auth-token"
                    }
                }.build()

        // then
        config.id shouldBe "task-123"
        config.pushNotificationConfig shouldBe
            PushNotificationConfig(
                url = "https://example.org/notifications",
                token = "auth-token",
            )
    }

    @Test
    fun `should throw exception when id is missing`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            TaskPushNotificationConfigBuilder()
                .apply {
                    pushNotificationConfig {
                        url = "https://example.org/notifications"
                    }
                }.build()
        }
    }

    @Test
    fun `should throw exception when pushNotificationConfig is missing`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            TaskPushNotificationConfigBuilder()
                .apply {
                    id = "task-123"
                }.build()
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // when
        val config =
            taskPushNotificationConfig {
                id = "task-123"
                pushNotificationConfig {
                    url = "https://example.org/notifications"
                    token = "auth-token"
                }
            }

        // then
        config.id shouldBe "task-123"
        config.pushNotificationConfig shouldBe
            PushNotificationConfig(
                url = "https://example.org/notifications",
                token = "auth-token",
            )
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val config =
            TaskPushNotificationConfig.create {
                id = "task-123"
                pushNotificationConfig {
                    url = "https://example.org/notifications"
                }
            }

        // then
        config.id shouldBe "task-123"
        config.pushNotificationConfig shouldBe
            PushNotificationConfig(
                url = "https://example.org/notifications",
                token = null,
            )
    }

    @Test
    fun `should build using companion object build function`() {
        // when
        val config =
            TaskPushNotificationConfig.create {
                id = "task-123"
                pushNotificationConfig {
                    url = "https://example.org/notifications"
                    token = "auth-token"
                }
            }

        // then
        config.id shouldBe "task-123"
        config.pushNotificationConfig shouldBe
            PushNotificationConfig(
                url = "https://example.org/notifications",
                token = "auth-token",
            )
    }
}
