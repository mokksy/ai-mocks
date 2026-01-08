package dev.mokksy.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A declaration of a protocol extension supported by an Agent.
 *
 * Example:
 * ```json
 * {
 *   "description": "Google OAuth 2.0 authentication",
 *   "required": false,
 *   "uri": "https://developers.google.com/identity/protocols/oauth2"
 * }
 * ```
 */
@Serializable
@JvmRecord
public data class AgentExtension(
    /**
     * A human-readable description of how this agent uses the extension.
     */
    @SerialName("description")
    val description: String? = null,

    /**
     * Optional, extension-specific configuration parameters.
     */
    @SerialName("params")
    val params: Data? = null,

    /**
     * If true, the client must understand and comply with the extension's requirements
     * to interact with the agent.
     */
    @SerialName("required")
    val required: Boolean? = null,

    /**
     * The unique URI identifying the extension.
     */
    @SerialName("uri")
    val uri: String,
) {
    public companion object {
        /**
         * Creates a new AgentExtension using the DSL builder.
         *
         * @param init The lambda to configure the agent extension.
         * @return A new AgentExtension instance.
         */
        @JvmStatic
        public fun create(init: AgentExtensionBuilder.() -> Unit): AgentExtension =
            AgentExtensionBuilder().apply(init).build()
    }
}
