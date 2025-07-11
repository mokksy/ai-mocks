package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

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
     * Sets the authentication schemes.
     *
     * @param schemes The list of authentication schemes.
     * @return This builder instance for method chaining.
     */
    public fun schemes(schemes: List<String>): AgentAuthenticationBuilder =
        apply {
            this.schemes = schemes
        }

    /**
     * Sets the credentials.
     *
     * @param credentials The credentials string.
     * @return This builder instance for method chaining.
     */
    public fun credentials(credentials: String): AgentAuthenticationBuilder =
        apply {
            this.credentials = credentials
        }

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
 * Top-level DSL function for creating [AgentAuthentication].
 *
 * @param init The lambda to configure the agent authentication.
 * @return A new [AgentAuthentication] instance.
 */
public inline fun agentAuthentication(
    init: AgentAuthenticationBuilder.() -> Unit,
): AgentAuthentication = AgentAuthenticationBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [AgentAuthentication].
 *
 * @param init The consumer to configure the agent authentication.
 * @return A new [AgentAuthentication] instance.
 */
public fun agentAuthentication(init: Consumer<AgentAuthenticationBuilder>): AgentAuthentication {
    val builder = AgentAuthenticationBuilder()
    init.accept(builder)
    return builder.build()
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

/**
 * Creates a new instance of an AgentAuthentication using the provided Java-friendly Consumer.
 *
 * @param block A consumer for building an AgentAuthentication instance using the AgentAuthenticationBuilder.
 * @return A newly created AgentAuthentication instance.
 */
public fun AgentAuthentication.Companion.create(
    block: Consumer<AgentAuthenticationBuilder>,
): AgentAuthentication {
    val builder = AgentAuthenticationBuilder()
    block.accept(builder)
    return builder.build()
}
