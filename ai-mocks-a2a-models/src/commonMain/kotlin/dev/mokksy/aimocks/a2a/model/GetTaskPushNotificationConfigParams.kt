package dev.mokksy.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines parameters for fetching a specific push notification configuration for a task.
 *
 * @param id The unique identifier of the task.
 * @param pushNotificationConfigId The ID of the push notification configuration to retrieve.
 * @param metadata Optional metadata associated with the request.
 */
@Serializable
@JvmRecord
public data class GetTaskPushNotificationConfigParams(
    /**
     * The unique identifier of the task.
     */
    @SerialName("id")
    val id: String,

    /**
     * The ID of the push notification configuration to retrieve.
     */
    @SerialName("pushNotificationConfigId")
    val pushNotificationConfigId: String? = null,

    /**
     * Optional metadata associated with the request.
     */
    @SerialName("metadata")
    val metadata: Metadata? = null,
) {
    public companion object
}
