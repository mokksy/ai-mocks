/*
 * SecuritySchemeBuilder.kt
 *
 * Builder for SecurityScheme according to A2A protocol version 0.3.0
 * See: https://a2a-protocol.org/latest/specification/
 */
package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder class for creating [SecurityScheme] instances.
 *
 * This builder provides a fluent API for creating various types of security schemes
 * supported by the A2A protocol, including the new mTLS and OAuth2 with metadata URL
 * features introduced in version 0.3.0.
 *
 * Example usage:
 * ```
 * val oauth2Scheme = SecurityScheme.oauth2 {
 *     metadataUrl = "https://auth.example.com/.well-known/oauth2"
 *     flows {
 *         authorizationCode {
 *             authorizationUrl = "https://auth.example.com/oauth2/authorize"
 *             tokenUrl = "https://auth.example.com/oauth2/token"
 *             scopes = mapOf("read" to "Read access", "write" to "Write access")
 *         }
 *     }
 * }
 * ```
 */
public object SecuritySchemeBuilder {
    /**
     * Creates an API Key security scheme.
     */
    public fun apiKey(
        name: String,
        location: ApiKeyLocation,
    ): ApiKeySecurityScheme = ApiKeySecurityScheme(name = name, location = location)

    /**
     * Creates an HTTP security scheme.
     */
    public fun http(
        scheme: String,
        bearerFormat: String? = null,
    ): HttpSecurityScheme = HttpSecurityScheme(scheme = scheme, bearerFormat = bearerFormat)

    /**
     * Creates a Mutual TLS security scheme.
     */
    public fun mutualTLS(description: String? = null): MutualTLSSecurityScheme =
        MutualTLSSecurityScheme(description = description)

    /**
     * Creates an OpenID Connect security scheme.
     */
    public fun openIdConnect(openIdConnectUrl: String): OpenIdConnectSecurityScheme =
        OpenIdConnectSecurityScheme(openIdConnectUrl = openIdConnectUrl)

    /**
     * Creates an OAuth2 security scheme with optional metadata URL.
     */
    public fun oauth2(
        metadataUrl: String? = null,
        flows: OAuth2Flows,
    ): OAuth2SecurityScheme = OAuth2SecurityScheme(metadataUrl = metadataUrl, flows = flows)
}

/**
 * Builder class for creating [OAuth2Flows] instances.
 */
public class OAuth2FlowsBuilder {
    public var implicit: OAuth2Flow? = null
    public var password: OAuth2Flow? = null
    public var clientCredentials: OAuth2Flow? = null
    public var authorizationCode: OAuth2Flow? = null

    /**
     * Configures the implicit OAuth2 flow.
     */
    public fun implicit(init: OAuth2FlowBuilder.() -> Unit) {
        implicit = OAuth2FlowBuilder().apply(init).build()
    }

    /**
     * Configures the password OAuth2 flow.
     */
    public fun password(init: OAuth2FlowBuilder.() -> Unit) {
        password = OAuth2FlowBuilder().apply(init).build()
    }

    /**
     * Configures the client credentials OAuth2 flow.
     */
    public fun clientCredentials(init: OAuth2FlowBuilder.() -> Unit) {
        clientCredentials = OAuth2FlowBuilder().apply(init).build()
    }

    /**
     * Configures the authorization code OAuth2 flow.
     */
    public fun authorizationCode(init: OAuth2FlowBuilder.() -> Unit) {
        authorizationCode = OAuth2FlowBuilder().apply(init).build()
    }

    public fun build(): OAuth2Flows =
        OAuth2Flows(
            implicit = implicit,
            password = password,
            clientCredentials = clientCredentials,
            authorizationCode = authorizationCode,
        )
}

/**
 * Builder class for creating [OAuth2Flow] instances.
 */
public class OAuth2FlowBuilder {
    public var authorizationUrl: String? = null
    public var tokenUrl: String? = null
    public var refreshUrl: String? = null
    public var scopes: MutableMap<String, String>? = null

    /**
     * Sets the authorization URL for the flow.
     */
    public fun authorizationUrl(url: String): OAuth2FlowBuilder =
        apply {
            this.authorizationUrl = url
        }

    /**
     * Sets the token URL for the flow.
     */
    public fun tokenUrl(url: String): OAuth2FlowBuilder =
        apply {
            this.tokenUrl = url
        }

    /**
     * Sets the refresh URL for the flow.
     */
    public fun refreshUrl(url: String): OAuth2FlowBuilder =
        apply {
            this.refreshUrl = url
        }

    /**
     * Sets the scopes for the flow.
     */
    public fun scopes(scopes: Map<String, String>): OAuth2FlowBuilder =
        apply {
            this.scopes = scopes.toMutableMap()
        }

    /**
     * Adds a scope to the flow.
     */
    public fun addScope(
        scope: String,
        description: String,
    ): OAuth2FlowBuilder =
        apply {
            if (this.scopes == null) {
                this.scopes = mutableMapOf()
            }
            requireNotNull(this.scopes)[scope] = description
        }

    public fun build(): OAuth2Flow =
        OAuth2Flow(
            authorizationUrl = authorizationUrl,
            tokenUrl = tokenUrl,
            refreshUrl = refreshUrl,
            scopes = scopes,
        )
}

/**
 * Creates an OAuth2 security scheme with DSL configuration.
 */
public fun oauth2SecurityScheme(
    metadataUrl: String? = null,
    init: OAuth2FlowsBuilder.() -> Unit,
): OAuth2SecurityScheme {
    val flows = OAuth2FlowsBuilder().apply(init).build()
    return OAuth2SecurityScheme(metadataUrl = metadataUrl, flows = flows)
}

/**
 * Creates an OAuth2 security scheme with Java-friendly Consumer.
 */
public fun oauth2SecurityScheme(
    metadataUrl: String? = null,
    init: Consumer<OAuth2FlowsBuilder>,
): OAuth2SecurityScheme {
    val flowsBuilder = OAuth2FlowsBuilder()
    init.accept(flowsBuilder)
    return OAuth2SecurityScheme(metadataUrl = metadataUrl, flows = flowsBuilder.build())
}
