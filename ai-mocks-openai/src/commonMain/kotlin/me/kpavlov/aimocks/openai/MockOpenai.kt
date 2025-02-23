package me.kpavlov.aimocks.openai

import io.kotest.assertions.json.containJsonKeyValue
import io.kotest.matchers.equals.beEqual
import me.kpavlov.aimocks.core.AbstractMockLlm
import java.util.function.Consumer

public open class MockOpenai(
    port: Int = 0,
    verbose: Boolean = true,
) : AbstractMockLlm(
        port = port,
        verbose = verbose,
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
            mokksy.post<ChatCompletionRequest>(name = name, ChatCompletionRequest::class) {
                val chatRequest = OpenaiChatRequestSpecification()
                block(chatRequest)

                path = beEqual("/v1/chat/completions")

                chatRequest.temperature?.let {
                    bodyString += containJsonKeyValue("temperature", it)
                }

                chatRequest.maxCompletionTokens?.let {
                    bodyString += containJsonKeyValue("max_completion_tokens", it)
                }

                chatRequest.seed?.let {
                    bodyString += containJsonKeyValue("seed", it)
                }

                chatRequest.model?.let {
                    bodyString += containJsonKeyValue("model", it)
                }

                chatRequest.requestBodyString.forEach {
                    bodyString += it
                }
            }

        return OpenaiBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }
}
