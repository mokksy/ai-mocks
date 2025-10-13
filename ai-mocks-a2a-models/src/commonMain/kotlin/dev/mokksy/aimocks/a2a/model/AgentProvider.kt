package dev.mokksy.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the service provider of an agent.
 *
 * Example:
 * ```json
 *  {
 *       "organization": "Google",
 *       "url": "https://ai.google.dev"
 *   }
 * ```
 */
@Serializable
public data class AgentProvider(
    /**
     * The name of the agent provider's organization.
     */
    @SerialName("organization")
    val organization: String,
    /**
     * A URL for the agent provider's website or relevant documentation.
     */
    @SerialName("url")
    val url: String? = null,
) {
    public companion object {
        /**
         * Creates a new AgentProvider using the DSL builder.
         *
         * @param init The lambda to configure the agent provider.
         * @return A new AgentProvider instance.
         */
        @JvmStatic
        public fun build(init: AgentProviderBuilder.() -> Unit): AgentProvider =
            AgentProviderBuilder().apply(init).build()
    }
}
