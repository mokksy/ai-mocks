package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class DataPartBuilderTest {
    @Test
    fun `should build DataPart with required parameters`() {
        // when
        val dataPart =
            DataPartBuilder()
                .put("key1", "value1")
                .put("key2", 42)
                .build()

        // then
        dataPart.data.asMap() shouldBe mapOf("key1" to "value1", "key2" to 42)
        dataPart.metadata shouldBe null
    }

    @Test
    fun `should build DataPart with all parameters`() {
        // when
        val metadata = Metadata.of("metaKey" to "metaValue")
        val dataPart =
            DataPartBuilder()
                .put("key1", "value1")
                .put("key2", 42)
                .metadata(metadata)
                .build()

        // then
        dataPart.data.asMap() shouldBe mapOf("key1" to "value1", "key2" to 42)
        dataPart.metadata shouldBe metadata
    }

    @Test
    fun `should add multiple entries using putAll`() {
        // when
        val dataMap = mapOf("key1" to "value1", "key2" to 42)
        val dataPart =
            DataPartBuilder()
                .putAll(dataMap)
                .build()

        // then
        dataPart.data.asMap() shouldBe dataMap
    }

    @Test
    fun `should fail validation when data is empty`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            DataPartBuilder().build(validate = true)
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // when
        val dataPart =
            dataPart {
                put("key1", "value1")
                put("key2", 42)
            }

        // then
        dataPart.data.asMap() shouldBe mapOf("key1" to "value1", "key2" to 42)
        dataPart.metadata shouldBe null
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val dataPart =
            DataPart.create {
                put("key1", "value1")
                put("key2", 42)
            }

        // then
        dataPart.data.asMap() shouldBe mapOf("key1" to "value1", "key2" to 42)
        dataPart.metadata shouldBe null
    }
}
