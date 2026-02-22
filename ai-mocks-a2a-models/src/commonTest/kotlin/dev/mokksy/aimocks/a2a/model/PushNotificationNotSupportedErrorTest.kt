package dev.mokksy.aimocks.a2a.model

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class PushNotificationNotSupportedErrorTest {
    @Test
    fun `should create with default parameters`() {
        // when
        val error = PushNotificationNotSupportedError()

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
        val error = PushNotificationNotSupportedError(expectedData)

        // then
        assertSoftly(error) {
            code shouldBe -32003
            message shouldBe "Push Notification is not supported"
            data shouldBe expectedData
        }
    }

    @Test
    fun `should copy with new data`() {
        // given
        val error = PushNotificationNotSupportedError()
        val newData = Data.of("reason" to "New reason", "details" to "New details")

        // when
        val copiedError = error.copy(data = newData)

        // then
        assertSoftly(copiedError) {
            this shouldBe PushNotificationNotSupportedError(newData)
            code shouldBe -32003
            message shouldBe "Push Notification is not supported"
            data shouldNotBeNull {
                this shouldBe newData
                get("reason") shouldBe "New reason"
                get("details") shouldBe "New details"
            }
        }
    }

    @Test
    fun `should copy with null data when not specified`() {
        // given
        val error = PushNotificationNotSupportedError()

        // when
        val copiedError = error.copy()

        // then
        assertSoftly(copiedError) {
            code shouldBe -32003
            message shouldBe "Push Notification is not supported"
            data shouldBe null
        }
    }
}
