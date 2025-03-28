package me.kpavlov.aimocks.openai

import io.kotest.assertions.json.containJsonKeyValue
import io.kotest.matchers.equals.beEqual
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.core.AbstractMockLlm
import me.kpavlov.aimocks.openai.completions.OpenaiChatCompletionRequestSpecification
import me.kpavlov.aimocks.openai.completions.OpenaiChatCompletionsBuildingStep
import me.kpavlov.aimocks.openai.responses.CreateResponseRequest
import me.kpavlov.aimocks.openai.responses.OpenaiResponsesBuildingStep
import me.kpavlov.aimocks.openai.responses.OpenaiResponsesRequestSpecification
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
        block: Consumer<OpenaiChatCompletionRequestSpecification>,
    ): OpenaiChatCompletionsBuildingStep = completion(name) { block.accept(this) }

    public fun completion(
        name: String? = null,
        block: OpenaiChatCompletionRequestSpecification.() -> Unit,
    ): OpenaiChatCompletionsBuildingStep {
        val requestStep =
            mokksy.post<ChatCompletionRequest>(
                name = name,
                requestType = ChatCompletionRequest::class,
            ) {
                val chatRequestSpec = OpenaiChatCompletionRequestSpecification()
                block(chatRequestSpec)

                path = beEqual("/v1/chat/completions")

                body += chatRequestSpec.requestBody

                chatRequestSpec.temperature?.let {
                    bodyString += containJsonKeyValue("temperature", it)
                }

                chatRequestSpec.maxTokens?.let {
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

        return OpenaiChatCompletionsBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    public fun responses(
        name: String? = null,
        block: OpenaiResponsesRequestSpecification.() -> Unit,
    ): OpenaiResponsesBuildingStep {
        val requestStep =
            mokksy.post<CreateResponseRequest>(
                name = name,
                requestType = CreateResponseRequest::class,
            ) {
                val chatRequestSpec = OpenaiResponsesRequestSpecification()
                block(chatRequestSpec)

                path = beEqual("/v1/responses")

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

    override fun baseUrl(): String = "http://localhost:${port()}/v1"
}
