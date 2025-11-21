package dev.mokksy.aimocks.anthropic

import dev.mokksy.aimocks.anthropic.model.MessageCreateParams
import dev.mokksy.aimocks.core.AbstractMockLlm
import dev.mokksy.mokksy.ServerConfiguration
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.function.Consumer

/**
 * Mock implementation of an Anthropic-compatible service for testing purposes.
 *
 * This class provides an HTTP mock server to simulate Anthropic's Messages API.
 * Extends [AbstractMockLlm] to provide Anthropic-specific functionality.
 *
 * @param port The port on which the mock server will run. Defaults to 0, which allows the server to select
 *             an available port.
 * @param verbose Controls whether the mock server's operations are logged in detail. Defaults to true.
 * @author Konstantin Pavlov
 */
public open class MockAnthropic(
    port: Int = 0,
    verbose: Boolean = true,
) : AbstractMockLlm(
        port = port,
        configuration =
            ServerConfiguration(
                verbose = verbose,
            ) { config ->
                config.json(
                    Json {
                        prettyPrint = true
                        ignoreUnknownKeys = true
                    },
                )
            },
    ) {
    /**
     * Java-friendly overload that accepts a Consumer for configuring the chat request.
     *
     * @param name Optional identifier for the mock endpoint.
     * @param block Configuration block for defining request matching rules via [AnthropicMessagesRequestSpecification].
     * @return An [AnthropicBuildingStep] for configuring the mock response.
     */
    @JvmOverloads
    public fun messages(
        name: String? = null,
        block: Consumer<AnthropicMessagesRequestSpecification>,
    ): AnthropicBuildingStep = messages(name) { block.accept(this) }

    /**
     * Sets up a mock handler for the Anthropic `/v1/messages` endpoint.
     *
     * @param name Optional identifier for the mock endpoint.
     * @param block Configuration block for defining request matching rules via [AnthropicMessagesRequestSpecification].
     * @return An [AnthropicBuildingStep] for configuring the mock response.
     */
    public fun messages(
        name: String? = null,
        block: AnthropicMessagesRequestSpecification.() -> Unit,
    ): AnthropicBuildingStep {
        val requestStep =
            mokksy.post(
                name = name,
                requestType = MessageCreateParams::class,
            ) {
                val chatRequestSpec = AnthropicMessagesRequestSpecification()
                block(chatRequestSpec)

                path("/v1/messages")

                body += chatRequestSpec.requestBody

                chatRequestSpec.userId?.let {
                    body += AnthropicAiMatchers.userIdEquals(it)
                }

                chatRequestSpec.temperature?.let {
                    body += AnthropicAiMatchers.temperatureEquals(it)
                }

                chatRequestSpec.maxTokens?.let {
                    body += AnthropicAiMatchers.maxTokensEquals(it)
                }

                chatRequestSpec.topP?.let {
                    body += AnthropicAiMatchers.topPEquals(it)
                }

                chatRequestSpec.topK?.let {
                    body += AnthropicAiMatchers.topKEquals(it)
                }

                chatRequestSpec.model?.let {
                    body += AnthropicAiMatchers.modelEquals(it)
                }

                body += chatRequestSpec.requestBody

                chatRequestSpec.requestBodyString.forEach {
                    bodyString += it
                }
            }

        return AnthropicBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }
}
