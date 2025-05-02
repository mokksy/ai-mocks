package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class CancelTaskResponseBuilderTest {
    @Test
    fun `should build CancelTaskResponse with id and result`() {
        // when
        val response =
            CancelTaskResponse.create {
                id = "request-123"
                result {
                    id = "task-123"
                    status {
                        state = "canceled"
                    }
                }
            }

        // then
        response.id shouldBe "request-123"
        response.result shouldBe
            Task(
                id = "task-123",
                status = TaskStatus(state = "canceled"),
                artifacts = emptyList(),
            )
        response.error shouldBe null
        response.jsonrpc shouldBe "2.0"
    }

    @Test
    fun `should build CancelTaskResponse with id and error`() {
        // when
        val error =
            JSONRPCError(
                code = 123,
                message = "Error message",
            )
        val response =
            CancelTaskResponse.create {
                id = "request-123"
                error(error)
            }

        // then
        response.id shouldBe "request-123"
        response.result shouldBe null
        response.error shouldBe error
        response.jsonrpc shouldBe "2.0"
    }

    @Test
    fun `should build CancelTaskResponse with all parameters`() {
        // when
        val task =
            Task(
                id = "task-123",
                status = TaskStatus(state = "canceled"),
            )
        val error =
            JSONRPCError(
                code = 123,
                message = "Error message",
            )
        val response =
            CancelTaskResponse.create {
                id = "request-123"
                result(task)
                error(error)
            }

        // then
        response.id shouldBe "request-123"
        response.result shouldBe task
        response.error shouldBe error
        response.jsonrpc shouldBe "2.0"
    }

    @Test
    fun `should build using top-level DSL function`() {
        // when
        val response =
            cancelTaskResponse {
                id = "request-123"
                result {
                    id = "task-123"
                    status {
                        state = "canceled"
                    }
                }
            }

        // then
        response.id shouldBe "request-123"
        response.result shouldBe
            Task(
                id = "task-123",
                status = TaskStatus(state = "canceled"),
                artifacts = emptyList(),
            )
        response.error shouldBe null
        response.jsonrpc shouldBe "2.0"
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val response =
            CancelTaskResponse.create {
                id = "request-123"
                result {
                    id = "task-123"
                    status {
                        state = "canceled"
                    }
                }
            }

        // then
        response.id shouldBe "request-123"
        response.result shouldBe
            Task(
                id = "task-123",
                status = TaskStatus(state = "canceled"),
                artifacts = emptyList(),
            )
        response.error shouldBe null
        response.jsonrpc shouldBe "2.0"
    }
}
