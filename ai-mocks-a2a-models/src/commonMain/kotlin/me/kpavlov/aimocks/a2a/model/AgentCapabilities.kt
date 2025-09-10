package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines optional capabilities supported by an agent.
 */
@Serializable
public data class AgentCapabilities(
    /**
     * A list of protocol extensions supported by the agent.
     */
    @SerialName("extensions")
    val extensions: List<AgentExtension>? = null,
    /**
     * Indicates if the agent supports Server-Sent Events (SSE) for streaming responses.
     */
    @SerialName("streaming")
    @EncodeDefault
    val streaming: Boolean = false,
    /**
     * Indicates if the agent supports sending push notifications for asynchronous task updates.
     */
    @SerialName("pushNotifications")
    @EncodeDefault
    val pushNotifications: Boolean = false,
    /**
     * Indicates if the agent provides a history of state transitions for a task.
     */
    @SerialName("stateTransitionHistory")
    @EncodeDefault
    val stateTransitionHistory: Boolean = false,
) {
    public companion object {
        /**
         * Creates a new AgentCapabilities using the DSL builder.
         *
         * @param init The lambda to configure the agent capabilities.
         * @return A new AgentCapabilities instance.
         */
        @JvmStatic
        public fun build(init: AgentCapabilitiesBuilder.() -> Unit): AgentCapabilities =
            AgentCapabilitiesBuilder().apply(init).build()
    }
}
