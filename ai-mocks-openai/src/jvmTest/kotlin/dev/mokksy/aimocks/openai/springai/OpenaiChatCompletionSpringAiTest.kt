package dev.mokksy.aimocks.openai.springai

import dev.mokksy.aimocks.openai.ChatCompletionRequest
import dev.mokksy.aimocks.openai.openai
import dev.mokksy.test.utils.runIntegrationTest
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beOfType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.ai.chat.model.ChatResponse
import kotlin.test.Test
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
    fun `Should respond with stream to Chat Completion`() =
        runIntegrationTest {
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

            val buffer = StringBuilder()
            val chunks = mutableListOf<ChatResponse>()
            prepareClientRequest("You are a helpful pirate")
                .stream()
                .chatResponse()
                .asFlow()
                .collect { chunk ->
                    logger.trace { "✅ Received chunk: $chunk" }
                    chunk.result.output.text
                        ?.let { buffer.append(it) }
                    chunks += chunk
                }

            chunks shouldHaveSize (4 + 2) // 4 data chunks + opening and closing chunks
            buffer.toString() shouldBe "Ahoy there, matey! Hello!"
        }
}
