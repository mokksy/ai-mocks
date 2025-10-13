package dev.mokksy.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class PushNotificationConfigBuilderTest {
    @Test
    fun `should build PushNotificationConfig with minimal parameters`() {
        // when
        val config =
            PushNotificationConfig.create {
                url = "https://example.org/notifications"
            }

        // then
        config.url shouldBe "https://example.org/notifications"
        config.token shouldBe null
    }

    @Test
    fun `should build PushNotificationConfig with all parameters`() {
        // when
        val config =
            PushNotificationConfig.create {
                url = "https://example.org/notifications"
                token = "auth-token"
            }

        // then
        config.url shouldBe "https://example.org/notifications"
        config.token shouldBe "auth-token"
    }

    @Test
    fun `should throw exception when url is missing`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            PushNotificationConfigBuilder().build()
        }
    }
}
