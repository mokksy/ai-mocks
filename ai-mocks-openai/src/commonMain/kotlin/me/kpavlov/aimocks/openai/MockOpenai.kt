package me.kpavlov.aimocks.openai

import io.kotest.assertions.json.containJsonKeyValue
import io.kotest.matchers.equals.beEqual
import me.kpavlov.aimocks.core.AbstractMockLlm

public open class MockOpenai(
    port: Int = 0,
    verbose: Boolean = true,
) : AbstractMockLlm<
        OpenaiBuildingStep,
        OpenaiChatRequestSpecification,
        OpenaiChatResponseSpecification,
    >(
        port = port,
        verbose = verbose,
    ) {
    override fun completion(
        name: String?,
        block: OpenaiChatRequestSpecification.() -> Unit,
    ): OpenaiBuildingStep {
        val requestStep =
            mokksy.post(name = name) {
                val chatRequest = OpenaiChatRequestSpecification()
                block(chatRequest)

                path = beEqual("/v1/chat/completions")

                chatRequest.temperature?.let {
                    body += containJsonKeyValue("temperature", it)
                }

                chatRequest.maxCompletionTokens?.let {
                    body += containJsonKeyValue("max_completion_tokens", it)
                }

                chatRequest.seed?.let {
                    body += containJsonKeyValue("seed", it)
                }

                chatRequest.model?.let {
                    body += containJsonKeyValue("model", it)
                }

                chatRequest.requestBody.forEach {
                    body += it
                }
            }

        return OpenaiBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }
}
