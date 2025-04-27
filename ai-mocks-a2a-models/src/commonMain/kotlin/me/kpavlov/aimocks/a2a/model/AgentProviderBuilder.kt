package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

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
 *     .create()
 * ```
 */
public class AgentProviderBuilder {
    public var organization: String? = null
    public var url: String? = null

    /**
     * Sets the organization of the agent provider.
     *
     * @param organization The organization of the agent provider.
     * @return This builder instance for method chaining.
     */
    public fun organization(organization: String): AgentProviderBuilder =
        apply {
            this.organization = organization
        }

    /**
     * Sets the URL of the agent provider.
     *
     * @param url The URL of the agent provider.
     * @return This builder instance for method chaining.
     */
    public fun url(url: String): AgentProviderBuilder =
        apply {
            this.url = url
        }

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
 * Top-level DSL function for creating [AgentProvider].
 *
 * @param init The lambda to configure the agent provider.
 * @return A new [AgentProvider] instance.
 */
public inline fun agentProvider(init: AgentProviderBuilder.() -> Unit): AgentProvider =
    AgentProviderBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [AgentProvider].
 *
 * @param init The consumer to configure the agent provider.
 * @return A new [AgentProvider] instance.
 */
public fun agentProvider(init: Consumer<AgentProviderBuilder>): AgentProvider {
    val builder = AgentProviderBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new instance of an AgentProvider using the provided configuration block.
 *
 * @param block A configuration block for building an AgentProvider instance using the AgentProviderBuilder.
 * @return A newly created AgentProvider instance.
 */
public fun AgentProvider.Companion.create(block: AgentProviderBuilder.() -> Unit): AgentProvider =
    AgentProviderBuilder().apply(block).build()

/**
 * Creates a new instance of an AgentProvider using the provided Java-friendly Consumer.
 *
 * @param block A consumer for building an AgentProvider instance using the AgentProviderBuilder.
 * @return A newly created AgentProvider instance.
 */
public fun AgentProvider.Companion.create(block: Consumer<AgentProviderBuilder>): AgentProvider {
    val builder = AgentProviderBuilder()
    block.accept(builder)
    return builder.build()
}
