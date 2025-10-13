/*
 * DeleteTaskPushNotificationConfigResponse.kt
 */
package dev.mokksy.aimocks.a2a.model

import dev.mokksy.aimocks.a2a.model.serializers.RequestIdSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * https://a2a-protocol.org/latest/specification/#78-taskspushnotificationconfigdelete
 */
@Serializable
public data class DeleteTaskPushNotificationConfigResponse(
    @SerialName("jsonrpc")
    @EncodeDefault
    val jsonrpc: String = "2.0",
    @SerialName("id")
    @Serializable(with = RequestIdSerializer::class)
    val id: RequestId? = null,
    @SerialName("result")
    @EncodeDefault(mode = EncodeDefault.Mode.NEVER)
    val result: Nothing? = null,
    @SerialName("error")
    @EncodeDefault(mode = EncodeDefault.Mode.NEVER)
    val error: JSONRPCError? = null,
) {
    init {
        require(jsonrpc == "2.0") { "jsonrpc not constant value 2.0 - $jsonrpc" }
    }

    public companion object
}
