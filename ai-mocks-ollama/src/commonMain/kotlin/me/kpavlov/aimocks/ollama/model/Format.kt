package me.kpavlov.aimocks.ollama.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import me.kpavlov.aimocks.core.json.schema.JsonSchema

/**
 * Represents the format field in the GenerateRequest.
 * This can be either a string "json" or a JSON schema object.
 */
@Serializable(with = FormatSerializer::class)
public sealed interface Format {
    /**
     * Represents the "json" format type.
     */
    @Serializable
    public object Json : Format {
        override fun toString(): String = "json"
    }

    /**
     * Represents a JSON schema format type.
     *
     * @property schema The JSON schema definition
     */
    @Serializable
    public data class Schema(val schema: JsonSchema) : Format
}

/**
 * Custom serializer for [Format] that can handle either a string "json" or a JSON schema object.
 */
internal class FormatSerializer : KSerializer<Format> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Format") {
        // Polymorphic format: either "json" string or JSON schema object
    }

    override fun deserialize(decoder: Decoder): Format {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("FormatSerializer requires JsonDecoder")

        val element = jsonDecoder.decodeJsonElement()

        return when {
            element is JsonPrimitive && element.jsonPrimitive.content == "json" -> Format.Json
            element is JsonObject -> {
                // Parse the JSON object into a JsonSchema
                val jsonSchema =
                    jsonDecoder.json.decodeFromJsonElement(JsonSchema.serializer(), element)
                Format.Schema(jsonSchema)
            }

            else -> throw SerializationException("Expected 'json' string or a JSON schema object, but got: $element")
        }
    }

    override fun serialize(encoder: Encoder, value: Format) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("FormatSerializer requires JsonEncoder")

        when (value) {
            is Format.Json -> {
                jsonEncoder.encodeJsonElement(JsonPrimitive("json"))
            }

            is Format.Schema -> {
                val jsonElement =
                    jsonEncoder.json.encodeToJsonElement(JsonSchema.serializer(), value.schema)
                jsonEncoder.encodeJsonElement(jsonElement)
            }
        }
    }
}
