package me.kpavlov.aimocks.anthropic

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.serialization.jackson.jackson
import io.ktor.server.plugins.contentnegotiation.ContentNegotiationConfig
import kotlinx.serialization.ExperimentalSerializationApi

private lateinit var objectMapper: ObjectMapper

@OptIn(ExperimentalSerializationApi::class)
internal actual fun configureContentNegotiation(config: ContentNegotiationConfig) {
    config.jackson {
        objectMapper = this
        findAndRegisterModules()
        enable(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION)
    }
}

internal actual fun serializer(data: Any): String = objectMapper.writeValueAsString(data)
