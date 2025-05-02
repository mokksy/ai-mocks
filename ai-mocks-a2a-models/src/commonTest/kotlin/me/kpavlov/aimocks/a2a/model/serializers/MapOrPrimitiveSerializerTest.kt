package me.kpavlov.aimocks.a2a.model.serializers

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainIgnoringCase
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.fail

class MapOrPrimitiveSerializerTest {
    private val serializer = MapOrPrimitiveSerializer<Any>()
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `should deserialize a JSON string to a string`() {
        val input = "\"hello\""
        val result = json.decodeFromString(serializer, input)
        result shouldBe "hello"
    }

    @Test
    fun `should deserialize a JSON number to an integer`() {
        val input = "42"
        val result = json.decodeFromString(serializer, input)
        result shouldBe 42
    }

    @Test
    fun `should deserialize a JSON number to a double`() {
        val input = "42.5"
        val result = json.decodeFromString(serializer, input)
        result shouldBe 42.5
    }

    @Test
    fun `should deserialize a JSON boolean true`() {
        val input = "true"
        val result = json.decodeFromString(serializer, input)
        result shouldBe true
    }

    @Test
    fun `should deserialize a JSON boolean false`() {
        val input = "false"
        val result = json.decodeFromString(serializer, input)
        result shouldBe false
    }

    @Test
    fun `should deserialize a JSON array to a List`() {
        val input = "[1, \"string\", true, null]"
        val result = json.decodeFromString(serializer, input)
        result shouldBe listOf(1, "string", true, null)
    }

    @Test
    fun `should deserialize a JSON object to a Map`() {
        val input = """{"key1": "value1", "key2": 42, "key3": true}"""
        val result = json.decodeFromString(serializer, input)
        result shouldBe mapOf("key1" to "value1", "key2" to 42, "key3" to true)
    }

    @Test
    fun `should deserialize a nested JSON structure`() {
        val input = """{"key1": {"nestedKey": "nestedValue"}, "key2": [1, 2, 3]}"""
        val result = json.decodeFromString(serializer, input)
        result shouldBe
            mapOf(
                "key1" to mapOf("nestedKey" to "nestedValue"),
                "key2" to listOf(1, 2, 3),
            )
    }

    @Test
    fun `should throw SerializationException for non-JSON input`() {
        val input = ":"
        try {
            json.decodeFromString(serializer, input)
            fail("Expected SerializationException")
        } catch (e: SerializationException) {
            e.message shouldContainIgnoringCase "Unexpected"
        }
    }

    @Test
    fun `should serialize a string to JSON`() {
        val value = "hello"
        val result = json.encodeToString(serializer, value)
        result shouldBe "\"hello\""
    }

    @Test
    fun `should serialize an integer to JSON`() {
        val value = 10
        val result = json.encodeToString(serializer, value)
        result shouldBe "10"
    }

    @Test
    fun `should serialize a boolean to JSON`() {
        val value = true
        val result = json.encodeToString(serializer, value)
        result shouldBe "true"
    }

    @Test
    fun `should serialize a list to JSON`() {
        val value = listOf(1, "string", false, null)
        val result = json.encodeToString(serializer, value)
        result shouldBe "[1,\"string\",false,null]"
    }

    @Test
    fun `should serialize a map to JSON`() {
        val value = mapOf("key1" to "value1", "key2" to 123)
        val result = json.encodeToString(serializer, value)
        result shouldBe """{"key1":"value1","key2":123}"""
    }

    @Test
    fun `should serialize a nested structure to JSON`() {
        val value =
            mapOf(
                "nestedMap" to mapOf("nestedKey" to 42),
                "nestedList" to listOf("a", "b", "c"),
            )
        val result = json.encodeToString(serializer, value)
        result shouldBe """{"nestedMap":{"nestedKey":42},"nestedList":["a","b","c"]}"""
    }
}
