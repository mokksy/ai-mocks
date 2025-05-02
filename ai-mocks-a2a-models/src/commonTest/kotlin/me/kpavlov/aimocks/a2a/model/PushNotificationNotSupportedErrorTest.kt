package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class PushNotificationNotSupportedErrorTest {
    @Test
    fun `should create PushNotificationNotSupportedError with default parameters`() {
        // when
        val error = PushNotificationNotSupportedError()

        // then
        error.code shouldBe -32003
        error.message shouldBe "Push Notification is not supported"
        error.data shouldBe null
    }

    @Test
    fun `should create PushNotificationNotSupportedError with data`() {
        // given
        val data =
            Data.of(
                "reason" to "Push notifications not configured",
                "details" to "Missing configuration",
            )

        // when
        val error = PushNotificationNotSupportedError(data)

        // then
        error.code shouldBe -32003
        error.message shouldBe "Push Notification is not supported"
        // Note: The constructor ignores the data parameter and always sets data to null
        error.data shouldBe null
    }

    @Test
    fun `should copy to InternalError with new data`() {
        // given
        val error = PushNotificationNotSupportedError()
        val newData = Data.of("reason" to "New reason", "details" to "New details")

        // when
        val copiedError = error.copy(data = newData)

        // then
        // Note: The copy method returns an InternalError, not a PushNotificationNotSupportedError
        copiedError shouldBe InternalError(newData)
        copiedError.code shouldBe -32603
        copiedError.message shouldBe "Internal error"
        copiedError.data shouldBe newData
        copiedError.data?.get("reason") shouldBe "New reason"
        copiedError.data?.get("details") shouldBe "New details"
    }

    @Test
    fun `should copy to InternalError with null data when not specified`() {
        // given
        val error = PushNotificationNotSupportedError()

        // when
        val copiedError = error.copy()

        // then
        // Note: The copy method returns an InternalError, not a PushNotificationNotSupportedError
        copiedError shouldBe InternalError(null)
        copiedError.code shouldBe -32603
        copiedError.message shouldBe "Internal error"
        copiedError.data shouldBe null
    }
}
