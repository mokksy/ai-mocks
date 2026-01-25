package dev.mokksy.mokksy

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.Level
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer

private val logger = KotlinLogging.logger {}

internal actual fun createEmbeddedServer(
    host: String,
    port: Int,
    configuration: ServerConfiguration,
    module: Application.() -> Unit,
): EmbeddedServer<out ApplicationEngine, out ApplicationEngine.Configuration> =
    embeddedServer(
        factory = CIO,
        host = host,
        port = port,
    ) {
        // CallLogging is JVM-only; use simple interceptor for native
        if (configuration.verbose) {
            intercept(ApplicationCallPipeline.Monitoring) {
                val request = call.request
                logger.at(Level.DEBUG) {
                    message = "${request.local.method.value} ${request.local.uri}"
                }
            }
        }
        module()
    }
