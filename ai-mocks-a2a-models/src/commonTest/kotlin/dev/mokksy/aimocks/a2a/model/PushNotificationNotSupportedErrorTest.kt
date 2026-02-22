package dev.mokksy.aimocks.a2a.model

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class PushNotificationNotSupportedErrorTest {
    @Test
    fun `should create with default parameters`() {
        // when
        val error = PushNotificationNotSupportedError()

        // then
        error.code shouldBe -32003
        error.message shouldBe "Push Notification is not supported"
        error.data shouldBe null
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
        copiedError shouldBe PushNotificationNotSupportedError(newData)
        copiedError.code shouldBe -32003
        copiedError.message shouldBe "Push Notification is not supported"
        copiedError.data shouldBe newData
        copiedError.data?.get("reason") shouldBe "New reason"
        copiedError.data?.get("details") shouldBe "New details"
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
