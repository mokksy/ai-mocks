/*
 * GetAuthenticatedExtendedCardResponse.kt
 */
package dev.mokksy.aimocks.a2a.model

import dev.mokksy.aimocks.a2a.model.serializers.RequestIdSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * https://a2a-protocol.org/latest/specification/#710-agentgetauthenticatedextendedcard
 */
@Serializable
@JvmRecord
public data class GetAuthenticatedExtendedCardResponse(
    @SerialName("jsonrpc")
    @EncodeDefault
    val jsonrpc: String = "2.0",
    @SerialName("id")
    @Serializable(with = RequestIdSerializer::class)
    val id: RequestId? = null,
    @SerialName("result")
    val result: AgentCard? = null,
    @SerialName("error")
    val error: JSONRPCError? = null,
) {
    init {
        require(jsonrpc == "2.0") { "jsonrpc not constant value 2.0 - $jsonrpc" }
        require((result == null) xor (error == null)) {
            "Either result or error must be present, but not both"
        }
    }

    public companion object
}
