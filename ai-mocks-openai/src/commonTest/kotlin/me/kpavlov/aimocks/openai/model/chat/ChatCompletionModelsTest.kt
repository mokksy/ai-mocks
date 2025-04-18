package me.kpavlov.aimocks.openai.model.chat

import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.openai.model.ChatCompletionRole
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class ChatCompletionModelsTest {
    private val jsonParser =
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

    @Test
    fun `Should deserialize ChatCompletionRequest`() {
        val json =
            """
            {
              "messages": [
                {
                  "role": "system",
                  "content": "You are a helpful assistant"
                },
                {
                  "role": "user",
                  "content": "Help me, please"
                }
              ],
              "model": "gpt-4o-mini",
              "response_format": {
                "type": "json_object"
              },
              "stream": false,
              "temperature": 0.7
            }
            """.trimIndent()

        val request = jsonParser.decodeFromString<ChatCompletionRequest>(json)

        assertEquals("gpt-4o-mini", request.model)
        assertEquals(0.7, request.temperature)
        assertEquals(false, request.stream)
        assertEquals(2, request.messages.size)
        assertEquals(ChatCompletionRole.SYSTEM, request.messages[0].role)
        assertEquals("You are a helpful assistant", request.messages[0].content)
        assertEquals(ChatCompletionRole.USER, request.messages[1].role)
        assertEquals("Help me, please", request.messages[1].content)
        assertNotNull(request.responseFormat)
        assertEquals("json_object", request.responseFormat?.type)
    }

    @Test
    fun `Should deserialize ChatResponse`() {
        val json =
            """
            {
              "id": "chatcmpl-123",
              "object": "chat.completions",
              "created": 1677858242,
              "model": "gpt-3.5-turbo-0613",
              "usage": {
                "prompt_tokens": 13,
                "completion_tokens": 7,
                "total_tokens": 20,
                "completion_tokens_details": {
                  "reasoning_tokens": 5,
                  "accepted_prediction_tokens": 1,
                  "rejected_prediction_tokens": 1
                }
              },
              "choices": [
                {
                  "message": {
                    "role": "assistant",
                    "content": "Hello! How can I help you today?"
                  },
                  "finish_reason": "stop",
                  "index": 0
                }
              ]
            }
            """.trimIndent()

        val response = jsonParser.decodeFromString<ChatResponse>(json)

        assertEquals("chatcmpl-123", response.id)
        assertEquals("chat.completions", response.objectType)
        assertEquals(1677858242, response.created)
        assertEquals("gpt-3.5-turbo-0613", response.model)
        assertEquals(13, response.usage.promptTokens)
        assertEquals(7, response.usage.completionTokens)
        assertEquals(20, response.usage.totalTokens)
        assertEquals(1, response.choices.size)
        assertEquals(0, response.choices[0].index)
        assertEquals("stop", response.choices[0].finishReason)
        assertNotNull(response.choices[0].message)
        assertEquals(ChatCompletionRole.ASSISTANT, response.choices[0].message?.role)
        assertEquals("Hello! How can I help you today?", response.choices[0].message?.content)
    }

    @Test
    fun `Should deserialize Chunk`() {
        val json =
            """
            {
              "id": "chatcmpl-123",
              "object": "chat.completion.chunk",
              "created": 1677858242,
              "model": "gpt-3.5-turbo-0613",
              "system_fingerprint": "fp_44709d6fcb",
              "choices": [
                {
                  "delta": {
                    "content": "Hello"
                  },
                  "index": 0,
                  "finish_reason": null
                }
              ]
            }
            """.trimIndent()

        val chunk = jsonParser.decodeFromString<Chunk>(json)

        assertEquals("chatcmpl-123", chunk.id)
        assertEquals("chat.completion.chunk", chunk.objectType)
        assertEquals(1677858242, chunk.created)
        assertEquals("gpt-3.5-turbo-0613", chunk.model)
        assertEquals("fp_44709d6fcb", chunk.systemFingerprint)
        assertEquals(1, chunk.choices.size)
        assertEquals(0, chunk.choices[0].index)
        assertNotNull(chunk.choices[0].delta)
        assertEquals("Hello", chunk.choices[0].delta?.content)
    }

    @Test
    fun `Should deserialize Message`() {
        val json =
            """
            {
              "role": "assistant",
              "content": "Hello! How can I help you today?"
            }
            """.trimIndent()

        val message = jsonParser.decodeFromString<Message>(json)

        assertEquals(ChatCompletionRole.ASSISTANT, message.role)
        assertEquals("Hello! How can I help you today?", message.content)
    }

    @Test
    fun `Should deserialize Tool`() {
        val json =
            """
            {
              "type": "function",
              "function": {
                "name": "get_weather",
                "description": "Get the current weather in a given location",
                "parameters": {
                  "location": "The city and state, e.g. San Francisco, CA",
                  "unit": "The temperature unit to use. Infer this from the user's location."
                }
              }
            }
            """.trimIndent()

        val tool = jsonParser.decodeFromString<Tool>(json)

        assertEquals("function", tool.type)
        assertEquals("get_weather", tool.function.name)
        assertEquals("Get the current weather in a given location", tool.function.description)
        assertEquals(2, tool.function.parameters?.size)
    }
}
