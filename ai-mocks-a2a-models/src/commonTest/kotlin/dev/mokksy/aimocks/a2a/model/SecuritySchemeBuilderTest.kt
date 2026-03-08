package dev.mokksy.aimocks.a2a.model

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

internal class SecuritySchemeBuilderTest {
    @Test
    fun `should create API key security scheme with header location`() {
        // when
        val scheme =
            SecuritySchemeBuilder.apiKey(
                name = "X-API-Key",
                location = ApiKeyLocation.HEADER,
            )

        // then
        assertSoftly(scheme) {
            shouldBeInstanceOf<ApiKeySecurityScheme>()
            type shouldBe "apiKey"
            name shouldBe "X-API-Key"
            location shouldBe ApiKeyLocation.HEADER
        }
    }

    @Test
    fun `should create API key security scheme with query location`() {
        // when
        val scheme =
            SecuritySchemeBuilder.apiKey(
                name = "api_key",
                location = ApiKeyLocation.QUERY,
            )

        // then
        assertSoftly(scheme) {
            shouldBeInstanceOf<ApiKeySecurityScheme>()
            type shouldBe "apiKey"
            name shouldBe "api_key"
            location shouldBe ApiKeyLocation.QUERY
        }
    }

    @Test
    fun `should create API key security scheme with cookie location`() {
        // when
        val scheme =
            SecuritySchemeBuilder.apiKey(
                name = "session",
                location = ApiKeyLocation.COOKIE,
            )

        // then
        assertSoftly(scheme) {
            shouldBeInstanceOf<ApiKeySecurityScheme>()
            type shouldBe "apiKey"
            name shouldBe "session"
            location shouldBe ApiKeyLocation.COOKIE
        }
    }

    @Test
    fun `should create HTTP security scheme with basic auth`() {
        // when
        val scheme = SecuritySchemeBuilder.http(scheme = "basic")

        // then
        assertSoftly(scheme) {
            shouldBeInstanceOf<HttpSecurityScheme>()
            type shouldBe "http"
            this.scheme shouldBe "basic"
            bearerFormat shouldBe null
        }
    }

    @Test
    fun `should create HTTP security scheme with bearer auth and format`() {
        // when
        val scheme =
            SecuritySchemeBuilder.http(
                scheme = "bearer",
                bearerFormat = "JWT",
            )

        // then
        assertSoftly(scheme) {
            shouldBeInstanceOf<HttpSecurityScheme>()
            type shouldBe "http"
            this.scheme shouldBe "bearer"
            bearerFormat shouldBe "JWT"
        }
    }

    @Test
    fun `should create HTTP security scheme with bearer auth without format`() {
        // when
        val scheme = SecuritySchemeBuilder.http(scheme = "bearer")

        // then
        assertSoftly(scheme) {
            shouldBeInstanceOf<HttpSecurityScheme>()
            type shouldBe "http"
            this.scheme shouldBe "bearer"
            bearerFormat shouldBe null
        }
    }

    @Test
    fun `should create Mutual TLS security scheme without description`() {
        // when
        val scheme = SecuritySchemeBuilder.mutualTLS()

        // then
        assertSoftly(scheme) {
            shouldBeInstanceOf<MutualTLSSecurityScheme>()
            type shouldBe "mutualTLS"
            description shouldBe null
        }
    }

    @Test
    fun `should create Mutual TLS security scheme with description`() {
        // given
        val desc = "Client certificate authentication"

        // when
        val scheme = SecuritySchemeBuilder.mutualTLS(description = desc)

        // then
        assertSoftly(scheme) {
            shouldBeInstanceOf<MutualTLSSecurityScheme>()
            type shouldBe "mutualTLS"
            description shouldBe desc
        }
    }

    @Test
    fun `should create OpenID Connect security scheme`() {
        // given
        val url = "https://auth.example.com/.well-known/openid-configuration"

        // when
        val scheme = SecuritySchemeBuilder.openIdConnect(openIdConnectUrl = url)

        // then
        assertSoftly(scheme) {
            shouldBeInstanceOf<OpenIdConnectSecurityScheme>()
            type shouldBe "openIdConnect"
            openIdConnectUrl shouldBe url
        }
    }

