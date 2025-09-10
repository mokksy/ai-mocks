package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * AgentCardSignature represents a JWS signature of an AgentCard.
 * This follows the JSON format of an RFC 7515 JSON Web Signature (JWS).
 */
@Serializable
public data class AgentCardSignature(
    /**
     * The unprotected JWS header values.
     */
    @SerialName("header")
    val header: Data? = null,

    /**
     * The protected JWS header for the signature. This is a Base64url-encoded
     * JSON object, as per RFC 7515.
     */
    @SerialName("protected")
    val protectedHeader: String,

    /**
     * The computed signature, Base64url-encoded.
     */
    @SerialName("signature")
    val signature: String,
) {
    public companion object {
        /**
         * Creates a new AgentCardSignature using the DSL builder.
         *
         * @param init The lambda to configure the signature.
         * @return A new AgentCardSignature instance.
         */
        @JvmStatic
        public fun build(init: AgentCardSignatureBuilder.() -> Unit): AgentCardSignature =
            AgentCardSignatureBuilder().apply(init).build()
    }
}
