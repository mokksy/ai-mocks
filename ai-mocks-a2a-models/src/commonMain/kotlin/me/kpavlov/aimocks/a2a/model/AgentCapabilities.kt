package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AgentCapabilities(
    @SerialName("streaming")
    val streaming: Boolean = false,
    @SerialName("pushNotifications")
    val pushNotifications: Boolean = false,
    @SerialName("stateTransitionHistory")
    val stateTransitionHistory: Boolean = false,
) {
    public companion object {
        /**
         * Creates a new AgentCapabilities using the DSL builder.
         *
         * @param init The lambda to configure the agent capabilities.
         * @return A new AgentCapabilities instance.
         */
        public fun build(init: AgentCapabilitiesBuilder.() -> Unit): AgentCapabilities =
            AgentCapabilitiesBuilder().apply(init).build()
    }
}