    @Test
    fun `should create OAuth2 scheme without metadata URL`() {
        // given
        val flows =
            OAuth2Flows(
                implicit =
                    OAuth2Flow(
                        authorizationUrl = "https://auth.example.com/authorize",
                        scopes = mapOf("read" to "Read access"),
                    ),
            )

        // when
        val scheme = SecuritySchemeBuilder.oauth2(flows = flows)

        // then
        assertSoftly(scheme) {
            shouldBeInstanceOf<OAuth2SecurityScheme>()
            type shouldBe "oauth2"
            metadataUrl shouldBe null
            this.flows shouldBe flows
        }
    }

    @Test
    fun `should create OAuth2 scheme with metadata URL`() {
        // given
        val metadataUrl = "https://auth.example.com/.well-known/oauth2"
        val flows =
            OAuth2Flows(
                authorizationCode =
                    OAuth2Flow(
                        authorizationUrl = "https://auth.example.com/oauth2/authorize",
                        tokenUrl = "https://auth.example.com/oauth2/token",
                        scopes =
                            mapOf(
                                "read" to "Read access",
                                "write" to "Write access",
                            ),
                    ),
            )

        // when
        val scheme = SecuritySchemeBuilder.oauth2(metadataUrl = metadataUrl, flows = flows)

        // then
        assertSoftly(scheme) {
            shouldBeInstanceOf<OAuth2SecurityScheme>()
            type shouldBe "oauth2"
            this.metadataUrl shouldBe metadataUrl
            this.flows shouldBe flows
        }
    }

    @Test
    fun `should build implicit OAuth2 flow with DSL`() {
        // when
        val builder = OAuth2FlowsBuilder()
        builder.implicit {
            authorizationUrl = "https://auth.example.com/authorize"
            scopes = mapOf("read" to "Read access", "write" to "Write access")
        }
        val flows = builder.build()

        // then
        assertSoftly(flows) {
            implicit shouldBe
                OAuth2Flow(
                    authorizationUrl = "https://auth.example.com/authorize",
                    scopes = mapOf("read" to "Read access", "write" to "Write access"),
                )
            password shouldBe null
            clientCredentials shouldBe null
            authorizationCode shouldBe null
        }
    }

    @Test
    fun `should build password OAuth2 flow with DSL`() {
        // when
        val builder = OAuth2FlowsBuilder()
        builder.password {
            tokenUrl = "https://auth.example.com/token"
            scopes = mapOf("admin" to "Admin access")
        }
        val flows = builder.build()

        // then
        assertSoftly(flows) {
            implicit shouldBe null
            password shouldBe
                OAuth2Flow(
                    tokenUrl = "https://auth.example.com/token",
                    scopes = mapOf("admin" to "Admin access"),
                )
            clientCredentials shouldBe null
            authorizationCode shouldBe null
        }
    }

    @Test
    fun `should build client credentials OAuth2 flow with DSL`() {
        // when
        val builder = OAuth2FlowsBuilder()
        builder.clientCredentials {
            tokenUrl = "https://auth.example.com/token"
            scopes = mapOf("api" to "API access")
        }
        val flows = builder.build()

        // then
        assertSoftly(flows) {
            implicit shouldBe null
            password shouldBe null
            clientCredentials shouldBe
                OAuth2Flow(
                    tokenUrl = "https://auth.example.com/token",
                    scopes = mapOf("api" to "API access"),
                )
            authorizationCode shouldBe null
        }
    }

    @Test
    fun `should build authorization code OAuth2 flow with DSL`() {
        // when
        val builder = OAuth2FlowsBuilder()
        builder.authorizationCode {
            authorizationUrl = "https://auth.example.com/authorize"
            tokenUrl = "https://auth.example.com/token"
            refreshUrl = "https://auth.example.com/refresh"
            scopes = mapOf("read" to "Read access", "write" to "Write access")
        }
        val flows = builder.build()

        // then
        assertSoftly(flows) {
            implicit shouldBe null
            password shouldBe null
            clientCredentials shouldBe null
            authorizationCode shouldBe
                OAuth2Flow(
                    authorizationUrl = "https://auth.example.com/authorize",
                    tokenUrl = "https://auth.example.com/token",
                    refreshUrl = "https://auth.example.com/refresh",
                    scopes = mapOf("read" to "Read access", "write" to "Write access"),
                )
        }
    }

