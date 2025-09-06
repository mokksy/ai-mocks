package me.kpavlov.aimocks.ollama.model

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/**
 * Base class for serialization tests.
 *
 * Provides a utility methods for testing serialization and deserialization.
 */
internal abstract class AbstractSerializationTest {
    /**
     * Deserializes a JSON string into a model object, then serializes it back to JSON
     * and verifies that the resulting JSON matches the original.
     *
     * @param payload The JSON string to deserialize
     * @return The deserialized model object
     */
    protected inline fun <reified T : Any> deserializeAndSerialize(payload: String): T {
        val json =
            Json {
                ignoreUnknownKeys = false
            }
        val model: T = json.decodeFromString(payload)

        model.shouldNotBeNull()

        val encoded = json.encodeToString(serializer<T>(), model)

        encoded shouldEqualJson payload
        return model
    }
}
