package me.kpavlov.aimocks.anthropic

import com.anthropic.models.messages.MessageCreateParams
import io.ktor.server.plugins.contentnegotiation.ContentNegotiationConfig
import me.kpavlov.aimocks.core.AbstractMockLlm
import me.kpavlov.mokksy.ServerConfiguration
import java.util.function.Consumer

internal expect fun configureContentNegotiation(config: ContentNegotiationConfig)

internal expect fun serializer(data: Any): String

public open class MockAnthropic(
    port: Int = 0,
    verbose: Boolean = true,
) : AbstractMockLlm(
        port = port,
        configuration =
            ServerConfiguration(
                verbose = verbose,
            ) {
                configureContentNegotiation(it)
            },
    ) {
    /**
     * Java-friendly overload that accepts a Consumer for configuring the chat request.
     */
    @JvmOverloads
    public fun messages(
        name: String? = null,
        block: Consumer<AnthropicMessagesRequestSpecification>,
    ): AnthropicBuildingStep = messages(name) { block.accept(this) }

    public fun messages(
        name: String? = null,
        block: AnthropicMessagesRequestSpecification.() -> Unit,
    ): AnthropicBuildingStep {
        val requestStep =
            mokksy.post(
                name = name,
                requestType = MessageCreateParams.Body::class,
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
            serializer = { serializer(it) },
        )
    }
}
