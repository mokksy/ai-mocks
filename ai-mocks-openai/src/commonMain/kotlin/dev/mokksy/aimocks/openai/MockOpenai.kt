package dev.mokksy.aimocks.openai

import dev.mokksy.aimocks.core.AbstractMockLlm
import dev.mokksy.aimocks.openai.completions.OpenaiChatCompletionRequestSpecification
import dev.mokksy.aimocks.openai.completions.OpenaiChatCompletionsBuildingStep
import dev.mokksy.aimocks.openai.embeddings.OpenaiEmbedBuildingStep
import dev.mokksy.aimocks.openai.embeddings.OpenaiEmbedRequestSpecification
import dev.mokksy.aimocks.openai.model.embeddings.CreateEmbeddingsRequest
import dev.mokksy.aimocks.openai.model.moderation.CreateModerationRequest
import dev.mokksy.aimocks.openai.model.responses.CreateResponseRequest
import dev.mokksy.aimocks.openai.moderation.OpenaiModerationBuildingStep
import dev.mokksy.aimocks.openai.moderation.OpenaiModerationRequestSpecification
import dev.mokksy.aimocks.openai.responses.OpenaiResponsesBuildingStep
import dev.mokksy.aimocks.openai.responses.OpenaiResponsesRequestSpecification
import dev.mokksy.mokksy.ServerConfiguration
import io.kotest.assertions.json.containJsonKeyValue
import io.kotest.matchers.string.contain
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.function.Consumer

/**
 * Mock implementation of an OpenAI-compatible service for testing purposes.
 *
 * This class provides an HTTP mock server to simulate OpenAI APIs, specifically for chat
 * completions and response generation. It is designed to mimic the behavior of the OpenAI APIs
 * locally and facilitate integration testing and development.
 *
 * Extends [AbstractMockLlm] to provide OpenAI-specific functionality.
 *
 * @param port The port on which the mock server will run. Defaults to 0, which allows the server to select
 *             an available port.
 * @param verbose Controls whether the mock server's operations are logged in detail. Defaults to true.
 * @see <a href="https://platform.openai.com/docs/api-reference">OpenAI API Reference</a>
 * @author Konstantin Pavlov
 */
