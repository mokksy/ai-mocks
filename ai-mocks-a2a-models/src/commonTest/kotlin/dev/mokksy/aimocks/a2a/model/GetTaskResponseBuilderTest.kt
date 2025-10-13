package dev.mokksy.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GetTaskResponseBuilderTest {
    @Test
    fun `should build GetTaskResponse with minimal properties`() {
        // when
        val response = GetTaskResponseBuilder().build()

        // then
        response.id shouldBe null
        response.result shouldBe null
        response.error shouldBe null
        response.jsonrpc shouldBe "2.0"
    }

    @Test
    fun `should build GetTaskResponse with id and result`() {
        // given
        val task =
            Task(
                id = "task-123",
                contextId = "ctx-123",
                status = TaskStatus(state = "completed"),
            )

        // when
        val response =
            GetTaskResponseBuilder()
                .id("request-123")
                .result(task)
                .build()

        // then
        response.id shouldBe "request-123"
        response.result shouldBe task
        response.error shouldBe null
        response.jsonrpc shouldBe "2.0"
    }

    @Test
    fun `should build GetTaskResponse with id and error`() {
        // given
        val error =
            JSONRPCError(
                code = 123,
                message = "Error message",
            )

        // when
        val response =
            GetTaskResponseBuilder()
                .id("request-123")
                .error(error)
                .build()

        // then
        response.id shouldBe "request-123"
        response.result shouldBe null
        response.error shouldBe error
        response.jsonrpc shouldBe "2.0"
    }

    @Test
    fun `should build GetTaskResponse with all properties`() {
        // given
        val task =
            Task(
                id = "task-123",
                contextId = "ctx-123",
                status = TaskStatus(state = "completed"),
            )
        val error =
            JSONRPCError(
                code = 123,
                message = "Error message",
            )

        // when
        val response =
            GetTaskResponseBuilder()
                .id("request-123")
                .result(task)
                .error(error)
                .build()

        // then
        response.id shouldBe "request-123"
        response.result shouldBe task
        response.error shouldBe error
        response.jsonrpc shouldBe "2.0"
    }

    @Test
    fun `should build GetTaskResponse using result DSL block`() {
        // when
        val response =
            GetTaskResponseBuilder()
                .id("request-123")
                .result {
                    id = "task-123"
                    contextId = "ctx-123"
                    status {
                        state = "completed"
                    }
                }.build()

        // then
        response.id shouldBe "request-123"
        response.result shouldBe
            Task(
                id = "task-123",
                contextId = "ctx-123",
                status = TaskStatus(state = "completed"),
            )
        response.error shouldBe null
        response.jsonrpc shouldBe "2.0"
    }

    @Test
    fun `should build using top-level DSL function`() {
        // when
        val response =
            getTaskResponse {
                id("request-123")
                result {
                    id = "task-123"
                    contextId = "ctx-123"
                    status {
                        state = "completed"
                    }
                }
            }

        // then
        response.id shouldBe "request-123"
        response.result shouldBe
            Task(
                id = "task-123",
                contextId = "ctx-123",
                status = TaskStatus(state = "completed"),
            )
        response.error shouldBe null
        response.jsonrpc shouldBe "2.0"
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val response =
            GetTaskResponse.create {
                id("request-123")
                result {
                    id = "task-123"
                    contextId = "ctx-123"
                    status {
                        state = "completed"
                    }
                }
            }

        // then
        response.id shouldBe "request-123"
        response.result shouldBe
            Task(
                id = "task-123",
                contextId = "ctx-123",
                status = TaskStatus(state = "completed"),
            )
        response.error shouldBe null
        response.jsonrpc shouldBe "2.0"
    }
}
