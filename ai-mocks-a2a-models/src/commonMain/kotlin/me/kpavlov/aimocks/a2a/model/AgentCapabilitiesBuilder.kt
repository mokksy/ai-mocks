package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder class for creating [AgentCapabilities] instances.
 *
 * This builder provides a fluent API for creating AgentCapabilities objects,
 * making it easier to configure agent capabilities.
 *
 * Example usage:
 * ```kotlin
 * val capabilities = AgentCapabilitiesBuilder()
 *     .streaming(true)
 *     .pushNotifications(true)
 *     .stateTransitionHistory(false)
 *     .create()
 * ```
 */
public class AgentCapabilitiesBuilder {
    public var streaming: Boolean = false
    public var pushNotifications: Boolean = false
    public var stateTransitionHistory: Boolean = false

    /**
     * Sets the streaming capability.
     *
     * @param streaming Whether the agent supports streaming.
     * @return This builder instance for method chaining.
     */
    public fun streaming(streaming: Boolean): AgentCapabilitiesBuilder =
        apply {
            this.streaming = streaming
        }

    /**
     * Sets the push notifications capability.
     *
     * @param pushNotifications Whether the agent supports push notifications.
     * @return This builder instance for method chaining.
     */
    public fun pushNotifications(pushNotifications: Boolean): AgentCapabilitiesBuilder =
        apply {
            this.pushNotifications = pushNotifications
        }

    /**
     * Sets the state transition history capability.
     *
     * @param stateTransitionHistory Whether the agent supports state transition history.
     * @return This builder instance for method chaining.
     */
    public fun stateTransitionHistory(stateTransitionHistory: Boolean): AgentCapabilitiesBuilder =
        apply {
            this.stateTransitionHistory = stateTransitionHistory
        }

    /**
     * Builds an [AgentCapabilities] instance with the configured parameters.
     *
     * @return A new [AgentCapabilities] instance.
     */
    public fun build(): AgentCapabilities =
        AgentCapabilities(
            streaming = streaming,
            pushNotifications = pushNotifications,
            stateTransitionHistory = stateTransitionHistory,
        )
}

/**
 * Top-level DSL function for creating [AgentCapabilities].
 *
 * @param init The lambda to configure the agent capabilities.
 * @return A new [AgentCapabilities] instance.
 */
public inline fun agentCapabilities(init: AgentCapabilitiesBuilder.() -> Unit): AgentCapabilities =
    AgentCapabilitiesBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [AgentCapabilities].
 *
 * @param init The consumer to configure the agent capabilities.
 * @return A new [AgentCapabilities] instance.
 */
public fun agentCapabilities(init: Consumer<AgentCapabilitiesBuilder>): AgentCapabilities {
    val builder = AgentCapabilitiesBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new instance of an AgentCapabilities using the provided configuration block.
 *
 * @param block A configuration block for building an AgentCapabilities instance using the AgentCapabilitiesBuilder.
 * @return A newly created AgentCapabilities instance.
 */
public fun AgentCapabilities.Companion.create(block: AgentCapabilitiesBuilder.() -> Unit): AgentCapabilities =
    AgentCapabilitiesBuilder().apply(block).build()

/**
 * Creates a new instance of an AgentCapabilities using the provided Java-friendly Consumer.
 *
 * @param block A consumer for building an AgentCapabilities instance using the AgentCapabilitiesBuilder.
 * @return A newly created AgentCapabilities instance.
 */
public fun AgentCapabilities.Companion.create(block: Consumer<AgentCapabilitiesBuilder>): AgentCapabilities {
    val builder = AgentCapabilitiesBuilder()
    block.accept(builder)
    return builder.build()
}
