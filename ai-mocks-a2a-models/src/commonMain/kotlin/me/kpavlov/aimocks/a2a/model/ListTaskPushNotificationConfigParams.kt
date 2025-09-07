/*
 * ListTaskPushNotificationConfigParams.kt
 */
package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Parameters for listing push notification configurations
 * https://a2a-protocol.org/latest/specification/#77-taskspushnotificationconfiglist
 */
@Serializable
public data class ListTaskPushNotificationConfigParams(
    @SerialName("limit")
    @EncodeDefault
    val limit: Int? = null,
    @SerialName("offset")
    @EncodeDefault
    val offset: Int? = null,
) {
    public companion object
}
