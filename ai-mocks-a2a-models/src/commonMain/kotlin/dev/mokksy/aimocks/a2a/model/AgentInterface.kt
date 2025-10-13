package dev.mokksy.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Declares a combination of a target URL and a transport protocol
 * for interacting with the agent.
 *
 * This allows agents to expose the same functionality over multiple transport mechanisms.
 */
@Serializable
public data class AgentInterface(
    /**
     * The transport protocol supported at this URL.
     */
    @SerialName("transport")
    val transport: Transport,

    /**
     * The URL where this interface is available. Must be a valid absolute HTTPS URL in production.
     *
     *  Examples:
     *  - `https://api.example.com/a2a/v1`
     *  - `https://grpc.example.com/a2a`
     *  - `https://rest.example.com/v1`
     */
    @SerialName("url")
    val url: String,
) {
    public companion object
}
