package dev.mokksy.aimocks.a2a.model.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

/**
 * Serializer that can handle values which can be either a primitive (String, Number, Boolean)
 * or a nested structure (Map, List) with arbitrary nesting levels.
 *
 * @param T The type parameter for the serializer (usually Any, Map<String, Any>, or a specific type)
 */
public open class MapOrPrimitiveSerializer<T : Any> : KSerializer<T> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MapOrPrimitive")

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(decoder: Decoder): T {
        val jsonDecoder =
            decoder as? JsonDecoder
                ?: throw SerializationException("Expected JSON decoder")
        val element = jsonDecoder.decodeJsonElement()

        return deserializeJsonElement(element) as T
    }

    private fun deserializeJsonElement(jsonElement: JsonElement): Any =
        when (jsonElement) {
            is JsonPrimitive -> deserializePrimitive(jsonElement)
            is JsonObject -> deserializeObject(jsonElement)
            is JsonArray -> deserializeArray(jsonElement)
        }

    private fun deserializePrimitive(jsonPrimitive: JsonPrimitive): Any =
        when {
            jsonPrimitive.isString -> jsonPrimitive.content
            jsonPrimitive.content == "true" -> true
            jsonPrimitive.content == "false" -> false
            jsonPrimitive.content.toIntOrNull() != null -> jsonPrimitive.content.toInt()
            jsonPrimitive.content.toLongOrNull() != null -> jsonPrimitive.content.toLong()
            jsonPrimitive.content.toDoubleOrNull() != null -> jsonPrimitive.content.toDouble()
            else -> jsonPrimitive.content
        }

    private fun deserializeObject(jsonObject: JsonObject): Map<String, Any> =
        jsonObject.mapValues { (_, value) ->
            deserializeJsonElement(value)
        }

    private fun deserializeArray(jsonArray: JsonArray): List<Any?> =
        jsonArray.map {
            if (it is JsonNull) null else deserializeJsonElement(it)
        }

    @Suppress("UNCHECKED_CAST")
    override fun serialize(
        encoder: Encoder,
        value: T,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: throw SerializationException("Expected JSON encoder")

        val jsonElement = serializeToJsonElement(value as Any?)
        jsonEncoder.encodeJsonElement(jsonElement)
    }

    private fun serializeToJsonElement(value: Any?): JsonElement =
        when (value) {
            null -> JsonNull
            is String -> JsonPrimitive(value)
            is Number -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                val map = value as Map<String, Any?>
                buildJsonObject {
                    map.forEach { (k, v) ->
                        put(k, serializeToJsonElement(v))
                    }
                }
            }

            is Collection<*> ->
                buildJsonArray {
                    value.forEach { item ->
                        add(serializeToJsonElement(item))
                    }
                }

            is Array<*> ->
                buildJsonArray {
                    value.forEach { item ->
                        add(serializeToJsonElement(item))
                    }
                }

            else -> JsonPrimitive(value.toString())
        }
}
