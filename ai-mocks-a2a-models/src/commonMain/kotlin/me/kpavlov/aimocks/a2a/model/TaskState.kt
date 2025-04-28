package me.kpavlov.aimocks.a2a.model

import TaskStateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = TaskStateSerializer::class)
public enum class TaskState(public val value: String) {
    @SerialName("submitted")
    SUBMITTED("submitted"),
    @SerialName("working")
    WORKING("working"),
    @SerialName("input-required")
    INPUT_REQUIRED("input-required"),
    @SerialName("completed")
    COMPLETED("completed"),
    @SerialName("canceled")
    CANCELED("canceled"),
    @SerialName("failed")
    FAILED("failed"),
    @SerialName("unknown")
    UNKNOWN("unknown");
}
