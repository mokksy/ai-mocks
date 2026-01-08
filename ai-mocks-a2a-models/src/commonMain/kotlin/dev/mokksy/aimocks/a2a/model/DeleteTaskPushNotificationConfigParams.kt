/*
 * DeleteTaskPushNotificationConfigParams.kt
 */
package dev.mokksy.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * https://a2a-protocol.org/latest/specification/#781-deletetaskpushnotificationconfigparams-object-taskspushnotificationconfigdelete
 */
@Serializable
@JvmRecord
public data class DeleteTaskPushNotificationConfigParams(
    @SerialName("id")
    val id: TaskId,
    @SerialName("metadata")
    val metadata: Map<String, JsonElement>? = null,
)
