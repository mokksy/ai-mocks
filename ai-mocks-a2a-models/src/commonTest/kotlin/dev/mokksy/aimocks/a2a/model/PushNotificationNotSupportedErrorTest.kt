package dev.mokksy.aimocks.a2a.model

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class PushNotificationNotSupportedErrorTest {
    @Test
    fun `should create with default parameters`() {
        // when
        val error = pushNotificationNotSupportedError()

        // then
        assertSoftly(error) {
            code shouldBe -32003
            message shouldBe "Push Notification is not supported"
            data shouldBe null
        }
    }

    @Test
    fun `should create with data`() {
        // given
        val expectedData =
            Data.of(
                "reason" to "Push notifications not configured",
                "details" to "Missing configuration",
            )

        // when
        val error =
            pushNotificationNotSupportedError(
                data = expectedData,
            )

        // then
        assertSoftly(error) {
            code shouldBe -32003
            message shouldBe "Push Notification is not supported"
            data shouldBe expectedData
        }
    }
}
