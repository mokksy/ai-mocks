package dev.mokksy.test.utils

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.serialization.json.Json

/**
 * Deserializes the input JSON string into an object of type [T],
 * re-serializes the object back to a JSON string,
 * and verifies if the re-serialized JSON matches the original input.
 *
 * @param payload The JSON string to be deserialized and re-serialized.
 * @return The deserialized object of type [T].
 */
public inline fun <reified T : Any> deserializeAndSerialize(payload: String): T {
    val model: T = Json.decodeFromString(payload)

    model.shouldNotBeNull()

    val encoded = Json.encodeToString(model)

    encoded shouldEqualJson payload
    return model
}
