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
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

public class SystemPromptsSerializer : KSerializer<List<MessageCreateParams.SystemPrompt>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SystemPrompts")

    override fun deserialize(decoder: Decoder): List<MessageCreateParams.SystemPrompt> {
        val jsonDecoder =
            decoder as? JsonDecoder
                ?: throw SerializationException("This serializer can only be used with JSON")

        val element = jsonDecoder.decodeJsonElement()

        return when (element) {
            is JsonPrimitive ->
                listOf(
                    MessageCreateParams.SystemPrompt(
                        text = element.contentOrNull ?: "",
                    ),
                )

            is JsonArray ->
                element.map {
                    val propmt =
                        Json.decodeFromJsonElement(
                            element = it,
                            deserializer = MessageCreateParams.SystemPrompt.serializer(),
                        )
                    propmt
                }

            else -> throw SerializationException("Expected string or array of strings")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: List<MessageCreateParams.SystemPrompt>,
    ) {
        TODO("Not implemented")
    }
}
