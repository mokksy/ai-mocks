package me.kpavlov.aimocks.core.json.schema.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import me.kpavlov.aimocks.core.json.schema.ArrayPropertyDefinition
import me.kpavlov.aimocks.core.json.schema.BooleanPropertyDefinition
import me.kpavlov.aimocks.core.json.schema.NumericPropertyDefinition
import me.kpavlov.aimocks.core.json.schema.ObjectPropertyDefinition
import me.kpavlov.aimocks.core.json.schema.PropertyDefinition
import me.kpavlov.aimocks.core.json.schema.ReferencePropertyDefinition
import me.kpavlov.aimocks.core.json.schema.StringPropertyDefinition
import me.kpavlov.mokksy.serializers.StringOrListSerializer

/**
 * Serializer for [PropertyDefinition] that handles polymorphic serialization.
 */
public class PropertyDefinitionSerializer : KSerializer<PropertyDefinition> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PropertyDefinition")

    @Suppress("LongMethod")
    override fun deserialize(decoder: Decoder): PropertyDefinition {
        require(decoder is JsonDecoder) { "This serializer can only be used with JSON" }

        val jsonElement = decoder.decodeJsonElement()
        require(jsonElement is JsonObject) { "Expected JSON object for PropertyDefinition" }

        // Check if it's a reference
        if (jsonElement.containsKey("\$ref")) {
            return decoder.json.decodeFromJsonElement(
                ReferencePropertyDefinition.serializer(),
                jsonElement,
            )
        }

        // Determine the type
        val typeElement = jsonElement["type"]
        val types =
            when (typeElement) {
                null -> null
                is JsonObject -> listOf(typeElement.toString())
                else -> {
                    val typeSerializer = StringOrListSerializer()
                    decoder.json.decodeFromJsonElement(typeSerializer, typeElement)
                }
            }

        return when {
            // If it has items, it's an array
            jsonElement.containsKey("items") -> {
                decoder.json.decodeFromJsonElement(
                    ArrayPropertyDefinition.serializer(),
                    jsonElement,
                )
            }
            // If it has properties, it's an object
            jsonElement.containsKey("properties") -> {
                decoder.json.decodeFromJsonElement(
                    ObjectPropertyDefinition.serializer(),
                    jsonElement,
                )
            }
            // Check type-specific properties
            types != null -> {
                when {
                    types.contains("string") -> {
                        decoder.json.decodeFromJsonElement(
                            StringPropertyDefinition.serializer(),
                            jsonElement,
                        )
                    }

                    types.contains("integer") || types.contains("number") -> {
                        decoder.json.decodeFromJsonElement(
                            NumericPropertyDefinition.serializer(),
                            jsonElement,
                        )
                    }

                    types.contains("boolean") -> {
                        decoder.json.decodeFromJsonElement(
                            BooleanPropertyDefinition.serializer(),
                            jsonElement,
                        )
                    }

                    types.contains("array") -> {
                        decoder.json.decodeFromJsonElement(
                            ArrayPropertyDefinition.serializer(),
                            jsonElement,
                        )
                    }

                    types.contains("object") -> {
                        decoder.json.decodeFromJsonElement(
                            ObjectPropertyDefinition.serializer(),
                            jsonElement,
                        )
                    }

                    else -> {
                        // Default to string for unknown types
                        decoder.json.decodeFromJsonElement(
                            StringPropertyDefinition.serializer(),
                            jsonElement,
                        )
                    }
                }
            }

            else -> {
                // If no type is specified, default to string
                decoder.json.decodeFromJsonElement(
                    StringPropertyDefinition.serializer(),
                    jsonElement,
                )
            }
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: PropertyDefinition,
    ) {
        val jsonEncoder =
            encoder as? kotlinx.serialization.json.JsonEncoder
                ?: throw IllegalArgumentException("This serializer can only be used with JSON")

        when (value) {
            is StringPropertyDefinition ->
                jsonEncoder.encodeSerializableValue(
                    StringPropertyDefinition.serializer(),
                    value,
                )

            is NumericPropertyDefinition ->
                jsonEncoder.encodeSerializableValue(
                    NumericPropertyDefinition.serializer(),
                    value,
                )

            is ArrayPropertyDefinition ->
                jsonEncoder.encodeSerializableValue(
                    ArrayPropertyDefinition.serializer(),
                    value,
                )

            is ObjectPropertyDefinition ->
                jsonEncoder.encodeSerializableValue(
                    ObjectPropertyDefinition.serializer(),
                    value,
                )

            is ReferencePropertyDefinition ->
                jsonEncoder.encodeSerializableValue(
                    ReferencePropertyDefinition.serializer(),
                    value,
                )

            is BooleanPropertyDefinition ->
                jsonEncoder.encodeSerializableValue(
                    BooleanPropertyDefinition.serializer(),
                    value,
                )
        }
    }
}
