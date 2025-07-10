package me.kpavlov.aimocks.openai.model.chat

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.core.json.schema.StringPropertyDefinition
import me.kpavlov.aimocks.openai.model.ChatCompletionRole
import org.junit.jupiter.api.Test

internal class ChatCompletionModelsTest {
    private val jsonParser =
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

    @Test
    @Suppress("MaxLineLength")
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

        assertSoftly(request) {
            model shouldBe "gpt-4o-mini"
            temperature shouldBe 0.7
            stream shouldBe false
            messages shouldHaveSize 2

            withClue("System message should be correct") {
                messages[0].role shouldBe ChatCompletionRole.SYSTEM
                messages[0].content shouldBe "You are a helpful assistant"
            }

            withClue("User message should be correct") {
                messages[1].role shouldBe ChatCompletionRole.USER
                messages[1].content shouldBe "Help me, please"
            }

            withClue("Response format should be present and correct") {
                responseFormat.shouldNotBeNull()
                responseFormat.type shouldBe "json_object"
            }
        }
    }

    @Test
    @Suppress("LongMethod")
    fun `Should deserialize ChatCompletionRequest with JsonSchema`() {
        // language=json
        val json =
            """
            {
              "model" : "gpt-4.1-nano",
              "messages" : [ {
                "role" : "system",
                "content" : "Convert person to JSON"
              }, {
                "role" : "user",
                "content" : "Bob is 25 years old and weighs 0.075 tonnes.\nHis height is one meter eighty-five centimeters.\nHe is married."
              } ],
              "temperature" : 0.7,
              "stream" : false,
              "max_completion_tokens" : 100,
              "response_format" : {
                "type" : "json_schema",
                "json_schema" : {
                  "name" : "Person",
                  "strict" : false,
                  "schema" : {
                    "type" : "object",
                    "properties" : {
                      "name" : {
                        "type" : "string",
                        "nullable": false,
                        "description" : "Person's name"
                      },
                      "age" : {
                        "type" : "integer",
                        "description" : "Person's age"
                      },
                      "weight" : {
                        "type" : "number",
                        "nullable": true,
                        "description" : "Weight in kilograms"
                      },
                      "height" : {
                        "type" : "number",
                        "description" : "Height in meters"
                      },
                      "married" : {
                        "type" : "boolean"
                      }
                    },
                    "required" : [ "name",  "age", "weight", "height", "married" ]
                  }
                }
              }
            }
            """.trimIndent()

        val request = jsonParser.decodeFromString<ChatCompletionRequest>(json)

        assertSoftly(request) {
            model shouldBe "gpt-4.1-nano"
            messages shouldHaveSize 2

            withClue("System message should be correct") {
                messages[0].role shouldBe ChatCompletionRole.SYSTEM
                messages[0].content shouldBe "Convert person to JSON"
            }

            withClue("User message should be correct") {
                messages[1].role shouldBe ChatCompletionRole.USER
                messages[1].content shouldBe
                    "Bob is 25 years old and weighs 0.075 tonnes.\nHis height is one meter eighty-five centimeters.\nHe is married."
            }

            temperature shouldBe 0.7
            stream shouldBe false
            maxCompletionTokens shouldBe 100

            withClue("Response format should be present and correct") {
                responseFormat.shouldNotBeNull {
                    type shouldBe "json_schema"

                    withClue("JSON schema should be present and correct") {
                        jsonSchema.shouldNotBeNull {
                            name shouldBe "Person"
                            strict shouldBe false

                            withClue("Schema definition should be correct") {
                                schema.shouldNotBeNull {
                                    type shouldBe "object"

                                    withClue("Schema properties should be correct") {
                                        properties.shouldNotBeNull {
                                            this.shouldHaveSize(5)

                                            withClue("Name property should be correct") {
                                                this["name"] as? StringPropertyDefinition shouldNotBeNull {
                                                    type shouldBe listOf("string")
                                                    nullable shouldBe false
                                                    description shouldBe "Person's name"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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

        assertSoftly(response) {
            id shouldBe "chatcmpl-123"
            objectType shouldBe "chat.completions"
            created shouldBe 1677858242
            model shouldBe "gpt-3.5-turbo-0613"

            withClue("Usage statistics should be correct") {
                assertSoftly(usage) {
                    promptTokens shouldBe 13
                    completionTokens shouldBe 7
                    totalTokens shouldBe 20
                }
            }

            choices shouldHaveSize 1

            withClue("First choice should be correct") {
                assertSoftly(choices[0]) {
                    index shouldBe 0
                    finishReason shouldBe "stop"

                    withClue("Message should be present and correct") {
                        message.shouldNotBeNull()
                        message?.role shouldBe ChatCompletionRole.ASSISTANT
                        message?.content shouldBe "Hello! How can I help you today?"
                    }
                }
            }
        }
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

        assertSoftly(chunk) {
            id shouldBe "chatcmpl-123"
            objectType shouldBe "chat.completion.chunk"
            created shouldBe 1677858242
            model shouldBe "gpt-3.5-turbo-0613"
            systemFingerprint shouldBe "fp_44709d6fcb"
            choices shouldHaveSize 1

            withClue("First choice should be correct") {
                assertSoftly(choices[0]) {
                    index shouldBe 0

                    withClue("Delta should be present and correct") {
                        delta.shouldNotBeNull()
                        delta?.content shouldBe "Hello"
                    }
                }
            }
        }
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

        assertSoftly(message) {
            role shouldBe ChatCompletionRole.ASSISTANT
            content shouldBe "Hello! How can I help you today?"
        }
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

        assertSoftly(tool) {
            type shouldBe "function"

            withClue("Function should be correct") {
                assertSoftly(function) {
                    name shouldBe "get_weather"
                    description shouldBe "Get the current weather in a given location"
                    parameters?.size shouldBe 2
                }
            }
        }
    }
}
