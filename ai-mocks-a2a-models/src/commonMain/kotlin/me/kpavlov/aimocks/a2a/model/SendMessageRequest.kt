/*
 * SendMessageRequest.kt
 *
 * JSON-RPC request for the message/send method according to the A2A protocol.
 * See: https://a2a-protocol.org/latest/specification/
 */
package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.kpavlov.aimocks.a2a.model.serializers.RequestIdSerializer

/**
 * JSON-RPC 2.0 request for sending a message to an agent using the A2A protocol's message/send method.
 *
 * This is the primary method for agent-to-agent communication in the A2A protocol.
 * It allows sending messages containing text, files, or structured data to another agent.
 *
 * @see [A2A Protocol - Send a Message](https://a2a-protocol.org/latest/specification/)
 * @see [JSON-RPC 2.0 Specification](https://www.jsonrpc.org/specification)
 */
@Serializable
public data class SendMessageRequest(
    @SerialName("jsonrpc")
    @EncodeDefault
    val jsonrpc: String = "2.0",
    @SerialName("id")
    @Serializable(with = RequestIdSerializer::class)
    val id: RequestId? = null,
    @SerialName("method")
    @EncodeDefault
    val method: String = "message/send",
    @SerialName("params")
    val params: MessageSendParams,
) : A2ARequest {
    init {
        require(jsonrpc == "2.0") { "jsonrpc not constant value \"2.0\" - $jsonrpc" }
        require(method == "message/send") { "method not constant value \"message/send\" - $method" }
    }

    public companion object
}
