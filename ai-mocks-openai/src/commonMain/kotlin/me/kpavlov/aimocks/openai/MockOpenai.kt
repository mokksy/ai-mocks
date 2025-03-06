package me.kpavlov.aimocks.openai

import io.kotest.assertions.json.containJsonKeyValue
import io.kotest.matchers.equals.beEqual
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.core.AbstractMockLlm
import me.kpavlov.mokksy.ServerConfiguration
import java.util.function.Consumer

public open class MockOpenai(
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
    public fun completion(
        name: String? = null,
        block: Consumer<OpenaiChatRequestSpecification>,
    ): OpenaiBuildingStep = completion(name) { block.accept(this) }

    public fun completion(
        name: String? = null,
        block: OpenaiChatRequestSpecification.() -> Unit,
    ): OpenaiBuildingStep {
        val requestStep =
            mokksy.post<ChatCompletionRequest>(
                name = name,
                requestType = ChatCompletionRequest::class,
            ) {
                val chatRequestSpec = OpenaiChatRequestSpecification()
                block(chatRequestSpec)

                path = beEqual("/v1/chat/completions")

                body += chatRequestSpec.requestBody

                chatRequestSpec.temperature?.let {
                    bodyString += containJsonKeyValue("temperature", it)
                }

                chatRequestSpec.maxCompletionTokens?.let {
                    bodyString += containJsonKeyValue("max_completion_tokens", it)
                }

                chatRequestSpec.seed?.let {
                    bodyString += containJsonKeyValue("seed", it)
                }

                chatRequestSpec.model?.let {
                    bodyString += containJsonKeyValue("model", it)
                }

                chatRequestSpec.requestBodyString.forEach {
                    bodyString += it
                }
            }

        return OpenaiBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    override fun baseUrl(): String = "http://localhost:${port()}/v1"
}
