package dev.mokksy.aimocks.ollama

import dev.mokksy.aimocks.core.AbstractMockLlm
import dev.mokksy.aimocks.ollama.chat.ChatRequest
import dev.mokksy.aimocks.ollama.chat.OllamaChatBuildingStep
import dev.mokksy.aimocks.ollama.chat.OllamaChatRequestSpecification
import dev.mokksy.aimocks.ollama.embed.EmbeddingsRequest
import dev.mokksy.aimocks.ollama.embed.OllamaEmbedBuildingStep
import dev.mokksy.aimocks.ollama.embed.OllamaEmbedRequestSpecification
import dev.mokksy.aimocks.ollama.generate.GenerateRequest
import dev.mokksy.aimocks.ollama.generate.OllamaGenerateBuildingStep
import dev.mokksy.aimocks.ollama.generate.OllamaGenerateRequestSpecification
import dev.mokksy.mokksy.ServerConfiguration
import io.kotest.assertions.json.containJsonKeyValue
import io.kotest.matchers.string.contain
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Mock implementation of an Ollama-compatible service for testing purposes.
 *
 * This class provides an HTTP mock server to simulate Ollama APIs, specifically for generate
 * completions, chat completions, and other Ollama endpoints. It is designed to mimic the behavior
 * of the Ollama APIs locally and facilitate integration testing and development.
 *
 * @param port The port on which the mock server will run. Defaults to 0, which allows the server to select
 *             an available port.
 * @param verbose Controls whether the mock server's operations are logged in detail. Defaults to true.
 * @author Konstantin Pavlov
 */
public open class MockOllama(
    port: Int = 0,
    verbose: Boolean = true,
) : AbstractMockLlm(
        port = port,
        configuration =
            ServerConfiguration(
                verbose = verbose,
                name = "MockOllama",
            ) { config ->
                config.json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                    },
                )
            },
    ) {

    /**
     * Sets up a mock handler for the Ollama `/api/generate` completion endpoint.
     *
     * Allows configuration of request matching criteria such as model, prompt, etc.
     * Returns a builder step for specifying the mock response.
     *
     * @param name Optional identifier for the mock endpoint.
     * @param block Configuration block for defining request matching rules.
     * @return A builder step for configuring the mock response.
     */
    public fun generate(
        name: String? = null,
        block: OllamaGenerateRequestSpecification.() -> Unit,
    ): OllamaGenerateBuildingStep {
        val requestStep =
            mokksy.post(
                name = name,
                requestType = GenerateRequest::class,
            ) {
                val generateRequestSpec = OllamaGenerateRequestSpecification()
                block(generateRequestSpec)

                path("/api/generate")

                body += generateRequestSpec.requestBody

                generateRequestSpec.model?.let {
                    bodyString += containJsonKeyValue("model", it)
                }

                generateRequestSpec.template?.let {
                    bodyString += containJsonKeyValue("template", it)
                }

                generateRequestSpec.stream?.let {
                    bodyString += containJsonKeyValue("stream", it)
                }

                body += generateRequestSpec.requestBody
                bodyString += generateRequestSpec.requestBodyString
            }

        return OllamaGenerateBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    /**
     * Sets up a mock handler for the Ollama chat completion (`/api/chat`) endpoint.
     *
     * Allows configuration of request matching criteria such as model, stream, seed, and custom request body content.
     *
     * @param name An optional identifier for the mock endpoint.
     * @param block A configuration block to specify request matching details.
     * @return A builder step for defining the mock response.
     */
    public fun chat(
        name: String? = null,
        block: OllamaChatRequestSpecification.() -> Unit,
    ): OllamaChatBuildingStep {
        val requestStep =
            mokksy.post(
                name = name,
                requestType = ChatRequest::class,
            ) {
                val chatRequestSpec = OllamaChatRequestSpecification()
                block(chatRequestSpec)

                path("/api/chat")

                chatRequestSpec.model?.let {
                    bodyString += containJsonKeyValue("model", it)
                }

                chatRequestSpec.stream?.let {
                    bodyString += containJsonKeyValue("stream", it)
                }

                chatRequestSpec.seed?.let {
                    bodyString += containJsonKeyValue("options.seed", it)
                }

                chatRequestSpec.temperature?.let {
                    bodyString += containJsonKeyValue("options.temperature", it)
                }

                chatRequestSpec.topP?.let {
                    bodyString += containJsonKeyValue("options.top_p", it)
                }

                chatRequestSpec.topK?.let {
                    bodyString += containJsonKeyValue("options.top_k", it)
                }

                body += chatRequestSpec.requestBody
                bodyString += chatRequestSpec.requestBodyString
            }

        return OllamaChatBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    /**
     * Sets up a mock handler for the Ollama `/api/embed` endpoint,
     * allowing configuration of request matching for embedding requests.
     *
     * Supports matching on model, input (string or list), truncate, keep_alive, and options fields in the request body.
     *
     * @param name Optional identifier for the mock configuration.
     * @param block Lambda to configure the request matching criteria.
     * @return A builder step for specifying the mock response to embedding requests.
     */
    public fun embed(
        name: String? = null,
        block: OllamaEmbedRequestSpecification.() -> Unit,
    ): OllamaEmbedBuildingStep {
        val requestStep =
            mokksy.post(
                name = name,
                requestType = EmbeddingsRequest::class,
            ) {
                val embedRequestSpec = OllamaEmbedRequestSpecification()
                block(embedRequestSpec)

                path("/api/embed")

                embedRequestSpec.model?.let {
                    bodyString += containJsonKeyValue("model", it)
                }

                // Handle string input
                embedRequestSpec.stringInput?.let {
                    bodyString += containJsonKeyValue("input", it)
                }

                // Handle string list input
                embedRequestSpec.stringListInput?.let {
                    // For list inputs, we can't use containJsonKeyValue directly
                    // Instead, we'll check that the request body contains the input values
                    it.forEach { inputValue ->
                        bodyString += contain(inputValue)
                    }
                }

                embedRequestSpec.truncate?.let {
                    bodyString += containJsonKeyValue("truncate", it)
                }

                embedRequestSpec.options?.let { options ->
                    options.forEach { (key, value) ->
                        bodyString += containJsonKeyValue("options.$key", value)
                    }
                }

                embedRequestSpec.keepAlive?.let {
                    bodyString += containJsonKeyValue("keep_alive", it)
                }

                embedRequestSpec.requestBodyString.forEach {
                    bodyString += contain(it)
                }
            }

        return OllamaEmbedBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    /**
     * Returns the base URL of the mock Ollama server.
     *
     * @return The server URL in the format `http://localhost:<port>`.
     */
    override fun baseUrl(): String = "http://localhost:${port()}"
}