    @Test
    fun `should build multiple OAuth2 flows with DSL`() {
        // when
        val builder = OAuth2FlowsBuilder()
        builder.implicit {
            authorizationUrl = "https://auth.example.com/authorize"
            scopes = mapOf("read" to "Read access")
        }
        builder.authorizationCode {
            authorizationUrl = "https://auth.example.com/authorize"
            tokenUrl = "https://auth.example.com/token"
            scopes = mapOf("read" to "Read access", "write" to "Write access")
        }
        val flows = builder.build()

        // then
        assertSoftly(flows) {
            implicit shouldBe
                OAuth2Flow(
                    authorizationUrl = "https://auth.example.com/authorize",
                    scopes = mapOf("read" to "Read access"),
                )
            password shouldBe null
            clientCredentials shouldBe null
            authorizationCode shouldBe
                OAuth2Flow(
                    authorizationUrl = "https://auth.example.com/authorize",
                    tokenUrl = "https://auth.example.com/token",
                    scopes = mapOf("read" to "Read access", "write" to "Write access"),
                )
        }
    }

    @Test
    fun `should build OAuth2 flow with fluent API using authorizationUrl method`() {
        // when
        val flow =
            OAuth2FlowBuilder().authorizationUrl("https://auth.example.com/authorize").build()

        // then
        assertSoftly(flow) {
            authorizationUrl shouldBe "https://auth.example.com/authorize"
            tokenUrl shouldBe null
            refreshUrl shouldBe null
            scopes shouldBe null
        }
    }

    @Test
    fun `should build OAuth2 flow with fluent API using tokenUrl method`() {
        // when
        val flow = OAuth2FlowBuilder().tokenUrl("https://auth.example.com/token").build()

        // then
        assertSoftly(flow) {
            authorizationUrl shouldBe null
            tokenUrl shouldBe "https://auth.example.com/token"
            refreshUrl shouldBe null
            scopes shouldBe null
        }
    }

    @Test
    fun `should build OAuth2 flow with fluent API using refreshUrl method`() {
        // when
        val flow = OAuth2FlowBuilder().refreshUrl("https://auth.example.com/refresh").build()

        // then
        assertSoftly(flow) {
            authorizationUrl shouldBe null
            tokenUrl shouldBe null
            refreshUrl shouldBe "https://auth.example.com/refresh"
            scopes shouldBe null
        }
    }

    @Test
    fun `should build OAuth2 flow with fluent API using scopes method`() {
        // given
        val scopesMap = mapOf("read" to "Read access", "write" to "Write access")

        // when
        val flow = OAuth2FlowBuilder().scopes(scopesMap).build()

        // then
        assertSoftly(flow) {
            authorizationUrl shouldBe null
            tokenUrl shouldBe null
            refreshUrl shouldBe null
            scopes shouldBe scopesMap
        }
    }

    @Test
    fun `should build OAuth2 flow with fluent API using addScope method`() {
        // when
        val flow = OAuth2FlowBuilder().addScope("read", "Read access").build()

        // then
        assertSoftly(flow) {
            authorizationUrl shouldBe null
            tokenUrl shouldBe null
            refreshUrl shouldBe null
            scopes shouldBe mapOf("read" to "Read access")
        }
    }

    @Test
    fun `should build OAuth2 flow with fluent API using multiple addScope calls`() {
        // when
        val flow =
            OAuth2FlowBuilder()
                .addScope("read", "Read access")
                .addScope("write", "Write access")
                .addScope("admin", "Admin access")
                .build()

        // then
        assertSoftly(flow) {
            authorizationUrl shouldBe null
            tokenUrl shouldBe null
            refreshUrl shouldBe null
            scopes shouldBe
                mapOf(
                    "read" to "Read access",
                    "write" to "Write access",
                    "admin" to "Admin access",
                )
        }
    }

