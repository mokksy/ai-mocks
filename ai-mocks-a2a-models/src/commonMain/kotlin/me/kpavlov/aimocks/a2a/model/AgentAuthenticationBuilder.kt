package me.kpavlov.aimocks.a2a.model

/**
 * Builder class for creating [AgentAuthentication] instances.
 *
 * This builder provides a fluent API for creating AgentAuthentication objects,
 * making it easier to construct complex authentication configurations.
 *
 * Example usage:
 * ```kotlin
 * val auth = AgentAuthenticationBuilder()
 *     .schemes(listOf("oauth2", "api_key"))
 *     .credentials("some-credentials-info")
 *     .create()
 * ```
 */
public class AgentAuthenticationBuilder {
    public var schemes: List<String> = emptyList()
    public var credentials: String? = null

    /**
     * Builds an [AgentAuthentication] instance with the configured parameters.
     *
     * @return A new [AgentAuthentication] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): AgentAuthentication {
        require(schemes.isNotEmpty()) { "At least one authentication scheme is required" }

        return AgentAuthentication(
            schemes = schemes,
            credentials = credentials,
        )
    }
}

/**
 * Creates a new instance of an AgentAuthentication using the provided configuration block.
 *
 * @param block A configuration block for building an AgentAuthentication instance using the AgentAuthenticationBuilder.
 * @return A newly created AgentAuthentication instance.
 */
public fun AgentAuthentication.Companion.create(
    block: AgentAuthenticationBuilder.() -> Unit,
): AgentAuthentication = AgentAuthenticationBuilder().apply(block).build()
