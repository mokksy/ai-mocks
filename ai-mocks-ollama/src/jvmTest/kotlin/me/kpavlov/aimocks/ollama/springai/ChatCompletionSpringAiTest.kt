package me.kpavlov.aimocks.ollama.springai

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.kpavlov.aimocks.ollama.mockOllama
import kotlin.test.Test

internal class ChatCompletionSpringAiTest : AbstractSpringAiTest() {
    @Test
    fun `Should respond to Chat Completion`() {
        mockOllama.chat {
            temperature = temperatureValue
            model = modelName
            seed = seedValue
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
            prepareClientRequest()
                .call()
                .chatResponse()

        response?.result shouldNotBe null
        response?.result?.apply {
            metadata.finishReason shouldBe "stop"
            output?.text shouldBe "Ahoy there, matey! Hello!"
        }
    }
}
