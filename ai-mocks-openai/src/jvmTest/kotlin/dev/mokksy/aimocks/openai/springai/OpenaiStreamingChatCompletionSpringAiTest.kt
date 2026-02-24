package dev.mokksy.aimocks.openai.springai

import dev.mokksy.aimocks.openai.openai
import dev.mokksy.test.utils.runIntegrationTest
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactor.awaitSingle
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

internal class OpenaiStreamingChatCompletionSpringAiTest : AbstractSpringAiTest() {
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
