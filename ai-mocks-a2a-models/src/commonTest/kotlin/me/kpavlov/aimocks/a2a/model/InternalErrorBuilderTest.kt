package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class InternalErrorBuilderTest {
    @Test
    fun `should build InternalError with default parameters`() {
        // when
        val error = InternalErrorBuilder().build()

        // then
        error.code shouldBe -32603
        error.message shouldBe "Internal error"
        error.data shouldBe null
    }

    @Test
    fun `should build InternalError with data parameter`() {
        // given
        val data = Data.of("key1" to "value1", "key2" to 42)

        // when
        val error =
            InternalErrorBuilder()
                .data(data)
                .build()

        // then
        error.code shouldBe -32603
        error.message shouldBe "Internal error"
        error.data shouldBe data
        error.data?.get("key1") shouldBe "value1"
        error.data?.get("key2") shouldBe 42
    }

    @Test
    fun `should build using top-level DSL function`() {
        // given
        val data = Data.of("key1" to "value1", "key2" to 42)

        // when
        val error =
            internalError {
                data(data)
            }

        // then
        error.code shouldBe -32603
        error.message shouldBe "Internal error"
        error.data shouldBe data
    }

    @Test
    fun `should build using companion object create function`() {
        // given
        val data = Data.of("key1" to "value1", "key2" to 42)

        // when
        val error =
            InternalError.create {
                data(data)
            }

        // then
        error.code shouldBe -32603
        error.message shouldBe "Internal error"
        error.data shouldBe data
    }
}
