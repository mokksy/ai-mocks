package me.kpavlov.aimocks.anthropic.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * Polymorphic serializer for MessageCreateParams.Content.
 */
public class ContentSerializer : KSerializer<MessageCreateParams.Content> {
    private val objectSerializer = MessageCreateParams.Content.serializer()

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TaskUpdateEvent")

    override fun deserialize(decoder: Decoder): MessageCreateParams.Content {
        // We need to work with JSON to inspect the properties
        val jsonDecoder =
            decoder as? JsonDecoder
                ?: throw SerializationException(
                    "TaskUpdateEventSerializer can only be used with JSON",
                )

        val jsonElement = jsonDecoder.decodeJsonElement()

        return when (jsonElement) {
            is JsonPrimitive -> {
                MessageCreateParams.TextContent(jsonElement.contentOrNull)
            }

            is JsonArray -> {
                MessageCreateParams.ContentList(
                    blocks = jsonElement.map(this::deserializeContentBlock),
                )
            }

            !is JsonObject -> {
                objectSerializer.deserialize(decoder)
            }

            else -> {
                throw SerializationException("Expected JSON object for MessageCreateParams.Content")
            }
        }
    }

    private fun deserializeContentBlock(element: JsonElement): MessageCreateParams.ContentBlock =
        Json.decodeFromJsonElement(
            element = element,
            deserializer = MessageCreateParams.ContentBlock.serializer(),
        )
//        MessageCreateParams.ContentBlock(
//            text = JsonElement.jsonObject["text"]?.jsonPrimitive?.contentOrNull
//        )

    override fun serialize(
        encoder: Encoder,
        value: MessageCreateParams.Content,
    ) {
        objectSerializer.serialize(encoder, value)
    }
}
