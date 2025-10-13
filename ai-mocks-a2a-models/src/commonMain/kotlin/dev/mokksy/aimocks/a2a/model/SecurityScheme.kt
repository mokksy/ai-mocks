/*
 * SecurityScheme.kt
 *
 * Security scheme definitions for A2A protocol version 0.3.0
 * See: https://a2a-protocol.org/latest/specification/
 */
package dev.mokksy.aimocks.a2a.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Security scheme types supported by the A2A protocol.
 *
 * Defines different authentication and security mechanisms that can be used
 * for agent-to-agent communication.
 *
 * @see [A2A Protocol - Security](https://a2a-protocol.org/latest/specification/)
 */
@Serializable
public sealed interface SecurityScheme {
    public val type: String
}

/**
 * API Key authentication scheme.
 */
@Serializable
@SerialName("apiKey")
public data class ApiKeySecurityScheme(
    @SerialName("type")
    @EncodeDefault
    override val type: String = "apiKey",
    @SerialName("name")
    val name: String,
    @SerialName("in")
    val location: ApiKeyLocation,
) : SecurityScheme

/**
 * Location where API key should be placed.
 */
@Serializable
public enum class ApiKeyLocation {
    @SerialName("header")
    HEADER,

    @SerialName("query")
    QUERY,

    @SerialName("cookie")
    COOKIE,
}

/**
 * HTTP authentication scheme (Basic, Bearer, etc.).
 */
@Serializable
@SerialName("http")
public data class HttpSecurityScheme(
    @SerialName("type")
    @EncodeDefault
    override val type: String = "http",
    @SerialName("scheme")
    val scheme: String,
    @SerialName("bearerFormat")
    @EncodeDefault
    val bearerFormat: String? = null,
) : SecurityScheme

/**
 * OAuth2 authentication scheme with metadata URL support (new in 0.3.0).
 */
@Serializable
@SerialName("oauth2")
public data class OAuth2SecurityScheme(
    @SerialName("type")
    @EncodeDefault
    override val type: String = "oauth2",
    @SerialName("flows")
    val flows: OAuth2Flows,
    @SerialName("metadataUrl")
    @EncodeDefault
    val metadataUrl: String? = null,
) : SecurityScheme

/**
 * OAuth2 flow configurations.
 */
@Serializable
public data class OAuth2Flows(
    @SerialName("implicit")
    @EncodeDefault
    val implicit: OAuth2Flow? = null,
    @SerialName("password")
    @EncodeDefault
    val password: OAuth2Flow? = null,
    @SerialName("clientCredentials")
    @EncodeDefault
    val clientCredentials: OAuth2Flow? = null,
    @SerialName("authorizationCode")
    @EncodeDefault
    val authorizationCode: OAuth2Flow? = null,
)

/**
 * OAuth2 flow configuration.
 */
@Serializable
public data class OAuth2Flow(
    @SerialName("authorizationUrl")
    @EncodeDefault
    val authorizationUrl: String? = null,
    @SerialName("tokenUrl")
    @EncodeDefault
    val tokenUrl: String? = null,
    @SerialName("refreshUrl")
    @EncodeDefault
    val refreshUrl: String? = null,
    @SerialName("scopes")
    @EncodeDefault
    val scopes: Map<String, String>? = null,
)

/**
 * OpenID Connect authentication scheme.
 */
@Serializable
@SerialName("openIdConnect")
public data class OpenIdConnectSecurityScheme(
    @SerialName("type")
    @EncodeDefault
    override val type: String = "openIdConnect",
    @SerialName("openIdConnectUrl")
    val openIdConnectUrl: String,
) : SecurityScheme

/**
 * Mutual TLS authentication scheme (new in 0.3.0).
 */
@Serializable
@SerialName("mutualTLS")
public data class MutualTLSSecurityScheme(
    @SerialName("type")
    @EncodeDefault
    override val type: String = "mutualTLS",
    @SerialName("description")
    @EncodeDefault
    val description: String? = null,
) : SecurityScheme
