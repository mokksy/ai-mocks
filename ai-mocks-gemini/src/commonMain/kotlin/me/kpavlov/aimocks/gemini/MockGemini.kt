package me.kpavlov.aimocks.gemini

import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.core.AbstractMockLlm
import me.kpavlov.aimocks.gemini.content.GeminiContentBuildingStep
import me.kpavlov.aimocks.gemini.content.GeminiContentRequestSpecification
import me.kpavlov.aimocks.gemini.content.GeminiStreamingContentBuildingStep
import me.kpavlov.mokksy.ServerConfiguration
import me.kpavlov.mokksy.request.RequestSpecificationBuilder
import java.util.function.Consumer
import kotlin.math.abs

private const val EPSILON = 0.00000005

/**
 * Mock implementation of an OpenAI-compatible service for testing purposes.
 *
 * This class provides an HTTP mock server to simulate OpenAI APIs, specifically for chat
 * completions and response generation. It is designed to mimic the behavior of the OpenAI APIs
 * locally and facilitate integration testing and development.
 *
 * @param port The port on which the mock server will run. Defaults to 0, which allows the server to select
 *             an available port.
 * @param verbose Controls whether the mock server's operations are logged in detail. Defaults to true.
 * @author Konstantin Pavlov
 */
public open class MockGemini(
    port: Int = 0,
    verbose: Boolean = true,
) : AbstractMockLlm(
        port = port,
        configuration =
            ServerConfiguration(
                verbose = verbose,
            ) { config ->
                config.json(
                    Json { ignoreUnknownKeys = true },
                )
            },
    ) {
    public constructor(verbose: Boolean = true) : this(port = 0, verbose = verbose)

    /**
     * Java-friendly overload that accepts a Consumer for configuring the chat request.
     */
    @JvmOverloads
    public fun generateContent(
        name: String? = null,
        block: Consumer<GeminiContentRequestSpecification>,
    ): GeminiContentBuildingStep = generateContent(name) { block.accept(this) }

    public fun generateContent(
        name: String? = null,
        block: GeminiContentRequestSpecification.() -> Unit,
    ): GeminiContentBuildingStep {
        val requestStep =
            mokksy.post(
                name = name,
                requestType = GenerateContentRequest::class,
            ) {
                val chatRequestSpec = matchRequestSpec(this, block)

                val model = chatRequestSpec.model

                @Suppress("MaxLineLength")
                val pathString: String =
                    chatRequestSpec.path
                        ?: "/${chatRequestSpec.apiVersion}/projects/${chatRequestSpec.project}/locations/${chatRequestSpec.location}/publishers/google/models/$model:generateContent"
                path(pathString)
            }

        return GeminiContentBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    /**
     * Generates a streaming content response specification for a Gemini content generation request.
     * This method provides a Java-friendly overload that accepts a `Consumer` for configuring the content request.
     *
     * @param name An optional name for the content request. Defaults to `null` if not provided.
     * @param block A `Consumer` that allows configuration of the `GeminiContentRequestSpecification` for the request.
     * @return A `GeminiStreamingContentBuildingStep` that represents the next step in configuring the streaming content response.
     */
    @JvmOverloads
    @Suppress("MaxLineLength")
    public fun generateContentStream(
        name: String? = null,
        block: Consumer<GeminiContentRequestSpecification>,
    ): GeminiStreamingContentBuildingStep = generateContentStream(name) { block.accept(this) }

    public fun generateContentStream(
        name: String? = null,
        block: GeminiContentRequestSpecification.() -> Unit,
    ): GeminiStreamingContentBuildingStep {
        val requestStep =
            mokksy.post(
                name = name,
                requestType = GenerateContentRequest::class,
            ) {
                val chatRequestSpec = matchRequestSpec(this, block)

                val model = chatRequestSpec.model

                @Suppress("MaxLineLength")
                val pathString: String =
                    chatRequestSpec.path
                        ?: "/${chatRequestSpec.apiVersion}/projects/${chatRequestSpec.project}/locations/${chatRequestSpec.location}/publishers/google/models/$model:streamGenerateContent"
                path(pathString)
            }

        return GeminiStreamingContentBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    override fun baseUrl(): String = "http://localhost:${port()}"

    private fun matchRequestSpec(
        builder: RequestSpecificationBuilder<GenerateContentRequest>,
        block: GeminiContentRequestSpecification.() -> Unit,
    ): GeminiContentRequestSpecification {
        val chatRequestSpec = GeminiContentRequestSpecification()
        block.invoke(chatRequestSpec)

        builder.body += chatRequestSpec.requestBody

        chatRequestSpec.maxOutputTokens?.let { maxOutputTokens ->
            builder.bodyMatchesPredicate(
                description = "Max output tokens should be $maxOutputTokens.",
            ) {
                it?.generationConfig?.maxOutputTokens == maxOutputTokens
            }
        }

        chatRequestSpec.seed?.let { seed ->
            builder.bodyMatchesPredicate(
                description = "Seed should be $seed.",
            ) {
                it?.generationConfig?.seed == seed.toInt()
            }
        }

        chatRequestSpec.temperature?.let { temperature ->
            builder.bodyMatchesPredicate(
                description = "Temperature should be within $EPSILON of $temperature.",
            ) {
                val requestTemperature = it?.generationConfig?.temperature
                requestTemperature != null &&
                    (abs(requestTemperature - temperature) <= EPSILON)
            }
        }

        chatRequestSpec.topP?.let { topP ->
            builder.bodyMatchesPredicate(
                description = "topP should be within $EPSILON of $topP.",
            ) {
                val value = it?.generationConfig?.topP
                value != null && (abs(value - topP) <= EPSILON)
            }
        }

        chatRequestSpec.topK?.let { topK ->
            builder.bodyMatchesPredicate(
                description = "Top K should be $topK.",
            ) {
                it?.generationConfig?.topK?.toLong() == topK
            }
        }

        chatRequestSpec.requestBodyString.forEach {
            builder.bodyString += it
        }
        return chatRequestSpec
    }
}
