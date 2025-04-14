package me.kpavlov.aimocks.a2a.model

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.serialization.json.Json

/**
 * https://github.com/google/A2A/blob/gh-pages/documentation.md#sample-methods-and-json-responses
 */
internal abstract class AbstractSerializationTest {
    protected inline fun <reified T : Any> deserializeAndSerialize(payload: String): T {
        val model: T = Json.decodeFromString(payload)

        model.shouldNotBeNull()

        val encoded = Json.encodeToString(model)

        encoded shouldEqualJson payload
        return model
    }
}
