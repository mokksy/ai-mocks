package me.kpavlov.mokksy

import io.ktor.server.plugins.contentnegotiation.ContentNegotiationConfig

public data class ServerConfiguration(
    val verbose: Boolean = false,
    val contextNegotiationConfigurer: (
        ContentNegotiationConfig,
    ) -> Unit = ::configureContentNegotiation,
)
