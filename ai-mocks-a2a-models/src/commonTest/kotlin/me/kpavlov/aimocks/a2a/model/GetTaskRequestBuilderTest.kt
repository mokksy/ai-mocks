package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class GetTaskRequestBuilderTest {
    @Test
    fun `should build GetTaskRequest with required parameters`() {
        // when
        val request = GetTaskRequestBuilder()
            .params {
                id("task-123")
            }
            .build()

        // then
        request.id shouldBe null
        request.params shouldBe TaskQueryParams(
            id = "task-123",
            historyLength = null,
            metadata = null
        )
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/get"
    }

    @Test
    fun `should build GetTaskRequest with all parameters`() {
        // when
        val request = GetTaskRequestBuilder()
            .id("request-123")
            .params {
                id("task-123")
                historyLength(10)
                metadata(Metadata.of("metaKey" to "metaValue"))
            }
            .build()

        // then
        request.id shouldBe "request-123"
        request.params shouldBe TaskQueryParams(
            id = "task-123",
            historyLength = 10,
            metadata = Metadata.of("metaKey" to "metaValue")
        )
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/get"
    }

    @Test
    fun `should fail when params is not provided`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            GetTaskRequestBuilder().build()
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // when
        val request = getTaskRequest {
            id("request-123")
            params {
                id("task-123")
                historyLength(10)
            }
        }

        // then
        request.id shouldBe "request-123"
        request.params shouldBe TaskQueryParams(
            id = "task-123",
            historyLength = 10,
            metadata = null
        )
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/get"
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val request = GetTaskRequest.create {
            id("request-123")
            params {
                id("task-123")
                historyLength(10)
            }
        }

        // then
        request.id shouldBe "request-123"
        request.params shouldBe TaskQueryParams(
            id = "task-123",
            historyLength = 10,
            metadata = null
        )
        request.jsonrpc shouldBe "2.0"
        request.method shouldBe "tasks/get"
    }
}
