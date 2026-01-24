package dev.mokksy.aimocks.ollama.model

import dev.mokksy.aimocks.ollama.chat.ChatRequest
import dev.mokksy.aimocks.ollama.chat.ChatResponse
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.schema.json.StringPropertyDefinition
import kotlin.test.Test
import kotlin.time.Instant

/**
 * Tests for the serialization and deserialization of chat models.
 */
internal class ChatModelsTest : AbstractSerializationTest() {
    @Test
    fun `Deserialize and Serialize ChatRequest`() {
        // language=json
        val payload =
            """
            {
              "model": "llama3.2",
              "messages": [
                {
                  "role": "system",
                  "content": "You are a helpful assistant."
                },
                {
                  "role": "user",
                  "content": "Why is the sky blue?"
                }
              ],
              "format": "json",
              "options": {
                "temperature": 0.7,
                "top_p": 0.9
              },
              "stream": false,
              "keep_alive": "10m"
            }
            """.trimIndent()

        val model = deserializeAndSerialize<ChatRequest>(payload)
        model.model shouldBe "llama3.2"
        model.messages.size shouldBe 2
        model.messages[0].role shouldBe "system"
        model.messages[0].content shouldBe "You are a helpful assistant."
        model.messages[1].role shouldBe "user"
        model.messages[1].content shouldBe "Why is the sky blue?"
        model.format.shouldBeInstanceOf<Format.Json>()
        model.options?.temperature shouldBe 0.7
        model.options?.topP shouldBe 0.9
        model.stream shouldBe false
        model.keepAlive shouldBe "10m"
    }

    @Test
    fun `Deserialize and Serialize ChatRequest 2`() {
        // language=json
        val payload =
            """
            {
              "model" : "mistral",
              "messages" : [ {
                "role" : "user",
                "content" : "Hello"
              } ],
              "options" : {
                "temperature" : 0.40528726585876296,
                "top_p" : 0.40024988370637504,
                "top_k" : 123,
                "stop" : [ ]
              },
              "stream" : true,
              "tools" : [ ]
            }
            """.trimIndent()

        val model = deserializeAndSerialize<ChatRequest>(payload)
        model shouldNotBeNull {
            stream shouldBe true
            options shouldNotBeNull {
                stop?.shouldHaveSize(0)
                temperature shouldBe 0.40528726585876296
                topP shouldBe 0.40024988370637504
                topK shouldBe 123
            }
        }
    }

    @Test
    fun `Deserialize and Serialize ChatRequest 3`() {
        // language=json
        val payload =
            """
            {
              "model" : "mistral",
              "messages" : [ {
                "role" : "user",
                "content" : "Hello"
              } ],
              "options" : {
                "stop" : [ ]
              },
              "stream" : true,
              "tools" : [ ]
            }
            """.trimIndent()

        val model = deserializeAndSerialize<ChatRequest>(payload)
        model shouldNotBeNull {
            stream shouldBe true
            options shouldNotBeNull {
                stop?.size shouldBe 0
                temperature shouldBe null
                topP shouldBe null
                topK shouldBe null
            }
        }
    }

