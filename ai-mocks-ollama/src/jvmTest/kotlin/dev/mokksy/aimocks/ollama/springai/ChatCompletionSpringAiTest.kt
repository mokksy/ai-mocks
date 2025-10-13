package dev.mokksy.aimocks.ollama.springai

import dev.mokksy.aimocks.ollama.mockOllama
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class ChatCompletionSpringAiTest : AbstractSpringAiTest() {
    @Test
    fun `Should respond to Chat Completion`() {
        mockOllama.chat {
            temperature = temperatureValue
            seed = seedValue
            model = modelName
            systemMessageContains("helpful pirate")
            userMessageContains("say 'Hello!'")
            requestMatchesPredicate {
                !it.stream
            }
        } responds {
            assistantContent = "Ahoy there, matey! Hello!"
            finishReason = "stop"
        }

        val response =
            prepareClientRequest("You are a helpful pirate")
                .call()
                .chatResponse()

        response?.result shouldNotBe null
        response?.result?.apply {
            metadata.finishReason shouldBe "stop"
            output?.text shouldBe "Ahoy there, matey! Hello!"
        }
    }
}
