package me.kpavlov.aimocks.gemini

import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.core.AbstractMockLlm
import me.kpavlov.aimocks.gemini.content.GeminiContentBuildingStep
import me.kpavlov.aimocks.gemini.content.GeminiContentRequestSpecification
import me.kpavlov.mokksy.ServerConfiguration
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
                val chatRequestSpec = GeminiContentRequestSpecification()
                block(chatRequestSpec)

                val model = chatRequestSpec.model
                @Suppress("MaxLineLength")
                path("/v1/projects/${chatRequestSpec.project}/locations/${chatRequestSpec.location}/publishers/google/models/${model}:generateContent")

                body += chatRequestSpec.requestBody

                chatRequestSpec.temperature?.let { temperature ->
                    bodyMatchesPredicate {
                        val requestTemperature = it?.generationConfig?.temperature
                        requestTemperature != null
                            && (abs(requestTemperature - temperature) <= EPSILON)

                    }
                }

                chatRequestSpec.requestBodyString.forEach {
                    bodyString += it
                }
            }

        return GeminiContentBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    override fun baseUrl(): String = "http://localhost:${port()}"
}
