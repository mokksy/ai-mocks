package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder class for creating [AuthenticationInfo] instances.
 *
 * This builder provides a fluent API for constructing AuthenticationInfo objects,
 * making it easier to configure complex settings programmatically.
 */
public class AuthenticationInfoBuilder {
    public var schemes: List<String> = mutableListOf()
    public var credentials: String? = null

    /**
     * Sets the schemes for authentication.
     *
     * @param schemes The list of authentication schemes.
     * @return This builder instance for method chaining.
     */
    public fun schemes(schemes: List<String>): AuthenticationInfoBuilder =
        apply {
            this.schemes = schemes
        }

    /**
     * Sets the credentials for authentication.
     *
     * @param credentials The credentials string.
     * @return This builder instance for method chaining.
     */
    public fun credentials(credentials: String?): AuthenticationInfoBuilder =
        apply {
            this.credentials = credentials
        }

    /**
     * Builds an [AuthenticationInfo] instance with the configured parameters.
     *
     * @return A new [AuthenticationInfo] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): AuthenticationInfo {
        require(schemes.isNotEmpty()) { "AuthenticationInfo requires at least one scheme" }
        return AuthenticationInfo(
            schemes = schemes,
            credentials = credentials,
        )
    }
}

/**
 * Top-level DSL function for creating [AuthenticationInfo].
 *
 * @param init The lambda to configure the authentication info.
 * @return A new [AuthenticationInfo] instance.
 */
public inline fun authenticationInfo(
    init: AuthenticationInfoBuilder.() -> Unit,
): AuthenticationInfo = AuthenticationInfoBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [AuthenticationInfo].
 *
 * @param init The consumer to configure the authentication info.
 * @return A new [AuthenticationInfo] instance.
 */
public fun authenticationInfo(init: Consumer<AuthenticationInfoBuilder>): AuthenticationInfo {
    val builder = AuthenticationInfoBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new AuthenticationInfo using the DSL builder.
 *
 * @param init The lambda to configure the authentication info.
 * @return A new AuthenticationInfo instance.
 */
public fun AuthenticationInfo.Companion.create(
    init: AuthenticationInfoBuilder.() -> Unit,
): AuthenticationInfo = AuthenticationInfoBuilder().apply(init).build()

/**
 * Creates a new AuthenticationInfo using the provided Java-friendly Consumer.
 *
 * @param init A consumer for building an AuthenticationInfo instance using the AuthenticationInfoBuilder.
 * @return A newly created AuthenticationInfo instance.
 */
public fun AuthenticationInfo.Companion.create(
    init: Consumer<AuthenticationInfoBuilder>,
): AuthenticationInfo {
    val builder = AuthenticationInfoBuilder()
    init.accept(builder)
    return builder.build()
}
