/*
 * SendMessageResponse.kt
 *
 * JSON-RPC response for the message/send method according to the A2A protocol.
 * See: https://a2a-protocol.org/latest/specification/
 */
package dev.mokksy.aimocks.a2a.model

import dev.mokksy.aimocks.a2a.model.serializers.RequestIdSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * JSON-RPC 2.0 response for the message/send method in the A2A protocol.
 *
 * Contains the task information created by sending a message to an agent.
 * The task represents the work being performed by the agent in response to the message.
 *
 * @see [A2A Protocol - Send a Message](https://a2a-protocol.org/latest/specification/)
 * @see [JSON-RPC 2.0 Specification](https://www.jsonrpc.org/specification)
 */
@Serializable
public data class SendMessageResponse(
    @SerialName("jsonrpc")
    @EncodeDefault
    val jsonrpc: String = "2.0",
    @SerialName("id")
    @Serializable(with = RequestIdSerializer::class)
    val id: RequestId? = null,
    @SerialName("result")
    val result: Task? = null,
    @SerialName("error")
    val error: JSONRPCError? = null,
) {
    init {
        require(jsonrpc == "2.0") { "jsonrpc not constant value 2.0 - $jsonrpc" }
    }

    public companion object
}
