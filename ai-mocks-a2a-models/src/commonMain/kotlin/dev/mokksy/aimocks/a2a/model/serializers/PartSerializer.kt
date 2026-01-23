package dev.mokksy.aimocks.a2a.model.serializers

import dev.mokksy.aimocks.a2a.model.DataPart
import dev.mokksy.aimocks.a2a.model.FilePart
import dev.mokksy.aimocks.a2a.model.Part
import dev.mokksy.aimocks.a2a.model.TextPart
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Custom polymorphic serializer for [Part] hierarchy.
 *
 * This serializer handles the polymorphic serialization of Part subclasses
 * by using the "kind" discriminator field to determine which concrete implementation
 * to use during deserialization.
 *
 * Supported Part types:
 * - "text" -> [TextPart]
 * - "file" -> [FilePart]
 * - "data" -> [DataPart]
 */
public class PartSerializer : KSerializer<Part> {
    // Serializers for the concrete implementations
    private val textPartSerializer = TextPart.serializer()
    private val filePartSerializer = FilePart.serializer()
    private val dataPartSerializer = DataPart.serializer()

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Part")

    @Suppress("ThrowsCount")
    override fun deserialize(decoder: Decoder): Part {
        // We need to work with JSON to inspect the "kind" property
        val jsonDecoder =
            decoder as? JsonDecoder
                ?: throw SerializationException("PartSerializer can only be used with JSON")

        val jsonElement = jsonDecoder.decodeJsonElement()
        if (jsonElement !is JsonObject) {
            throw SerializationException("Expected JSON object for Part")
        }

        // Get the "kind" discriminator value
        val kindElement =
            jsonElement["kind"]
                ?: throw SerializationException("Missing 'kind' discriminator in Part JSON")

        // Determine which subclass to use based on the "kind" value
        return when (val kind = kindElement.jsonPrimitive.content) {
            "text" -> jsonDecoder.json.decodeFromJsonElement(textPartSerializer, jsonElement)
            "file" -> jsonDecoder.json.decodeFromJsonElement(filePartSerializer, jsonElement)
            "data" -> jsonDecoder.json.decodeFromJsonElement(dataPartSerializer, jsonElement)
            else -> throw SerializationException("Unknown Part kind: '$kind'")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: Part,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: throw SerializationException("PartSerializer can only be used with JSON")

        // Delegate to the appropriate serializer based on the concrete type
        val jsonElement: JsonElement =
            when (value) {
                is TextPart -> jsonEncoder.json.encodeToJsonElement(textPartSerializer, value)
                is FilePart -> jsonEncoder.json.encodeToJsonElement(filePartSerializer, value)
                is DataPart -> jsonEncoder.json.encodeToJsonElement(dataPartSerializer, value)
            }

        jsonEncoder.encodeJsonElement(jsonElement)
    }
}
