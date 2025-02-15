package me.kpavlov.mokksy

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.sse.SSE
import kotlinx.serialization.json.Json

/**
 * Creates a Ktor `HttpClient` configured to work with Server-Sent Events (SSE).
 *
 * @param port The port number to configure the base URL for the `HttpClient`.
 * @return A configured instance of `HttpClient` with JSON serialization, SSE support,
 *         and a default request base URL pointing to the specified port.
 */
internal fun createKtorSSEClient(port: Int): HttpClient =
    HttpClient(Java) {
        install(ContentNegotiation) {
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        }
        install(SSE) {
            showRetryEvents()
            showCommentEvents()
        }
        install(DefaultRequest) {
            url("http://127.0.0.1:$port") // Set the base URL
        }
    }

/**
 * Creates and configures a Ktor HTTP client using the specified port to set the base URL.
 * The client leverages the `Java` engine and installs plugins such as `ContentNegotiation` for JSON handling
 * and `DefaultRequest` for setting default request parameters.
 *
 * @param port The server port number used to set the base URL for the client.
 * @return A configured instance of [HttpClient].
 */
internal fun createKtorClient(port: Int): HttpClient =
    HttpClient(Java) {
        install(ContentNegotiation) {
            Json {
                // Configure JSON serialization
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        }
        install(DefaultRequest) {
            url("http://127.0.0.1:$port") // Set the base URL
        }
    }
