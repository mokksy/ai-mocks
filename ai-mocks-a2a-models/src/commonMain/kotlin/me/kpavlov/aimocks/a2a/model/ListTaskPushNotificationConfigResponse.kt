/*
 * ListTaskPushNotificationConfigResponse.kt
 */
package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.kpavlov.aimocks.a2a.model.serializers.RequestIdSerializer

/**
 * https://a2a-protocol.org/latest/specification/#77-taskspushnotificationconfiglist
 */
@Serializable
public data class ListTaskPushNotificationConfigResponse(
    @SerialName("jsonrpc")
    @EncodeDefault
    val jsonrpc: String = "2.0",
    @SerialName("id")
    @Serializable(with = RequestIdSerializer::class)
    val id: RequestId? = null,
    @SerialName("result")
    val result: List<TaskPushNotificationConfig>? = null,
    @SerialName("error")
    val error: JSONRPCError? = null,
) {
    init {
        require(jsonrpc == "2.0") { "jsonrpc not constant value '2.0' - $jsonrpc" }
    }

    public companion object
}