public open class MockOpenai(
    port: Int = 0,
    verbose: Boolean = true,
) : AbstractMockLlm(
        port = port,
        configuration =
            ServerConfiguration(
                name = "MockOpenai",
                verbose = verbose,
            ) { config ->
                config.json(
                    Json { ignoreUnknownKeys = true },
                )
            },
    ) {
    /**
     * Java-friendly overload that accepts a Consumer for configuring the chat request.
     *
     * @param name An optional identifier for the mock configuration.
     * @param block A Consumer to configure the specifications via [OpenaiChatCompletionRequestSpecification].
     * @return An [OpenaiChatCompletionsBuildingStep] for configuring mock response behavior.
     */
    @JvmOverloads
    public fun completion(
        name: String? = null,
        block: Consumer<OpenaiChatCompletionRequestSpecification>,
    ): OpenaiChatCompletionsBuildingStep = completion(name) { block.accept(this) }

    /**
     * Configures and constructs a mock handler for the OpenAI `/v1/chat/completions` endpoint.
     *
     * This method allows you to define specifications and criteria for chat completion requests,
     * enabling controlled responses for testing and simulations.
     *
     * @param name An optional identifier for the mock configuration. Defaults to `null` if not provided.
     * @param block A lambda function to configure the specifications for the chat completion request,
     *              using the [OpenaiChatCompletionRequestSpecification] object.
     * @return An instance of [OpenaiChatCompletionsBuildingStep], representing a builder step
     *         for configuring mock response behavior.
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/create">Create Chat Completion</a>
     */
    public fun completion(
        name: String? = null,
        block: OpenaiChatCompletionRequestSpecification.() -> Unit,
    ): OpenaiChatCompletionsBuildingStep {
        val requestStep =
            mokksy.post(
                name = name,
                requestType = ChatCompletionRequest::class,
            ) {
                val requestSpec = OpenaiChatCompletionRequestSpecification()
                block(requestSpec)

                path("/v1/chat/completions")

                body += requestSpec.requestBody
                bodyString += requestSpec.requestBodyString

                requestSpec.temperature?.let {
                    bodyString += containJsonKeyValue("temperature", it)
                }

                requestSpec.maxTokens?.let {
                    bodyString += containJsonKeyValue("max_completion_tokens", it)
                }

                requestSpec.seed?.let {
                    bodyString += containJsonKeyValue("seed", it)
                }

                requestSpec.model?.let {
                    bodyString += containJsonKeyValue("model", it)
                }
            }

        return OpenaiChatCompletionsBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    /**
     * Sets up a mock handler for the OpenAI `/v1/responses` endpoint,
     * allowing configuration of request matching for response generation requests.
     *
     * This endpoint is used to generate responses based on input files and instructions.
     *
     * @param name Optional identifier for the mock configuration.
     * @param block Lambda to configure the request matching criteria via [OpenaiResponsesRequestSpecification].
     * @return An [OpenaiResponsesBuildingStep] for specifying the mock response to response generation requests.
     * @see <a href="https://platform.openai.com/docs/api-reference/responses/create">OpenAI Responses API</a>
     */
    public fun responses(
        name: String? = null,
        block: OpenaiResponsesRequestSpecification.() -> Unit,
    ): OpenaiResponsesBuildingStep {
        val requestStep =
            mokksy.post(
                name = name,
                requestType = CreateResponseRequest::class,
            ) {
                val chatRequestSpec = OpenaiResponsesRequestSpecification()
                block(chatRequestSpec)

                path("/v1/responses")

                body += chatRequestSpec.requestBody

                chatRequestSpec.temperature?.let {
                    bodyString += containJsonKeyValue("temperature", it)
                }

                chatRequestSpec.maxTokens?.let {
                    bodyString += containJsonKeyValue("max_output_tokens", it)
                }

                chatRequestSpec.model?.let {
                    bodyString += containJsonKeyValue("model", it)
                }

                chatRequestSpec.requestBodyString.forEach {
                    bodyString += it
                }
            }

        return OpenaiResponsesBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    /**
     * Java-friendly overload that accepts a Consumer for configuring the moderation request.
     *
     * @param name Optional identifier for the mock configuration.
     * @param block Consumer to configure the request matching criteria via [OpenaiModerationRequestSpecification].
     * @return An [OpenaiModerationBuildingStep] for specifying the mock response to moderation requests.
     */
    @JvmOverloads
    public fun moderation(
        name: String? = null,
        block: Consumer<OpenaiModerationRequestSpecification>,
    ): OpenaiModerationBuildingStep = moderation(name) { block.accept(this) }

    /**
     * Sets up a mock handler for the OpenAI `/v1/moderations` endpoint,
     * allowing configuration of request matching for moderation requests.
     *
     * This endpoint classifies if input text or images violate OpenAI's usage policies.
     *
     * @param name Optional identifier for the mock configuration.
     * @param block Lambda to configure the request matching criteria via [OpenaiModerationRequestSpecification].
     * @return An [OpenaiModerationBuildingStep] for specifying the mock response to moderation requests.
     * @see <a href="https://platform.openai.com/docs/api-reference/moderations/create">OpenAI Moderations API</a>
     */
    public fun moderation(
        name: String? = null,
        block: OpenaiModerationRequestSpecification.() -> Unit,
    ): OpenaiModerationBuildingStep {
        val requestStep =
            mokksy.post(
                name = name,
                requestType = CreateModerationRequest::class,
            ) {
                val reqSpec = OpenaiModerationRequestSpecification()
                block(reqSpec)

                path("/v1/moderations")

                body += reqSpec.requestBody

                reqSpec.model?.let {
                    bodyString += containJsonKeyValue("model", it)
                }

                reqSpec.requestBodyString.forEach {
                    bodyString += it
                }
            }

        return OpenaiModerationBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    /**
     * Java-friendly overload that accepts a Consumer for configuring the embedding request.
     *
     * @param name Optional identifier for the mock configuration.
     * @param block Consumer to configure the request matching criteria via [OpenaiEmbedRequestSpecification].
     * @return An [OpenaiEmbedBuildingStep] for specifying the mock response to embedding requests.
     */
    @JvmOverloads
    public fun embeddings(
        name: String? = null,
        block: Consumer<OpenaiEmbedRequestSpecification>,
    ): OpenaiEmbedBuildingStep = embeddings(name) { block.accept(this) }

    /**
     * Sets up a mock handler for the OpenAI `/v1/embeddings` endpoint,
     * allowing configuration of request matching for embedding requests.
     *
     * Supports matching on model, input (string or list), dimensions, encoding_format,
     * and user fields in the request body.
     *
     * Example with single string input:
     * ```kotlin
     * openai.embeddings {
     *     model = "text-embedding-3-small"
     *     inputContains("Hello")
     *     stringInput("Hello world")
     * } responds {
     *     delay = 200.milliseconds
     *     embeddings(listOf(0.1f, 0.2f, 0.3f))
     * }
     * ```
     *
     * Example with list of strings:
     * ```kotlin
     * openai.embeddings {
     *     model = "text-embedding-3-small"
     *     stringListInput(listOf("Hello", "world"))
     * } responds {
     *     embeddings(
     *         listOf(0.1f, 0.2f, 0.3f),
     *         listOf(0.4f, 0.5f, 0.6f)
     *     )
     * }
     * ```
     *
     * @param name Optional identifier for the mock configuration.
     * @param block Lambda to configure the request matching criteria via [OpenaiEmbedRequestSpecification].
     * @return An [OpenaiEmbedBuildingStep] for specifying the mock response to embedding requests.
     * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/create">OpenAI Embeddings API</a>
     */
    @JvmOverloads
    public fun embeddings(
        name: String? = null,
        block: OpenaiEmbedRequestSpecification.() -> Unit,
    ): OpenaiEmbedBuildingStep {
        val requestStep =
            mokksy.post(
                name = name,
                requestType = CreateEmbeddingsRequest::class,
            ) {
                val embedRequestSpec = OpenaiEmbedRequestSpecification()
                block(embedRequestSpec)

                path("/v1/embeddings")

                embedRequestSpec.model?.let {
                    bodyString += containJsonKeyValue("model", it)
                }

                // Handle string input
                embedRequestSpec.stringInput?.let {
                    bodyString += containJsonKeyValue("input", it)
                }

                // Handle string list input
                embedRequestSpec.stringListInput?.let {
                    // For list inputs, we can't use containJsonKeyValue directly.
                    // Instead, we'll check that the request body contains the input values
                    it.forEach { inputValue ->
                        bodyString += contain(inputValue)
                    }
                }

                embedRequestSpec.user?.let {
                    bodyString += containJsonKeyValue("user", it)
                }

                body += embedRequestSpec.requestBody
                bodyString += embedRequestSpec.requestBodyString
            }

        return OpenaiEmbedBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    public fun MockOpenai.moderation(
        block: Consumer<OpenaiModerationRequestSpecification>,
    ): OpenaiModerationBuildingStep = moderation(block)

    override fun baseUrl(): String = "http://localhost:${port()}/v1"
}
