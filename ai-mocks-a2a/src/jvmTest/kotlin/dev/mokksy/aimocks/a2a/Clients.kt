package dev.mokksy.aimocks.a2a

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.sse.SSE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Creates a Ktor `HttpClient` configured to work with Server-Sent Events (SSE).
 *
 * @param url The base URL for the `HttpClient`.
 * @return A configured instance of `HttpClient` with JSON serialization, SSE support,
 *         and a default request base URL pointing to the specified port.
 */
internal fun createA2AClient(url: String): HttpClient =
    HttpClient(Java) {
        val json =
            Json {
                prettyPrint = true
                isLenient = true
            }
        install(ContentNegotiation) {
            json(json)
        }
        install(SSE) {
            showRetryEvents()
            showCommentEvents()
        }
        install(DefaultRequest) {
            url(url) // Set the base URL
        }
    }
