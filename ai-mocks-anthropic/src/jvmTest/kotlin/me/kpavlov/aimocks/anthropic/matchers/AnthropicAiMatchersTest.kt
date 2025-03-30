package me.kpavlov.aimocks.anthropic.matchers

import com.anthropic.models.messages.MessageCreateParams
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import me.kpavlov.aimocks.anthropic.AnthropicAiMatchers.maxTokensEquals
import me.kpavlov.aimocks.anthropic.AnthropicAiMatchers.modelEquals
import me.kpavlov.aimocks.anthropic.AnthropicAiMatchers.systemMessageContains
import me.kpavlov.aimocks.anthropic.AnthropicAiMatchers.temperatureEquals
import me.kpavlov.aimocks.anthropic.AnthropicAiMatchers.topKEquals
import me.kpavlov.aimocks.anthropic.AnthropicAiMatchers.topPEquals
import me.kpavlov.aimocks.anthropic.AnthropicAiMatchers.userIdEquals
import me.kpavlov.aimocks.anthropic.AnthropicAiMatchers.userMessageContains
import org.junit.jupiter.api.Test

/**
 * See https://docs.anthropic.com/en/api/messages
 */
class AnthropicAiMatchersTest {
    private val objectMapper = ObjectMapper().findAndRegisterModules()

    @Test
    @Suppress("LongMethod")
    fun `should parse single user message`() {
        test(
            // language=json
            """
            {
              "model" : "claude-3-5-haiku-20241022",
              "system":"You are a helpful assistant.",
              "top_p":0.42,
              "top_k":100500,
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
            modelEquals("claude-3-5-haiku-20241022").apply {
                toString() shouldBe "model should be \"claude-3-5-haiku-20241022\""
                test(body).apply {
                    passed() shouldBe true
                    failureMessage() shouldBe "model should be \"claude-3-5-haiku-20241022\""
                    negatedFailureMessage() shouldBe
                        "model should not be \"claude-3-5-haiku-20241022\""
                }
            }
            modelEquals("claude-3-7-haiku").test(body).passed() shouldBe false
            temperatureEquals(0.7).apply {
                toString() shouldBe "temperature should be 0.7"
                test(body).apply {
                    passed() shouldBe true
                    failureMessage() shouldBe "temperature should be 0.7"
                    negatedFailureMessage() shouldBe "temperature should not be 0.7"
                }
            }
            temperatureEquals(0.71).test(body).passed() shouldBe false
            topPEquals(0.42).apply {
                toString() shouldBe "top_p should be 0.42"
                test(body).apply {
                    passed() shouldBe true
                    failureMessage() shouldBe "top_p should be 0.42"
                    negatedFailureMessage() shouldBe "top_p should not be 0.42"
                }
            }
            topPEquals(0.142).test(body).passed() shouldBe false
            topKEquals(100500).apply {
                toString() shouldBe "top_k should be 100500"
                test(body).apply {
                    passed() shouldBe true
                    failureMessage() shouldBe "top_k should be 100500"
                    negatedFailureMessage() shouldBe "top_k should not be 100500"
                }
            }
            topKEquals(55).test(body).apply {
                passed() shouldBe false
            }
            systemMessageContains("helpful assistant").apply {
                toString() shouldBe "System message should contain \"helpful assistant\""
                test(body).apply {
                    passed() shouldBe true
                    failureMessage() shouldBe "System message should contain \"helpful assistant\""
                    negatedFailureMessage() shouldBe
                        "System message should not contain \"helpful assistant\""
                }
            }
            systemMessageContains("unhelpful assistant").test(body).passed() shouldBe false
            userMessageContains("joke about").apply {
                toString() shouldBe "User message should contain \"joke about\""
                test(body).apply {
                    passed() shouldBe true
                    failureMessage() shouldBe "User message should contain \"joke about\""
                    negatedFailureMessage() shouldBe
                        "User message should not contain \"joke about\""
                }
            }
            userMessageContains("joker").test(body).passed() shouldBe false
            userIdEquals("foo-bar-baz").apply {
                toString() shouldBe "metadata.user_id should be \"foo-bar-baz\""
                test(body).apply {
                    passed() shouldBe true
                    failureMessage() shouldBe "metadata.user_id should be \"foo-bar-baz\""
                    negatedFailureMessage() shouldBe
                        "metadata.user_id should not be \"foo-bar-baz\""
                }
            }
            userIdEquals("foo-bar-bUzz").test(body).passed() shouldBe false
            maxTokensEquals(20).apply {
                toString() shouldBe "maxTokens should be 20"
                test(body).apply {
                    passed() shouldBe true
                    failureMessage() shouldBe "maxTokens should be 20"
                    negatedFailureMessage() shouldBe "maxTokens should not be 20"
                }
            }
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
