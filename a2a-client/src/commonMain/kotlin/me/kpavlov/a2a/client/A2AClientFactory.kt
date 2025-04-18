package me.kpavlov.a2a.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.sse.SSE
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Factory for creating instances of the A2AClient.
 */
public object A2AClientFactory {
    /**
     * Creates a new instance of the A2AClient.
     *
     * @param baseUrl The base URL of the A2A server.
     * @param httpClient An optional HttpClient to use. If not provided, a new one will be created.
     * @param json An optional Json serializer/deserializer to use. If not provided, a new one will be created.
     * @return A new instance of the A2AClient.
     */
    public fun create(
        baseUrl: String,
        httpClient: HttpClient? = null,
        json: Json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        }
    ): A2AClient {
        val client = httpClient ?: HttpClient {
            install(ContentNegotiation) {
                json(json)
            }
            install(SSE) {
                // Configure SSE client
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
                sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }
            defaultRequest {
                url(baseUrl)
                headers.append(HttpHeaders.UserAgent, "mokksy-a2a-client")
            }
        }

        return DefaultA2AClient(
            httpClient = client,
            baseUrl = baseUrl,
            json = json
        )
    }
}
