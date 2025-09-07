/*
 * ListTaskPushNotificationConfigRequest.kt
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
public data class ListTaskPushNotificationConfigRequest(
    @SerialName("jsonrpc")
    @EncodeDefault
    val jsonrpc: String = "2.0",
    @SerialName("id")
    @Serializable(with = RequestIdSerializer::class)
    val id: RequestId? = null,
    @SerialName("method")
    @EncodeDefault
    val method: String = "tasks/pushNotificationConfig/list",
    @SerialName("params")
    val params: ListTaskPushNotificationConfigParams? = null,
) : A2ARequest {
    init {
        require(jsonrpc == "2.0") { "jsonrpc not constant value 2.0 - $jsonrpc" }
        require(method == "tasks/pushNotificationConfig/list") {
            "method not constant value \"tasks/pushNotificationConfig/list\" - $method"
        }
    }

    public companion object
}
