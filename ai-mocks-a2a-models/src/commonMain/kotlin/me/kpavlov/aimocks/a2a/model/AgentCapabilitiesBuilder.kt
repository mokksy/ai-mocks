package me.kpavlov.aimocks.a2a.model

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
 *     .build()
 * ```
 */
public class AgentCapabilitiesBuilder {
    public var streaming: Boolean = false
    public var pushNotifications: Boolean = false
    public var stateTransitionHistory: Boolean = false

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
 * Creates a new instance of an AgentCapabilities using the provided configuration block.
 *
 * @param block A configuration block for building an AgentCapabilities instance using the AgentCapabilitiesBuilder.
 * @return A newly created AgentCapabilities instance.
 */
public fun AgentCapabilities.Companion.create(
    block: AgentCapabilitiesBuilder.() -> Unit,
): AgentCapabilities = AgentCapabilitiesBuilder().apply(block).build()
