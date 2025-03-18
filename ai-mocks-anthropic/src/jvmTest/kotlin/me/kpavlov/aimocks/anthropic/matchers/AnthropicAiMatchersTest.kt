package me.kpavlov.aimocks.anthropic.matchers

import com.anthropic.models.messages.MessageCreateParams
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import me.kpavlov.aimocks.anthropic.AnthropicAiMatchers.systemMessageContains
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
              "stream" : false
            }""",
        ) { body ->
            systemMessageContains("helpful assistant").test(body).passed() shouldBe true
            userMessageContains("joke about").test(body).passed() shouldBe true
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
              "max_tokens" : 20,
              "stream" : false
            }""",
        ) { body ->
            userMessageContains("Greek name").test(body).passed() shouldBe true
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
