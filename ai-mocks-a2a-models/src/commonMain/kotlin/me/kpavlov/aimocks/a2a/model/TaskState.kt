package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.kpavlov.aimocks.a2a.model.serializers.TaskStateSerializer

/**
 * Defines the lifecycle states of a Task.
 *
 * This enum represents all possible states a task can be in during its lifecycle,
 * from initial submission through various processing states to final completion or termination.
 *
 * @see [A2A Protocol - TaskState](https://a2a-protocol.org/latest/specification/#63-taskstate-enum)
 */
@Serializable(with = TaskStateSerializer::class)
public enum class TaskState(
    public val value: String,
) {
    /**
     * Task has been received and queued for processing.
     */
    @SerialName("submitted")
    SUBMITTED("submitted"),

    /**
     * Task is currently being processed by the agent.
     */
    @SerialName("working")
    WORKING("working"),

    /**
     * Task is waiting for additional input from the client to continue processing.
     */
    @SerialName("input-required")
    INPUT_REQUIRED("input-required"),

    /**
     * Task has been completed successfully.
     */
    @SerialName("completed")
    COMPLETED("completed"),

    /**
     * Task has been canceled by the client or system.
     */
    @SerialName("canceled")
    CANCELED("canceled"),

    /**
     * Task has failed due to an error during processing.
     */
    @SerialName("failed")
    FAILED("failed"),

    /**
     * Task has been rejected due to policy, capacity, or other constraints.
     */
    @SerialName("rejected")
    REJECTED("rejected"),

    /**
     * Task requires additional authentication before it can proceed.
     */
    @SerialName("auth-required")
    AUTH_REQUIRED("auth-required"),

    /**
     * Task state is unknown or cannot be determined.
     */
    @SerialName("unknown")
    UNKNOWN("unknown"),
}

public fun TaskState.Companion.fromString(value: String): TaskState? {
    return TaskState.entries.find { it.value == value }
}

