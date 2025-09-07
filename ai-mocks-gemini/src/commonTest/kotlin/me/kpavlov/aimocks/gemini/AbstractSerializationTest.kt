package me.kpavlov.aimocks.gemini

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.serialization.json.Json

/**
 * https://a2a-protocol.org/latest/specification/
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
