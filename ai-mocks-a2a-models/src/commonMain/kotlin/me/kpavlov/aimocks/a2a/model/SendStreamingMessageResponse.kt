/*
 * SendStreamingMessageResponse.kt
 *
 * JSON-RPC response for the message/stream method according to the A2A protocol.
 * See: https://a2a-protocol.org/latest/specification/
 */
package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.kpavlov.aimocks.a2a.model.serializers.RequestIdSerializer
import me.kpavlov.aimocks.a2a.model.serializers.TaskUpdateEventSerializer

/**
 * JSON-RPC 2.0 response for the message/stream method in the A2A protocol.
 *
 * Contains the initial task information for a streaming message request.
 * Subsequent updates are delivered via Server-Sent Events (SSE) as TaskUpdateEvent instances.
 *
 * @see [A2A Protocol - Streaming Messages](https://a2a-protocol.org/latest/specification/)
 * @see [JSON-RPC 2.0 Specification](https://www.jsonrpc.org/specification)
 */
@Serializable
public data class SendStreamingMessageResponse(
    @SerialName("jsonrpc")
    @EncodeDefault
    val jsonrpc: String = "2.0",
    @SerialName("id")
    @Serializable(with = RequestIdSerializer::class)
    @EncodeDefault
    var id: RequestId? = null,
    @SerialName("result")
    @Polymorphic
    @Serializable(with = TaskUpdateEventSerializer::class)
    @EncodeDefault
    val result: TaskUpdateEvent? = null,
    @SerialName("error")
    @EncodeDefault
    val error: JSONRPCError? = null,
) {
    init {
        require(jsonrpc == "2.0") { "jsonrpc not constant value 2.0 - $jsonrpc" }
    }
}
