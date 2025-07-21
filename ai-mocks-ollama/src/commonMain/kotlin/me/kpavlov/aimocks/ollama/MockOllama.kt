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
     * Java-friendly overload that accepts a Consumer for configuring the generate request.
     */
    @JvmOverloads
    public fun generate(
        name: String? = null,
        block: Consumer<OllamaGenerateRequestSpecification>,
    ): OllamaGenerateBuildingStep = generate(name) { block.accept(this) }

    /**
     * Configures a mock for the generate completion endpoint.
     *
     * @param name An optional name for the mock
     * @param block A configuration block for specifying the request matching criteria
     * @return A building step for configuring the response
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
     * Java-friendly overload that accepts a Consumer for configuring the chat request.
     */
    @JvmOverloads
    public fun chat(
        name: String? = null,
        block: Consumer<OllamaChatRequestSpecification>,
    ): OllamaChatBuildingStep = chat(name) { block.accept(this) }

    /**
     * Configures a mock for the chat completion endpoint.
     *
     * @param name An optional name for the mock
     * @param block A configuration block for specifying the request matching criteria
     * @return A building step for configuring the response
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
     * Java-friendly overload that accepts a Consumer for configuring the embedding request.
     */
    @JvmOverloads
    public fun embed(
        name: String? = null,
        block: Consumer<OllamaEmbedRequestSpecification>,
    ): OllamaEmbedBuildingStep = embed(name) { block.accept(this) }

    /**
     * Configures a mock for the embed endpoint.
     *
     * This method supports both string and string list inputs for embedding requests.
     *
     * @param name An optional name for the mock
     * @param block A configuration block for specifying the request matching criteria
     * @return A building step for configuring the response
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

    override fun baseUrl(): String = "http://localhost:${port()}"
}
