package dev.mokksy.aimocks.ollama.ktor

import dev.mokksy.aimocks.ollama.AbstractMockOllamaTest
import io.ktor.client.HttpClient
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Base class for Ktor-based integration tests.
 *
 * This class provides a configured Ktor HttpClient with content negotiation and logging,
 * which can be used by all test classes to avoid duplication.
 */
internal abstract class AbstractKtorTest : AbstractMockOllamaTest() {
    /**
     * Shared Ktor HttpClient instance configured with:
     * - ContentNegotiation for JSON serialization/deserialization
     * - Logging for request/response logging
     */
    protected val client =
        HttpClient {
            // Install ContentNegotiation plugin for JSON serialization/deserialization
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    },
                )
            }
            // Install Logging plugin for request/response logging
            install(Logging) {
                level = LogLevel.ALL
            }
            install(UserAgent) {
                agent = "Ktor tests"
            }
        }
}
