package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AgentAuthentication(
    @SerialName("schemes")
    val schemes: List<String>,
    @SerialName("credentials")
    val credentials: String? = null,
) {
    public companion object {
        /**
         * Creates a new AgentAuthentication using the DSL builder.
         *
         * @param init The lambda to configure the agent authentication.
         * @return A new AgentAuthentication instance.
         */
        public fun build(init: AgentAuthenticationBuilder.() -> Unit): AgentAuthentication =
            AgentAuthenticationBuilder().apply(init).build()
    }
}
