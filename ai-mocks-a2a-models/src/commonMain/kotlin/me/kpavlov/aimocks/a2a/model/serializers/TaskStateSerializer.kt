package me.kpavlov.aimocks.a2a.model.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.kpavlov.aimocks.a2a.model.TaskState

public class TaskStateSerializer : KSerializer<TaskState> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("TaskState", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: TaskState,
    ) {
        val serialName =
            when (value) {
                TaskState.SUBMITTED -> "submitted"
                TaskState.WORKING -> "working"
                TaskState.INPUT_REQUIRED -> "input-required"
                TaskState.COMPLETED -> "completed"
                TaskState.CANCELED -> "canceled"
                TaskState.FAILED -> "failed"
                TaskState.UNKNOWN -> "unknown"
                TaskState.REJECTED -> "rejected"
                TaskState.AUTH_REQUIRED -> "auth-required"
            }
        encoder.encodeString(serialName)
    }

    override fun deserialize(decoder: Decoder): TaskState =
        when (decoder.decodeString()) {
            "submitted" -> TaskState.SUBMITTED
            "working" -> TaskState.WORKING
            "input-required" -> TaskState.INPUT_REQUIRED
            "completed" -> TaskState.COMPLETED
            "canceled" -> TaskState.CANCELED
            "failed" -> TaskState.FAILED
            "unknown" -> TaskState.UNKNOWN
            else -> TaskState.UNKNOWN
        }
}
