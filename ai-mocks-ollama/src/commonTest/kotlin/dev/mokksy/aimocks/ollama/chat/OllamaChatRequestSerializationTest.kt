package dev.mokksy.aimocks.ollama.chat

import dev.mokksy.test.utils.deserializeAndSerialize
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test

internal class OllamaChatRequestSerializationTest {
    private val json = Json { ignoreUnknownKeys = false }

    // MockOllama's exact JSON configuration

    @Test
    fun `Should parse SpringAI format correctly`() {
        val springAiJson =
            """
            {
              "model": "llama3.2",
              "messages": [{
                "role": "system",
                "content": "You are a helpful pirate"
              }, {
                "role": "user",
                "content": "Just say 'Hello!'"
              }],
              "stream": true,
              "tools": [],
              "options": {
                "top_p": 0.30994638421186493,
                "seed": 32668,
                "top_k": 404,
                "temperature": 0.30081615737430945
              }
            }
            """.trimIndent()

        val request = json.decodeFromString<ChatRequest>(springAiJson)
        request shouldNotBe null
        request.model shouldBe "llama3.2"
        request.messages.size shouldBe 2
        request.stream shouldBe true
        request.options shouldNotBeNull {
            temperature shouldBe (0.30081615737430945 plusOrMinus 1e-12)
            topK shouldBe 404
            topP shouldBe (0.30994638421186493 plusOrMinus 1e-12)
        }
    }

    @Test
    fun `Should parse Langchain4j format correctly`() {
        val langchain4jJson =
            """
            {
              "model": "llama3.1",
              "messages": [{
                "role": "user",
                "content": "Hello"
              }],
              "options": {
                "temperature": 0.015273115109424307,
                "top_k": 721,
                "top_p": 0.4836426825417919,
                "stop": []
              },
              "stream": true,
              "tools": []
            }
            """.trimIndent()

        val request = json.decodeFromString<ChatRequest>(langchain4jJson)
        request shouldNotBe null
        request.model shouldBe "llama3.1"
        request.messages.size shouldBe 1
        request.stream shouldBe true
        request.options shouldNotBeNull {
            temperature shouldBe (0.015273115109424307 plusOrMinus 1e-12)
            topK shouldBe 721
            topP shouldBe (0.4836426825417919 plusOrMinus 1e-12)
            stop shouldBe emptyList()
        }
    }

    @Test
    fun `Should parse Langchain4j format with MockOllama JSON config`() {
        val langchain4jJson =
            """
            {
              "model": "llama3.1",
              "messages": [{
                "role": "user",
                "content": "Hello"
              }],
              "options": {
                "temperature": 0.015273115109424307,
                "top_k": 721,
                "top_p": 0.4836426825417919,
                "stop": []
              },
              "stream": true,
              "tools": []
            }
            """.trimIndent()

        val request = json.decodeFromString<ChatRequest>(langchain4jJson)
        request shouldNotBe null
        request.model shouldBe "llama3.1"
        request.messages.size shouldBe 1
        request.stream shouldBe true
        request.options shouldNotBeNull {
            temperature shouldBe (0.015273115109424307 plusOrMinus 1e-12)
            topK shouldBe 721
            topP shouldBe (0.4836426825417919 plusOrMinus 1e-12)
            stop shouldBe emptyList()
        }
    }

    @Test
    fun `Should parse tool call arguments as JSON object`() {
        val responseJson =
            """
            {
              "model": "llama3.2",
              "created_at": "2025-07-07T20:32:53.844124Z",
              "message": {
                "role": "assistant",
                "content": "",
                "tool_calls": [
                  {
                    "function": {
                      "name": "get_weather",
                      "arguments": {
                        "city": "Tokyo"
                      }
                    }
                  }
                ]
              },
              "done_reason": "stop",
              "done": true
            }
            """.trimIndent()

        val response = deserializeAndSerialize<ChatResponse>(responseJson, json)
        val toolCall =
            response.message.toolCalls
                .shouldNotBeNull()
                .single()

        toolCall.id shouldBe null
        toolCall.function.name shouldBe "get_weather"
        toolCall.function.arguments shouldBe
            buildJsonObject {
                put("city", "Tokyo")
            }
    }
}