    @Test
    fun `should build complete OAuth2 flow with all fluent API methods chained`() {
        // when
        val flow =
            OAuth2FlowBuilder()
                .authorizationUrl("https://auth.example.com/authorize")
                .tokenUrl("https://auth.example.com/token")
                .refreshUrl("https://auth.example.com/refresh")
                .addScope("read", "Read access")
                .addScope("write", "Write access")
                .build()

        // then
        assertSoftly(flow) {
            authorizationUrl shouldBe "https://auth.example.com/authorize"
            tokenUrl shouldBe "https://auth.example.com/token"
            refreshUrl shouldBe "https://auth.example.com/refresh"
            scopes shouldBe mapOf("read" to "Read access", "write" to "Write access")
        }
    }

    @Test
    fun `should create OAuth2 scheme using oauth2SecurityScheme DSL function`() {
        // when
        val scheme =
            oauth2SecurityScheme(metadataUrl = "https://auth.example.com/.well-known/oauth2") {
                authorizationCode {
                    authorizationUrl = "https://auth.example.com/oauth2/authorize"
                    tokenUrl = "https://auth.example.com/oauth2/token"
                    scopes = mapOf("read" to "Read access", "write" to "Write access")
                }
            }

        // then
        assertSoftly(scheme) {
            shouldBeInstanceOf<OAuth2SecurityScheme>()
            type shouldBe "oauth2"
            metadataUrl shouldBe "https://auth.example.com/.well-known/oauth2"
            flows.authorizationCode shouldBe
                OAuth2Flow(
                    authorizationUrl = "https://auth.example.com/oauth2/authorize",
                    tokenUrl = "https://auth.example.com/oauth2/token",
                    scopes = mapOf("read" to "Read access", "write" to "Write access"),
                )
        }
    }

    @Test
    fun `should create OAuth2 scheme using oauth2SecurityScheme DSL without metadata URL`() {
        // when
        val scheme =
            oauth2SecurityScheme {
                implicit {
                    authorizationUrl = "https://auth.example.com/authorize"
                    scopes = mapOf("read" to "Read access")
                }
            }

        // then
        assertSoftly(scheme) {
            shouldBeInstanceOf<OAuth2SecurityScheme>()
            type shouldBe "oauth2"
            metadataUrl shouldBe null
            flows.implicit shouldBe
                OAuth2Flow(
                    authorizationUrl = "https://auth.example.com/authorize",
                    scopes = mapOf("read" to "Read access"),
                )
        }
    }

    @Test
    fun `should override scopes when using both scopes and addScope methods`() {
        // when
        val flow =
            OAuth2FlowBuilder()
                .scopes(mapOf("read" to "Read access"))
                .addScope("write", "Write access")
                .build()

        // then
        assertSoftly(flow) {
            scopes shouldBe mapOf("read" to "Read access", "write" to "Write access")
        }
    }

    @Test
    fun `should build empty OAuth2Flows when no flows are configured`() {
        // when
        val flows = OAuth2FlowsBuilder().build()

        // then
        assertSoftly(flows) {
            implicit shouldBe null
            password shouldBe null
            clientCredentials shouldBe null
            authorizationCode shouldBe null
        }
    }

    @Test
    fun `should build OAuth2 flow with direct property assignment`() {
        // when
        val builder = OAuth2FlowBuilder()
        builder.authorizationUrl = "https://auth.example.com/authorize"
        builder.tokenUrl = "https://auth.example.com/token"
        builder.refreshUrl = "https://auth.example.com/refresh"
        builder.scopes = mapOf("read" to "Read access")
        val flow = builder.build()

        // then
        assertSoftly(flow) {
            authorizationUrl shouldBe "https://auth.example.com/authorize"
            tokenUrl shouldBe "https://auth.example.com/token"
            refreshUrl shouldBe "https://auth.example.com/refresh"
            scopes shouldBe mapOf("read" to "Read access")
        }
    }
}
