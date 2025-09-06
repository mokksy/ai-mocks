/*
 * SendStreamingMessageRequest.kt
 *
 * JSON-RPC request for the message/stream method according to the A2A protocol.
 * See: https://a2a-protocol.org/latest/specification/
 */
package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.kpavlov.aimocks.a2a.model.serializers.RequestIdSerializer

/**
 * JSON-RPC 2.0 request for streaming a message to an agent using the A2A protocol's message/stream method.
 *
 * This method allows sending messages with real-time streaming responses via Server-Sent Events (SSE).
 * The server will return incremental updates about the task progress, status changes, and generated artifacts.
 *
 * @see [A2A Protocol - Streaming Messages](https://a2a-protocol.org/latest/specification/)
 * @see [JSON-RPC 2.0 Specification](https://www.jsonrpc.org/specification)
 */
@Serializable
public data class SendStreamingMessageRequest(
    @SerialName("jsonrpc")
    @EncodeDefault
    val jsonrpc: String = "2.0",
    @SerialName("id")
    @Serializable(with = RequestIdSerializer::class)
    var id: RequestId? = null,
    @SerialName("method")
    @EncodeDefault
    val method: String = "message/stream",
    @SerialName("params")
    val params: MessageSendParams,
) : A2ARequest {
    init {
        require(jsonrpc == "2.0") { "jsonrpc not constant value 2.0 - $jsonrpc" }
        require(method == "message/stream") {
            "method not constant value message/stream - $method"
        }
    }
}