    @Test
    fun `Deserialize and Serialize ChatRequest with tools`() {
        // language=json
        val payload =
            """
            {
              "model": "llama3.2",
              "messages": [
                {
                  "role": "user",
                  "content": "what is the weather in tokyo?"
                }
              ],
              "tools": [
                {
                  "type": "function",
                  "function": {
                    "name": "get_weather",
                    "description": "Get the weather in a given city",
                    "parameters": {
                      "type": "object",
                      "properties": {
                        "city": {
                          "type": "string",
                          "description": "The city to get the weather for"
                        }
                      },
                      "required": ["city"]
                    }
                  }
                }
              ],
              "stream": true
            }
            """.trimIndent()

        val model = deserializeAndSerialize<ChatRequest>(payload)
        model.model shouldBe "llama3.2"
        model.messages.size shouldBe 1
        model.messages[0].role shouldBe "user"
        model.messages[0].content shouldBe "what is the weather in tokyo?"
        model.tools?.size shouldBe 1
        model.tools?.get(0)?.type shouldBe "function"
        model.tools
            ?.get(0)
            ?.function
            ?.name shouldBe "get_weather"
        model.tools
            ?.get(0)
            ?.function
            ?.description shouldBe "Get the weather in a given city"

        // Check SchemaDefinition properties
        val parameters =
            model.tools
                ?.get(0)
                ?.function
                ?.parameters
        parameters?.type shouldBe arrayOf("object")
        parameters?.properties?.size shouldBe 1
        parameters
            ?.properties
            ?.get("city")
            ?.shouldBeInstanceOf<StringPropertyDefinition>()
        val cityProperty =
            parameters?.properties?.get(
                "city",
            ) as StringPropertyDefinition
        cityProperty.description shouldBe "The city to get the weather for"
        parameters.required shouldBe listOf("city")

        model.stream shouldBe true
    }

    @Test
    fun `Deserialize and Serialize ChatResponse`() {
        // language=json
        val payload =
            """
            {
              "model": "llama3.2",
              "created_at": "2023-08-04T19:22:45.499127Z",
              "message": {
                "role": "assistant",
                "content": "The sky appears blue because of a Rayleigh scattering."
              },
              "done": true,
              "total_duration": 4883583458,
              "load_duration": 1334875,
              "prompt_eval_count": 26,
              "prompt_eval_duration": 342546000,
              "eval_count": 282,
              "eval_duration": 4535599000
            }
            """.trimIndent()

        val model = deserializeAndSerialize<ChatResponse>(payload)
        model.model shouldBe "llama3.2"
        model.createdAt shouldBe Instant.parse("2023-08-04T19:22:45.499127Z")
        model.message.role shouldBe "assistant"
        model.message.content shouldBe "The sky appears blue because of a Rayleigh scattering."
        model.done shouldBe true
        model.totalDuration shouldBe 4883583458
        model.loadDuration shouldBe 1334875
        model.promptEvalCount shouldBe 26
        model.promptEvalDuration shouldBe 342546000
        model.evalCount shouldBe 282
        model.evalDuration shouldBe 4535599000
    }

    @Test
    fun `Deserialize and Serialize ChatResponse with tool calls`() {
        // language=json
        val payload =
            """
            {
              "model": "llama3.2",
              "created_at": "2025-07-07T20:22:19.184789Z",
              "message": {
                "role": "assistant",
                "content": "",
                "tool_calls": [
                  {
                    "id": "call_123",
                    "type": "function",
                    "function": {
                      "name": "get_weather",
                      "arguments": "{\"city\":\"Tokyo\"}"
                    }
                  }
                ]
              },
              "done": true,
              "total_duration": 4883583458,
              "load_duration": 1334875,
              "prompt_eval_count": 26,
              "prompt_eval_duration": 342546000,
              "eval_count": 282,
              "eval_duration": 4535599000
            }
            """.trimIndent()

        val model = deserializeAndSerialize<ChatResponse>(payload)
        model.model shouldBe "llama3.2"
        model.createdAt shouldBe Instant.parse("2025-07-07T20:22:19.184789Z")
        model.message.role shouldBe "assistant"
        model.message.content shouldBe ""
        model.message.toolCalls?.size shouldBe 1
        model.message.toolCalls
            ?.get(0)
            ?.id shouldBe "call_123"
        model.message.toolCalls
            ?.get(0)
            ?.type shouldBe "function"
        model.message.toolCalls
            ?.get(0)
            ?.function
            ?.name shouldBe "get_weather"
        model.message.toolCalls
            ?.get(0)
            ?.function
            ?.arguments shouldBe "{\"city\":\"Tokyo\"}"
        model.done shouldBe true
    }
}
