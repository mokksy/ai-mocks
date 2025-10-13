/*
 * ListTaskPushNotificationConfigResponse.kt
 */
package dev.mokksy.aimocks.a2a.model

import dev.mokksy.aimocks.a2a.model.serializers.RequestIdSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
