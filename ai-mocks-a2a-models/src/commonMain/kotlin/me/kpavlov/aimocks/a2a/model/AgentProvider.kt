package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AgentProvider(
    @SerialName("organization")
    val organization: String,
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
        public fun build(init: AgentProviderBuilder.() -> Unit): AgentProvider =
            AgentProviderBuilder().apply(init).build()
    }
}
