package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class InternalErrorTest {
    @Test
    fun `should create InternalError with default parameters`() {
        // when
        val error = InternalError()

        // then
        error.code shouldBe -32603
        error.message shouldBe "Internal error"
        error.data shouldBe null
    }

    @Test
    fun `should create InternalError with data`() {
        // given
        val data = Data.of("reason" to "Something went wrong", "details" to "Error details")

        // when
        val error = InternalError(data)

        // then
        error.code shouldBe -32603
        error.message shouldBe "Internal error"
        error.data shouldBe data
        error.data?.get("reason") shouldBe "Something went wrong"
        error.data?.get("details") shouldBe "Error details"
    }

    @Test
    fun `should copy InternalError with new data`() {
        // given
        val originalData = Data.of("reason" to "Original reason")
        val error = InternalError(originalData)
        val newData = Data.of("reason" to "New reason", "details" to "New details")

        // when
        val copiedError = error.copy(data = newData)

        // then
        copiedError.code shouldBe -32603
        copiedError.message shouldBe "Internal error"
        copiedError.data shouldBe newData
        copiedError.data?.get("reason") shouldBe "New reason"
        copiedError.data?.get("details") shouldBe "New details"
    }

    @Test
    fun `should copy InternalError with same data when not specified`() {
        // given
        val data = Data.of("reason" to "Original reason")
        val error = InternalError(data)

        // when
        val copiedError = error.copy()

        // then
        copiedError.code shouldBe -32603
        copiedError.message shouldBe "Internal error"
        copiedError.data shouldBe data
        copiedError.data?.get("reason") shouldBe "Original reason"
    }
}
