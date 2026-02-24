package dev.mokksy.aimocks.ollama.springai

import dev.mokksy.aimocks.ollama.mockOllama
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class OllamaChatCompletionSpringAiTest : AbstractSpringAiTest() {
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
            requireNotNull(
                prepareClientRequest("You are a helpful pirate")
                    .call()
                    .chatResponse(),
            )

        response.result shouldNotBeNull {
            metadata.finishReason shouldBe "stop"
            output.text shouldBe "Ahoy there, matey! Hello!"
        }
    }
}
