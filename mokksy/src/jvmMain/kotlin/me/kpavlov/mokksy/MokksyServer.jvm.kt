package me.kpavlov.mokksy

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.http.ContentType
import io.ktor.serialization.jackson.JacksonConverter
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiationConfig
import kotlinx.serialization.ExperimentalSerializationApi
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

@OptIn(ExperimentalSerializationApi::class)
internal actual fun configureContentNegotiation(config: ContentNegotiationConfig) {
    val converter =
        KotlinxFirstContentConverter(
            KotlinxSerializationConverter(
                Json {
                    ignoreUnknownKeys = true
                },
            ),
            createJacksonConverter {
                findAndRegisterModules()
                disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            },
        )

    config.register(ContentType.Application.Json, converter)
}

private fun createJacksonConverter(
    streamRequestBody: Boolean = true,
    block: ObjectMapper.() -> Unit = {},
): JacksonConverter {
    val mapper = ObjectMapper()
    mapper.apply {
        setDefaultPrettyPrinter(
            DefaultPrettyPrinter().apply {
                indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                indentObjectsWith(DefaultIndenter("  ", "\n"))
            },
        )
    }
    mapper.apply(block)
    mapper.registerKotlinModule()
    return JacksonConverter(mapper, streamRequestBody)
}
