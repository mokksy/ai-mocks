package dev.mokksy.mokksy

import io.ktor.server.plugins.contentnegotiation.ContentNegotiationConfig

public data class ServerConfiguration(
    val verbose: Boolean = false,
    val name: String? = "Mokksy",
    val contentNegotiationConfigurer: (
        ContentNegotiationConfig,
    ) -> Unit = ::configureContentNegotiation,
)
