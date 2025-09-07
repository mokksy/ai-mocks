package me.kpavlov.aimocks.gemini.springai

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import me.kpavlov.aimocks.gemini.gemini
import org.springframework.ai.chat.model.ChatResponse
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

internal class ChatCompletionSpringAiTest : AbstractSpringAiTest() {
    @Test
    fun `Should respond to generateContent`() {
        val systemMessage = "You are a helpful pirate. $seedValue"
        gemini.generateContent {
            temperature = temperatureValue
            model = modelName
            project = projectId
            location = locationId
            systemMessageContains(systemMessage)
            userMessageContains("Just say 'Hello!'")
        } responds {
            content = "Ahoy there, matey! Hello!"
            finishReason = "stop"
            delay = 42.milliseconds
        }

        val response: ChatResponse? =
            prepareClientRequest()
                .call()
                .chatResponse()

        response shouldNotBeNull {
            result shouldNotBeNull {
                metadata.finishReason shouldBe "STOP"
                output.text shouldBe "Ahoy there, matey! Hello!"
            }
        }
    }
}
