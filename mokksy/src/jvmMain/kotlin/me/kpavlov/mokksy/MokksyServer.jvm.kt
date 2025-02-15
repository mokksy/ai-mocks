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
    verbose: Boolean,
    module: Application.() -> Unit,
): EmbeddedServer<ApplicationEngine, ApplicationEngine.Configuration> =
    embeddedServer(
        factory = Netty,
        host = host,
        port = port,
    ) {
        module()
        install(CallLogging) {
            if (verbose) {
                this.level = Level.DEBUG
            } else {
                this.level = Level.INFO
            }
        }
    } as EmbeddedServer<ApplicationEngine, ApplicationEngine.Configuration>
