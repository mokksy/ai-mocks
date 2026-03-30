package dev.mokksy.aimocks.openai.model.chat

import dev.mokksy.aimocks.openai.model.ChatCompletionRole
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ChatCompletionRequestBuilderTest {
    @Test
    fun `build creates request with required fields`() {
        val request =
            ChatCompletionRequestBuilder()
                .model("gpt-4")
                .addUserMessage("Hello")
                .build()

        assertSoftly(request) {
            model shouldBe "gpt-4"
            messages shouldNotBeNull {
                size shouldBe 1
                first().role shouldBe ChatCompletionRole.USER
            }
        }
    }

    @Test
    fun `build creates request with system, user and assistant messages`() {
        val request =
            ChatCompletionRequestBuilder()
                .model("gpt-4o")
                .addSystemMessage("You are a helpful assistant")
                .addUserMessage("Hello")
                .addAssistantMessage("Hi there!")
                .build()

        assertSoftly(request) {
            model shouldBe "gpt-4o"
            messages.size shouldBe 3
            messages[0].role shouldBe ChatCompletionRole.SYSTEM
            messages[1].role shouldBe ChatCompletionRole.USER
            messages[2].role shouldBe ChatCompletionRole.ASSISTANT
        }
    }

    @Test
    fun `build applies all optional parameters`() {
        val request =
            ChatCompletionRequestBuilder()
                .model("gpt-4")
                .addUserMessage("Hello")
                .temperature(0.7)
                .seed(42)
                .store(true)
                .reasoningEffort("high")
                .maxCompletionTokens(100)
                .frequencyPenalty(0.5)
                .stream(true)
                .metadata(mapOf("key" to "value"))
                .build()

        assertSoftly(request) {
            temperature shouldBe 0.7
            seed shouldBe 42
            store shouldBe true
            reasoningEffort shouldBe "high"
            maxCompletionTokens shouldBe 100
            frequencyPenalty shouldBe 0.5
            stream shouldBe true
            metadata.shouldNotBeNull()
        }
    }

    @Test
    fun `build uses defaults for unset optional fields`() {
        val request =
            ChatCompletionRequestBuilder()
                .model("gpt-4")
                .addUserMessage("Hello")
                .build()

        assertSoftly(request) {
            temperature shouldBe 1.0
            store shouldBe false
            reasoningEffort shouldBe "medium"
            stream shouldBe false
            seed.shouldBeNull()
            maxCompletionTokens.shouldBeNull()
            tools.shouldBeNull()
        }
    }

    @Test
    fun `build fails when no messages are provided`() {
        val ex =
            assertFailsWith<IllegalArgumentException> {
                ChatCompletionRequestBuilder()
                    .model("gpt-4")
                    .build()
            }
        ex.message shouldBe "At least one message is required"
    }

    @Test
    fun `build fails when no model is provided`() {
        val ex =
            assertFailsWith<IllegalArgumentException> {
                ChatCompletionRequestBuilder()
                    .addUserMessage("Hello")
                    .build()
            }
        ex.message shouldBe "Model is required"
    }
}
