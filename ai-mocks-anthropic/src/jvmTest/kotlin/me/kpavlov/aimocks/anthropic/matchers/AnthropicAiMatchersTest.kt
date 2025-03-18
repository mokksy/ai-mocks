package me.kpavlov.aimocks.anthropic.matchers

import com.anthropic.models.messages.MessageCreateParams
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import me.kpavlov.aimocks.anthropic.AnthropicAiMatchers.maxTokensEquals
import me.kpavlov.aimocks.anthropic.AnthropicAiMatchers.modelEquals
import me.kpavlov.aimocks.anthropic.AnthropicAiMatchers.systemMessageContains
import me.kpavlov.aimocks.anthropic.AnthropicAiMatchers.temperatureEquals
import me.kpavlov.aimocks.anthropic.AnthropicAiMatchers.userIdEquals
import me.kpavlov.aimocks.anthropic.AnthropicAiMatchers.userMessageContains
import org.junit.jupiter.api.Test

/**
 * See https://docs.anthropic.com/en/api/messages
 */
class AnthropicAiMatchersTest {
    private val objectMapper = ObjectMapper().findAndRegisterModules()

    @Test
    fun `should parse single user message`() {
        test(
            // language=json
            """
            {
              "model" : "claude-3-5-haiku-20241022",
              "system":"You are a helpful assistant.",
              "messages" : [ {
                "role" : "user",
                "content" : [ {
                  "type" : "text",
                  "text" : "Tell me a joke about Anthropic"
                } ]
              } ],
              "max_tokens" : 20,
              "temperature" : 0.7,
              "stream" : false,
              "metadata": {
                "user_id": "foo-bar-baz"
              }
            }""",
        ) { body ->
            modelEquals("claude-3-5-haiku-20241022").test(body).passed() shouldBe true
            modelEquals("claude-3-7-haiku").test(body).passed() shouldBe false
            temperatureEquals(0.7).test(body).passed() shouldBe true
            temperatureEquals(0.71).test(body).passed() shouldBe false
            systemMessageContains("helpful assistant").test(body).passed() shouldBe true
            systemMessageContains("unhelpful assistant").test(body).passed() shouldBe false
            userMessageContains("joke about").test(body).passed() shouldBe true
            userMessageContains("joker").test(body).passed() shouldBe false
            userIdEquals("foo-bar-baz").test(body).passed() shouldBe true
            userIdEquals("foo-bar-bUzz").test(body).passed() shouldBe false
            maxTokensEquals(20).test(body).passed() shouldBe true
            maxTokensEquals(42).test(body).passed() shouldBe false
        }
    }

    @Test
    fun `should parse multiple messages`() {
        test(
            // language=json
            """
            {
              "model" : "claude-3-5-haiku-20241022",
              "messages" : [
                  {"role": "user", "content": "Hello there."},
                  {"role": "assistant", "content": "Hi, I'm Claude. How can I help you?"},
                  {"role": "user", "content": "Can you explain LLMs in plain English?"}
               ],
              "system" : [ ],
              "max_tokens" : 20,
              "stream" : false
            }""",
        ) { body ->

            userMessageContains("Hello there.").test(body).passed() shouldBe true
            userMessageContains("Can you explain LLMs").test(body).passed() shouldBe true
            maxTokensEquals(20).test(body).passed() shouldBe true
        }
    }

    @Test
    fun `should parse partially-filled response from Claude`() {
        test(
            // language=json
            """
            {
              "model" : "claude-3-5-haiku-20241022",
              "messages" : [
                  {"role": "user", "content": "What's the Greek name for Sun? (A) Sol (B) Helios (C) Sun"},
                  {"role": "assistant", "content": "The best answer is ("}
               ],
              "system" : [ ],
              "max_tokens" : 42,
              "stream" : false
            }""",
        ) { body ->
            modelEquals("claude-3-5-haiku-20241022").test(body).passed() shouldBe true
            userMessageContains("Greek name").test(body).passed() shouldBe true
            maxTokensEquals(42).test(body).passed() shouldBe true
        }
    }

    @Test
    fun `should parse message with content block`() {
        test(
            // language=json
            """
            {
              "model" : "claude-3-5-haiku-20241022",
              "messages" : [
                  {"role": "user", "content": "What's the Greek name for Sun? (A) Sol (B) Helios (C) Sun"},
                  {"role": "assistant", "content": "The best answer is ("},
                  {"role": "user", "content": [
                  {
                    "type": "image",
                    "source": {
                      "type": "base64",
                      "media_type": "image/jpeg",
                      "data": "/9j/4AAQSkZJRg..."
                    }
                  },
                  {"type": "text", "text": "What is in this image?"}
                ]}
               ],
               "system" : [ {
                "type" : "text",
                "text" : "Let's test"
              } ],
              "max_tokens" : 20,
              "stream" : false
            }""",
        ) { body ->
            systemMessageContains("test").test(body).passed() shouldBe true
            userMessageContains("What is in this image?").test(body).passed() shouldBe true
        }
    }

    private fun test(
        payload: String,
        block: (MessageCreateParams.Body) -> Unit,
    ) {
        val body = objectMapper.readValue(payload, MessageCreateParams.Body::class.java)
        block(body)
    }
}
