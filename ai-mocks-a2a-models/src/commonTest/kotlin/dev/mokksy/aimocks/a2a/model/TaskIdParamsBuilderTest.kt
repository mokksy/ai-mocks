package dev.mokksy.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class TaskIdParamsBuilderTest {
    @Test
    fun `should build TaskIdParams with required parameters`() {
        // when
        val builder = TaskIdParamsBuilder()
        builder.id = "task-123"
        val params = builder.build()

        // then
        params.id shouldBe "task-123"
        params.metadata shouldBe null
    }

    @Test
    fun `should build TaskIdParams with all parameters`() {
        // given
        val metadata = Metadata.of("key1" to "value1", "key2" to 42)

        // when
        val builder = TaskIdParamsBuilder()
        builder.id = "task-123"
        builder.metadata = metadata
        val params = builder.build()

        // then
        params.id shouldBe "task-123"
        params.metadata shouldBe metadata
        params.metadata?.get("key1") shouldBe "value1"
        params.metadata?.get("key2") shouldBe 42
    }

    @Test
    fun `should fail when id is not provided`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            TaskIdParamsBuilder().build()
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // given
        val metadata = Metadata.of("key1" to "value1", "key2" to 42)

        // when
        val params =
            taskIdParams {
                id = "task-123"
                this.metadata = metadata
            }

        // then
        params.id shouldBe "task-123"
        params.metadata shouldBe metadata
    }

    @Test
    fun `should build using companion object create function`() {
        // given
        val metadata = Metadata.of("key1" to "value1", "key2" to 42)

        // when
        val params =
            TaskIdParams.create {
                id = "task-123"
                this.metadata = metadata
            }

        // then
        params.id shouldBe "task-123"
        params.metadata shouldBe metadata
    }
}
