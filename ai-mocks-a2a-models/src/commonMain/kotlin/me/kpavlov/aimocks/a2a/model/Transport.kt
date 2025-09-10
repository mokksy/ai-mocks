/*
 * Transport.kt
 *
 * Transport enum for the A2A protocol specification version 0.3.0
 * See: https://a2a-protocol.org/latest/specification/
 */
package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TransportProtocol types supported by the A2A protocol.
 *
 * This enum defines the different transport mechanisms that can be used
 * for agent-to-agent communication according to the A2A specification.
 *
 * @see [A2A Protocol - Transport](https://a2a-protocol.org/latest/specification/)
 */
@Serializable
public enum class Transport {
    /**
     * JSON-RPC 2.0 transport over HTTP/WebSocket.
     */
    @SerialName("JSONRPC")
    JSONRPC,

    /**
     * gRPC transport for high-performance communication.
     */
    @SerialName("GRPC")
    GRPC,

    /**
     * HTTP transport with JSON payloads.
     */
    @SerialName("HTTP+JSON")
    HTTP_JSON,
}
