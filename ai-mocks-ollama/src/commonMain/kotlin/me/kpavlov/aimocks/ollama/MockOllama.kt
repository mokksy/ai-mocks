package me.kpavlov.aimocks.ollama

import io.kotest.assertions.json.containJsonKeyValue
import io.kotest.matchers.string.contain
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.core.AbstractMockLlm
import me.kpavlov.aimocks.ollama.chat.ChatRequest
import me.kpavlov.aimocks.ollama.chat.OllamaChatBuildingStep
import me.kpavlov.aimocks.ollama.chat.OllamaChatRequestSpecification
import me.kpavlov.aimocks.ollama.embed.EmbeddingsRequest
import me.kpavlov.aimocks.ollama.embed.OllamaEmbedBuildingStep
import me.kpavlov.aimocks.ollama.embed.OllamaEmbedRequestSpecification
import me.kpavlov.aimocks.ollama.generate.GenerateRequest
import me.kpavlov.aimocks.ollama.generate.OllamaGenerateBuildingStep
import me.kpavlov.aimocks.ollama.generate.OllamaGenerateRequestSpecification
import me.kpavlov.mokksy.ServerConfiguration
import java.util.function.Consumer

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
            name = "MockOllama"
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
     * Configures a mock `/api/generate` endpoint using a Java `Consumer` to specify the request criteria.
     *
     * This overload enables Java interoperability for setting up request matching and response configuration for the generate API.
     *
     * @param name An optional name for the mock configuration.
     * @param block A Java `Consumer` that configures the generate request specification.
     * @return A building step for further response configuration.
     */
    @JvmOverloads
    public fun generate(
        name: String? = null,
        block: Consumer<OllamaGenerateRequestSpecification>,
    ): OllamaGenerateBuildingStep = generate(name) { block.accept(this) }

    /**
     * Sets up a mock handler for the Ollama `/api/generate` completion endpoint.
     *
     * Allows configuration of request matching criteria such as model, prompt, system, template, and stream fields. Returns a builder step for specifying the mock response.
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

                generateRequestSpec.prompt?.let {
                    bodyString += containJsonKeyValue("prompt", it)
                }

                generateRequestSpec.system?.let {
                    bodyString += containJsonKeyValue("system", it)
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
     * Provides a Java-compatible overload for configuring a mock Ollama chat request using a Consumer.
     *
     * @param name An optional name for the mock configuration.
     * @param block A Consumer that configures the chat request specification.
     * @return A building step for further configuration of the mock chat response.
     */
    @JvmOverloads
    public fun chat(
        name: String? = null,
        block: Consumer<OllamaChatRequestSpecification>,
    ): OllamaChatBuildingStep = chat(name) { block.accept(this) }

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
                    bodyString += containJsonKeyValue("seed", it)
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
     * Provides a Java-compatible overload for configuring a mock `/api/embed` endpoint using a Consumer.
     *
     * Allows Java code to specify the embedding request specification for the mock Ollama embed API.
     *
     * @param name Optional name for the mock configuration.
     * @param block Consumer that configures the embedding request specification.
     * @return A building step for further response configuration.
     */
    @JvmOverloads
    public fun embed(
        name: String? = null,
        block: Consumer<OllamaEmbedRequestSpecification>,
    ): OllamaEmbedBuildingStep = embed(name) { block.accept(this) }

    /**
     * Sets up a mock handler for the Ollama `/api/embed` endpoint, allowing configuration of request matching for embedding requests.
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

                embedRequestSpec.requestBodyString.filterNotNull().forEach {
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
