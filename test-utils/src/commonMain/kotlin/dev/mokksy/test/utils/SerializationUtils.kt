package dev.mokksy.test.utils

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.serialization.json.Json

/**
 * Deserializes the input JSON string into an object of type [T],
 * re-serializes the object back to a JSON string,
 * and verifies if the re-serialized JSON matches the original input.
 *
 * @param payload The JSON string to be deserialized and re-serialized.
 * @param jsonParser The [Json] instance used for serialization and deserialization.
 * Defaults to the standard [Json] parser.
 * @return The deserialized object of type [T].
 */
public inline fun <reified T : Any> deserializeAndSerialize(
    payload: String,
    jsonParser: Json = Json,
): T {
    val model: T = jsonParser.decodeFromString(payload)

    model.shouldNotBeNull()

    val encoded = jsonParser.encodeToString(model)

    encoded shouldEqualJson payload
    return model
}

/**
 * Serializes the specified value to a JSON string and deserializes it back,
 * ensuring the serialized JSON matches the expected payload,
 * and the final deserialized object matches the original value.
 *
 * @param value The object to be serialized and deserialized.
 * @param expectedPayload The expected serialized JSON string that should match the serialization result.
 * @param jsonParser The [Json] instance used for serialization and deserialization.
 * Defaults to the standard [Json] parser.
 * @return The deserialized object, which should be equal to the original input value.
 */
public inline fun <reified T : Any> serializeAndDeserialize(
    value: T,
    expectedPayload: String,
    jsonParser: Json = Json,
): T {
    val encoded: String = jsonParser.encodeToString(value)

    encoded shouldEqualJson expectedPayload

    val decoded = jsonParser.decodeFromString<T>(encoded)

    decoded shouldBeEqual value
    return decoded
}
