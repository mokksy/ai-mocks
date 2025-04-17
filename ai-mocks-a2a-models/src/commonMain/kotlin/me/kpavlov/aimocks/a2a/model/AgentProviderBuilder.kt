package me.kpavlov.aimocks.a2a.model

/**
 * Builder class for creating [AgentProvider] instances.
 *
 * This builder provides a fluent API for creating AgentProvider objects,
 * making it easier to configure agent providers.
 *
 * Example usage:
 * ```kotlin
 * val provider = AgentProviderBuilder()
 *     .organization("Example Organization")
 *     .url("https://example.org")
 *     .build()
 * ```
 */
public class AgentProviderBuilder {
    public var organization: String? = null
    public var url: String? = null

    /**
     * Builds an [AgentProvider] instance with the configured parameters.
     *
     * @return A new [AgentProvider] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): AgentProvider {
        requireNotNull(organization) { "Organization is required" }

        return AgentProvider(
            organization = organization!!,
            url = url,
        )
    }
}

/**
 * Creates a new instance of an AgentProvider using the provided configuration block.
 *
 * @param block A configuration block for building an AgentProvider instance using the AgentProviderBuilder.
 * @return A newly created AgentProvider instance.
 */
public fun AgentProvider.Companion.create(block: AgentProviderBuilder.() -> Unit): AgentProvider =
    AgentProviderBuilder().apply(block).build()
