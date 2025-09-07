package me.kpavlov.mokksy.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

public class StringOrListSerializer : KSerializer<List<String>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("StringOrList")

    override fun deserialize(decoder: Decoder): List<String> {
        val jsonDecoder =
            decoder as? JsonDecoder
                ?: throw SerializationException("This serializer can only be used with JSON")

        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> listOf(element.contentOrNull ?: "")
            is JsonArray ->
                element.map {
                    (it as? JsonPrimitive)?.contentOrNull ?: ""
                }

            else -> throw SerializationException("Expected string or array of strings")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: List<String>,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: throw SerializationException("This serializer can only be used with JSON")

        // If there's only one element, serialize as a string
        // Otherwise serialize as an array
        val element =
            if (value.size == 1) {
                JsonPrimitive(value.first())
            } else {
                JsonArray(value.map { JsonPrimitive(it) })
            }

        jsonEncoder.encodeJsonElement(element)
    }
}
