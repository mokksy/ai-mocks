package dev.mokksy.aimocks.ollama.springai

import dev.mokksy.aimocks.ollama.mockOllama
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactor.awaitSingle
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

internal class OllamaStreamingChatCompletionSpringAiTest : AbstractSpringAiTest() {
    @Test
    suspend fun `Should respond with stream to Chat Completion`() {
        mockOllama.chat {
            temperature = temperatureValue
            seed = seedValue
            model = modelName
            topK = topKValue
            topP = topPValue
            systemMessageContains("unhelpful orc $seedValue")
            userMessageContains("say 'Hello!'")
            stream = true
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
        }

        val chunks =
            prepareClientRequest("You are a unhelpful orc $seedValue")
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
