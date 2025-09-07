/*
 * GetAuthenticatedExtendedCardRequest.kt
 */
package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.kpavlov.aimocks.a2a.model.serializers.RequestIdSerializer

/**
 * https://a2a-protocol.org/latest/specification/#710-agentgetauthenticatedextendedcard
 */
@Serializable
public data class GetAuthenticatedExtendedCardRequest(
    @SerialName("jsonrpc")
    @EncodeDefault
    override val jsonrpc: String = "2.0",
    @SerialName("id")
    @Serializable(with = RequestIdSerializer::class)
    override val id: RequestId? = null,
    @SerialName("method")
    @EncodeDefault
    override val method: String = "agent/getAuthenticatedExtendedCard",
    @SerialName("params")
    val params: Nothing? = null,
) : A2ARequest {
    init {
        require(jsonrpc == "2.0") { "jsonrpc not constant value 2.0 - $jsonrpc" }
        require(method == "agent/getAuthenticatedExtendedCard") {
            "method not constant value \"agent/getAuthenticatedExtendedCard\" - $method"
        }
    }

    public companion object
}
