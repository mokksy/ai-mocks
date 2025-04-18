package me.kpavlov.a2a.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.sse.SSE
import kotlinx.serialization.json.Json

/**
 * Creates a Ktor `HttpClient` configured to work with Server-Sent Events (SSE).
 *
 * @param url The base URL for the `HttpClient`.
 * @return A configured instance of `HttpClient` with JSON serialization, SSE support,
 *         and a default request base URL pointing to the specified port.
 */
@Deprecated("Use A2AClientFactory.create() instead")
internal fun createA2AClient(url: String): HttpClient =
    HttpClient(Java) {
        install(ContentNegotiation) {
            Json {
                prettyPrint = true
                isLenient = true
            }
        }
        install(SSE) {
            showRetryEvents()
            showCommentEvents()
        }
        install(DefaultRequest) {
            url(url) // Set the base URL
        }
    }
