package me.kpavlov.mokksy

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
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
            this.level = if (configuration.verbose) Level.DEBUG else Level.INFO
        }
    }
