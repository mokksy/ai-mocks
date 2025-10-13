package dev.mokksy.aimocks.a2a.model.serializers

import dev.mokksy.aimocks.a2a.model.TaskArtifactUpdateEvent
import dev.mokksy.aimocks.a2a.model.TaskStatusUpdateEvent
import dev.mokksy.aimocks.a2a.model.TaskUpdateEvent
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
import kotlinx.serialization.json.jsonObject

/**
 * Polymorphic serializer for TaskUpdateEvent hierarchy.
 * Determines which subclass to use based on the presence of "artifact" or "status" properties.
 */
public class TaskUpdateEventSerializer : KSerializer<TaskUpdateEvent> {
    // Serializers for the concrete implementations
    private val statusUpdateSerializer = TaskStatusUpdateEvent.serializer()
    private val artifactUpdateSerializer = TaskArtifactUpdateEvent.serializer()

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TaskUpdateEvent")

    override fun deserialize(decoder: Decoder): TaskUpdateEvent {
        // We need to work with JSON to inspect the properties
        val jsonDecoder =
            decoder as? JsonDecoder
                ?: throw SerializationException(
                    "TaskUpdateEventSerializer can only be used with JSON",
                )

        val jsonElement = jsonDecoder.decodeJsonElement()
        if (jsonElement !is JsonObject) {
            throw SerializationException("Expected JSON object for TaskUpdateEvent")
        }

        return if (jsonElement["result"] is JsonElement) {
            doDecode(jsonElement["result"]!!, jsonDecoder)
        } else {
            doDecode(jsonElement, jsonDecoder)
        }
    }

    private fun doDecode(
        jsonElement: JsonElement,
        jsonDecoder: JsonDecoder,
    ): TaskUpdateEvent {
        // Determine which subclass to use based on the presence of properties
        return when {
            "artifact" in jsonElement.jsonObject -> {
                // If it has an "artifact" property, it's a TaskArtifactUpdateEvent
                jsonDecoder.json.decodeFromJsonElement(artifactUpdateSerializer, jsonElement)
            }

            "status" in jsonElement.jsonObject -> {
                // If it has a "status" property, it's a TaskStatusUpdateEvent
                jsonDecoder.json.decodeFromJsonElement(statusUpdateSerializer, jsonElement)
            }

            else -> {
                throw SerializationException(
                    "Cannot determine TaskUpdateEvent type: neither 'artifact' nor 'status' property found",
                )
            }
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: TaskUpdateEvent,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: throw SerializationException(
                    "TaskUpdateEventSerializer can only be used with JSON",
                )

        // Delegate to the appropriate serializer based on the concrete type
        val jsonElement: JsonElement =
            when (value) {
                is TaskStatusUpdateEvent ->
                    jsonEncoder.json.encodeToJsonElement(
                        statusUpdateSerializer,
                        value,
                    )

                is TaskArtifactUpdateEvent ->
                    jsonEncoder.json.encodeToJsonElement(
                        artifactUpdateSerializer,
                        value,
                    )
            }

        jsonEncoder.encodeJsonElement(jsonElement)
    }
}
