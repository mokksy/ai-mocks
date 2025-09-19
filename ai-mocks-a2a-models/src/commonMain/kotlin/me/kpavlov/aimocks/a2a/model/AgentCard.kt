package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The [AgentCard] is a self-describing manifest for an agent. It provides essential
 * metadata including the agent's identity, capabilities, skills, supported
 * communication methods, and security requirements.
 *
 * @see [A2A Protocol - Agent Card](https://a2a-protocol.org/latest/specification/#55-agentcard-object-structure)
 */
@Serializable
public data class AgentCard(
    /**
     * The version of the A2A protocol this agent supports.
     * Defaults to "0.3.0".
     */
    @SerialName("protocolVersion")
    @EncodeDefault
    val protocolVersion: String = "0.3.0",

    /**
     * A human-readable name for the agent.
     *
     * Example: "Recipe Agent"
     */
    @SerialName("name")
    val name: String,

    /**
     * A human-readable description of the agent, assisting users and other agents
     * in understanding its purpose.
     *
     * Example: "Agent that helps users with recipes and cooking."
     */
    @SerialName("description")
    val description: String? = null,

    /**
     * The preferred endpoint URL for interacting with the agent.
     * This URL MUST support the transport specified by 'preferredTransport'.
     *
     * Example: "https://api.example.com/a2a/v1"
     */
    @SerialName("url")
    val url: String,
    /**
     * The transport protocol for the preferred endpoint (the main 'url' field).
     * If not specified, defaults to 'JSONRPC'.
     *
     * IMPORTANT: The transport specified here MUST be available at the main 'url'.
     * This creates a binding between the main URL and its supported transport protocol.
     * Clients should prefer this transport and URL combination when both are supported.
     */
    @SerialName("preferredTransport")
    @EncodeDefault
    val preferredTransport: Transport = Transport.JSONRPC,
    /**
     * A list of additional supported interfaces (transport and URL combinations).
     * This allows agents to expose multiple transports, potentially at different URLs.
     *
     * Best practices:
     * - SHOULD include all supported transports for completeness
     * - SHOULD include an entry matching the main 'url' and 'preferredTransport'
     * - MAY reuse URLs if multiple transports are available at the same endpoint
     * - MUST accurately declare the transport available at each URL
     *
     * Clients can select any interface from this list based on their transport capabilities
     * and preferences. This enables transport negotiation and fallback scenarios.
     */
    @SerialName("additionalInterfaces")
    val additionalInterfaces: List<AgentInterface>? = null,

    /**
     * An optional URL to an icon for the agent.
     */
    @SerialName("iconUrl")
    val iconUrl: String? = null,

    /**
     * Information about the agent's service provider.
     */
    @SerialName("provider")
    val provider: AgentProvider? = null,

    /**
     * The agent's own version number. The format is defined by the provider.
     *
     * Example: "1.0.0"
     */
    @SerialName("version")
    val version: String,

    /**
     * An optional URL to the agent's documentation.
     */
    @SerialName("documentationUrl")
    val documentationUrl: String? = null,

    /**
     * A declaration of optional capabilities supported by the agent.
     */
    @SerialName("capabilities")
    val capabilities: AgentCapabilities,

    /**
     * Default set of supported input MIME types for all skills, which can be
     * overridden on a per-skill basis.
     */
    @SerialName("defaultInputModes")
    val defaultInputModes: List<String>,

    /**
     * Default set of supported output MIME types for all skills, which can be
     * overridden on a per-skill basis.
     */
    @SerialName("defaultOutputModes")
    val defaultOutputModes: List<String>,

    /**
     * The set of skills, or distinct capabilities, that the agent can perform.
     */
    @SerialName("skills")
    val skills: List<AgentSkill>,

    /**
     * A list of security requirement objects that apply to all agent interactions. Each object
     * lists security schemes that can be used. Follows the OpenAPI 3.0 Security Requirement Object.
     * This list can be seen as an OR of ANDs. Each object in the list describes one possible
     * set of security requirements that must be present on a request. This allows specifying,
     * for example, "callers must either use OAuth OR an API Key AND mTLS."
     *
     * Example:
     * ```
     * [
     *   {
     *     "oauth": [ "read" ]
     *   },
     *   {
     *     "api-key": [],
     *     "mtls": []
     *   }
     * ]
     * ```
     */
    @SerialName("security")
    val security: List<Map<String, List<String>>>? = null,

    /**
     * A declaration of the security schemes available to authorize requests. The key is the
     * scheme name. Follows the OpenAPI 3.0 Security Scheme Object.
     */
    @SerialName("securitySchemes")
    val securitySchemes: Map<String, SecurityScheme>? = null,
    /**
     * JSON Web Signatures computed for this [AgentCard].
     */
    @SerialName("signatures")
    val signatures: List<AgentCardSignature>? = null,

    /**
     * If true, the agent can provide an extended agent card with additional details
     * to authenticated users. Defaults to false.
     */
    @SerialName("supportsAuthenticatedExtendedCard")
    @EncodeDefault
    val supportsAuthenticatedExtendedCard: Boolean = false,
) {
    public companion object
}
