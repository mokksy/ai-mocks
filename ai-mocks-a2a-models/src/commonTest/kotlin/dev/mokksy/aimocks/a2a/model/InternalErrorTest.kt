package dev.mokksy.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class InternalErrorTest {
    @Test
    fun `should create InternalError with default parameters`() {
        // when
        val error = internalError()

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
        val error = internalError(data = data)

        // then
        error.code shouldBe -32603
        error.message shouldBe "Internal error"
        error.data shouldBe data
        error.data?.get("reason") shouldBe "Something went wrong"
        error.data?.get("details") shouldBe "Error details"
    }
}
