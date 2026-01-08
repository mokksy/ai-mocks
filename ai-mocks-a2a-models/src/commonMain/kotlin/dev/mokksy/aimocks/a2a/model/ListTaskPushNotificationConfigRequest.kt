/*
 * ListTaskPushNotificationConfigRequest.kt
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
@JvmRecord
public data class ListTaskPushNotificationConfigRequest(
    @SerialName("jsonrpc")
    @EncodeDefault
    override val jsonrpc: String = "2.0",
    @SerialName("id")
    @Serializable(with = RequestIdSerializer::class)
    override val id: RequestId? = null,
    @SerialName("method")
    @EncodeDefault
    override val method: String = "tasks/pushNotificationConfig/list",
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
