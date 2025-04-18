package me.kpavlov.aimocks.a2a.model

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
    public fun schemes(schemes: List<String>): AuthenticationInfoBuilder {
        this.schemes = schemes
        return this
    }

    /**
     * Sets the credentials for authentication.
     *
     * @param credentials The credentials string.
     * @return This builder instance for method chaining.
     */
    public fun credentials(credentials: String?): AuthenticationInfoBuilder {
        this.credentials = credentials
        return this
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
 * Creates a new AuthenticationInfo using the DSL builder.
 *
 * @param init The lambda to configure the authentication info.
 * @return A new AuthenticationInfo instance.
 */
public fun AuthenticationInfo.Companion.create(
    init: AuthenticationInfoBuilder.() -> Unit,
): AuthenticationInfo = AuthenticationInfoBuilder().apply(init).build()
