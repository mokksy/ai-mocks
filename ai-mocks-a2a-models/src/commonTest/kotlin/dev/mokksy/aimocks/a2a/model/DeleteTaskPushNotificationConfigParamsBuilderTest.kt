package dev.mokksy.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class DeleteTaskPushNotificationConfigParamsBuilderTest {
    @Test
    fun `should build DeleteTaskPushNotificationConfigParams with required parameters`() {
        // when
        val builder = DeleteTaskPushNotificationConfigParamsBuilder()
        builder.id = "task-123"
        val params = builder.build()

        // then
        params.id shouldBe "task-123"
        params.metadata shouldBe null
    }

    @Test
    fun `should build DeleteTaskPushNotificationConfigParams with all parameters`() {
        // given
        val metadata = mapOf<String, JsonElement>(
            "key1" to JsonPrimitive("value1"),
            "key2" to JsonPrimitive(42),
        )

        // when
        val builder = DeleteTaskPushNotificationConfigParamsBuilder()
        builder.id = "task-123"
        builder.metadata = metadata
        val params = builder.build()

        // then
        params.id shouldBe "task-123"
        params.metadata shouldBe metadata
        params.metadata?.get("key1") shouldBe JsonPrimitive("value1")
        params.metadata?.get("key2") shouldBe JsonPrimitive(42)
    }

    @Test
    fun `should fail when id is not provided`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            DeleteTaskPushNotificationConfigParamsBuilder().build()
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // given
        val metadata = mapOf<String, JsonElement>(
            "key1" to JsonPrimitive("value1"),
            "key2" to JsonPrimitive(42),
        )

        // when
        val params =
            deleteTaskPushNotificationConfigParams {
                id = "task-123"
                this.metadata = metadata
            }

        // then
        params.id shouldBe "task-123"
        params.metadata shouldBe metadata
    }

    @Test
    fun `should build with method chaining`() {
        // given
        val metadata = mapOf<String, JsonElement>(
            "key1" to JsonPrimitive("value1"),
        )

        // when
        val params =
            DeleteTaskPushNotificationConfigParamsBuilder()
                .id("task-456")
                .metadata(metadata)
                .build()

        // then
        params.id shouldBe "task-456"
        params.metadata shouldBe metadata
    }

    @Test
    fun `should build using Java-friendly Consumer factory method`() {
        // given
        val metadata = mapOf<String, JsonElement>(
            "key1" to JsonPrimitive("value1"),
            "key2" to JsonPrimitive(42),
        )

        // when
        val params =
            deleteTaskPushNotificationConfigParams { builder ->
                builder.id = "task-789"
                builder.metadata = metadata
            }

        // then
        params.id shouldBe "task-789"
        params.metadata shouldBe metadata
    }
}
