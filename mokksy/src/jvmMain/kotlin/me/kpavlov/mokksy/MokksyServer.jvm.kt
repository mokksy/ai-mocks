package me.kpavlov.mokksy

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.serialization.jackson.jackson
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiationConfig
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

internal actual fun createEmbeddedServer(
    host: String,
    port: Int,
    configuration: ServerConfiguration,
    module: Application.() -> Unit,
): EmbeddedServer<
    out ApplicationEngine,
    out ApplicationEngine.Configuration,
> =
    embeddedServer(
        factory = Netty,
        host = host,
        port = port,
    ) {
        module()
        install(CallLogging) {
            level = if (configuration.verbose) Level.DEBUG else Level.INFO
        }
    }

internal actual fun configureContentNegotiation(config: ContentNegotiationConfig) {
    config.jackson {
        findAndRegisterModules()
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }
    config.json(
        Json {
            ignoreUnknownKeys = true
        },
    )
}
