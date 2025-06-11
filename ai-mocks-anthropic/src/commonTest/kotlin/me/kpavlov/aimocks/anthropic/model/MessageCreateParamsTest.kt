package me.kpavlov.aimocks.anthropic.model

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

internal class MessageCreateParamsTest {
    val jsonParser =
        Json {
            ignoreUnknownKeys = false
        }

    @Test
    fun `Should deserialize text request`() {
        val json =
            // language=json
            """
            {
    "model": "claude-3-7-sonnet-20250219",
    "max_tokens": 1024,
    "messages": [
  {"role": "user", "content": "Hello there."},
  {"role": "assistant", "content": "Hi, I'm Claude. How can I help you?"},
  {"role": "user", "content": "Can you explain LLMs in plain English?"}
]
}
            """.trimIndent()
        val result = jsonParser.decodeFromString<MessageCreateParams>(json)
        result.model shouldBe "claude-3-7-sonnet-20250219"
    }

    @Test
    fun `Should deserialize image request`() {
        val json =
            // language=json
            """
            {
                "model": "claude-3-7-sonnet-20250219",
                "max_tokens": 1024,
                "messages": [
              {"role": "user", "content": "Hello there."},
              {"role": "assistant", "content": "Hi, I'm Claude. How can I help you?"},
              {"role": "user", "content": "Can you explain LLMs in plain English?"} ,
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
            ]
            }
            """.trimIndent()

        val result = jsonParser.decodeFromString<MessageCreateParams>(json)
        result.model shouldBe "claude-3-7-sonnet-20250219"
    }

    @Test
    fun `Should deserialize request with system array`() {
        val json =
            // language=json
            """
            {
               "model" : "claude-3-5-haiku-20241022",
               "messages" : [ {
                 "role" : "user",
                 "content" : [ {
                   "type" : "text",
                   "text" : "Respond with error 400: invalid_request_error"
                 } ]
               } ],
               "system" : [ {
                 "type" : "text",
                 "text" : "Let's test invalid_request_error"
               } ],
               "max_tokens" : 20,
               "stream" : false,
               "tools" : [ ]
             }
            """.trimIndent()

        val result = jsonParser.decodeFromString<MessageCreateParams>(json)
        result.model shouldBe "claude-3-5-haiku-20241022"
        result.system shouldBe
            listOf(MessageCreateParams.SystemPrompt("Let's test invalid_request_error"))
    }

    @Test
    fun `Should deserialize request with empty system array`() {
        val json =
            // language=json
            """
            {
                "model" : "claude-3-opus-latest",
                "messages" : [ {
                  "role" : "user",
                  "content" : [ {
                    "type" : "text",
                    "text" : "What is in the sea?"
                  } ]
                } ],
                "system" : [ ],
                "max_tokens" : 1024,
                "stream" : true
              }
            """.trimIndent()

        val result = jsonParser.decodeFromString<MessageCreateParams>(json)
        result.model shouldBe "claude-3-opus-latest"
        result.messages.first().content as MessageCreateParams.ContentList shouldNotBeNull {
            blocks shouldHaveSize 1
            blocks.first() as? MessageCreateParams.TextBlock shouldNotBeNull {
                text shouldBe "What is in the sea?"
            }
        }
    }
}
