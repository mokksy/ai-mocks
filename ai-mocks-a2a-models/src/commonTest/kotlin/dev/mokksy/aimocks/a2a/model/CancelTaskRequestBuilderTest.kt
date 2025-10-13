package dev.mokksy.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class CancelTaskRequestBuilderTest {
    @Test
    fun `should build CancelTaskRequest with required parameters`() {
        // when
        val request =
            CancelTaskRequestBuilder()
                .apply {
                    params {
                        id = "task-123"
                    }
                }.build()

        // then
        request.id shouldBe null
        request.params shouldBe
            TaskIdParams(
                id = "task-123",
                metadata = null,
            )
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/cancel"
    }

    @Test
    fun `should build CancelTaskRequest with all parameters`() {
        // when
        val request =
            CancelTaskRequestBuilder()
                .apply {
                    id = "request-123"
                    params {
                        id = "task-123"
                        metadata = Metadata.empty()
                    }
                }.build()

        // then
        request.id shouldBe "request-123"
        request.params shouldBe
            TaskIdParams(
                id = "task-123",
                metadata = Metadata.empty(),
            )
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/cancel"
    }

    @Test
    fun `should throw exception when params is missing`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            CancelTaskRequestBuilder()
                .apply {
                    id = "request-123"
                }.build()
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // when
        val request =
            cancelTaskRequest {
                id = "request-123"
                params {
                    id = "task-123"
                }
            }

        // then
        request.id shouldBe "request-123"
        request.params shouldBe
            TaskIdParams(
                id = "task-123",
                metadata = null,
            )
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/cancel"
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val request =
            CancelTaskRequest.create {
                id = "request-123"
                params {
                    id = "task-123"
                    metadata = Metadata.empty()
                }
            }

        // then
        request.id shouldBe "request-123"
        request.params shouldBe
            TaskIdParams(
                id = "task-123",
                metadata = Metadata.empty(),
            )
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/cancel"
    }
}
