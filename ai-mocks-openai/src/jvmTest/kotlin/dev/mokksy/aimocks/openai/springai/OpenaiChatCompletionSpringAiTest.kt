package dev.mokksy.aimocks.openai.springai

import dev.mokksy.aimocks.openai.ChatCompletionRequest
import dev.mokksy.aimocks.openai.openai
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beOfType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactor.awaitSingle
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

internal class OpenaiChatCompletionSpringAiTest : AbstractSpringAiTest() {
    @Test
    fun `Should respond to Chat Completion`() {
        openai.completion {
            temperature = temperatureValue
            seed = seedValue
            model = modelName
            maxTokens = maxCompletionTokensValue
            systemMessageContains("helpful merchant")
            userMessageContains("say 'Hello!'")
            requestMatchesPredicate {
                !it.stream
            }
        } responds {
            assistantContent = "Hello there! Welcome to our shop! How can I assist you today?"
            finishReason = "stop"
        }

        val response =
            prepareClientRequest("You are a helpful merchant")
                .call()
                .chatResponse()

        response?.result shouldNotBeNull {
            metadata.finishReason shouldBe "STOP"
            output.text shouldBe "Hello there! Welcome to our shop! How can I assist you today?"
        }
    }

    @Test
    suspend fun `Should respond with stream to Chat Completion`() {
        openai.completion {
            temperature = temperatureValue
            seed = seedValue
            model = modelName
            maxTokens = maxCompletionTokensValue
            systemMessageContains("helpful pirate")
            userMessageContains("say 'Hello!'")
            requestMatches(beOfType(ChatCompletionRequest::class))
            requestSatisfies("Should be streamed") {
                it?.stream shouldBe true
            }
        } respondsStream {
            responseFlow =
                flow {
                    emit("Ahoy")
                    emit(" there,")
                    delay(100.milliseconds)
                    emit(" matey!")
                    emit(" Hello!")
                }
            delay = 60.milliseconds
            delayBetweenChunks = 15.milliseconds
            finishReason = "stop"
        }

        val chunks =
            prepareClientRequest("You are a helpful pirate")
                .stream()
                .chatResponse()
                .collectList()
                .awaitSingle()

        chunks shouldHaveSize (4 + 2) // 4 data chunks + opening and closing chunks
        chunks
            .mapNotNull { it.result.output.text }
            .joinToString("") shouldBe "Ahoy there, matey! Hello!"
    }
}
