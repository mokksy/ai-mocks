package me.kpavlov.aimocks.a2a.model.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import me.kpavlov.aimocks.a2a.model.RequestId

/**
 * Serializer that can handle ID values of type String, Int, or null
 */
public class RequestIdSerializer : KSerializer<RequestId?> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RequestId")

    override fun deserialize(decoder: Decoder): RequestId? {
        val jsonDecoder =
            decoder as? JsonDecoder ?: throw SerializationException("Expected JSON decoder")
        val element = jsonDecoder.decodeJsonElement()

        return when {
            element is JsonNull -> null
            element is JsonPrimitive -> {
                // Try to convert to Int first, if not possible, use as String
                element.intOrNull ?: element.content
            }

            else -> throw SerializationException("Unexpected JSON element type: $element")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: RequestId?,
    ) {
        val jsonEncoder =
            encoder as? kotlinx.serialization.json.JsonEncoder
                ?: throw SerializationException("Expected JSON encoder")

        val jsonElement =
            when (value) {
                null -> JsonNull
                is Int -> JsonPrimitive(value)
                is String -> JsonPrimitive(value)
                else -> throw SerializationException("Unsupported type for ID: ${value::class}")
            }

        jsonEncoder.encodeJsonElement(jsonElement)
    }
}
