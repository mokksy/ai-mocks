package dev.mokksy.aimocks.openai.model.responses

import dev.mokksy.test.utils.deserializeAndSerialize
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.Test

internal class ResponseModelsTest {
    private val jsonParser =
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

    @Test
    fun `Should deserialize CreateResponseRequest`() {
        val json =
            """
            {
              "model": "gpt-4o",
              "input": "What is the capital of France?",
              "temperature": 0.7,
              "top_p": 0.9,
              "max_output_tokens": 100,
              "stream": false
            }
            """.trimIndent()

        val request = jsonParser.decodeFromString<CreateResponseRequest>(json)

        request.model shouldBe "gpt-4o"
        request.temperature shouldBe 0.7
        request.topP shouldBe 0.9
        request.maxOutputTokens shouldBe 100
        request.stream shouldBe false
        request.input.shouldNotBeNull().shouldBeInstanceOf<Text>()
        (request.input as Text).text shouldBe "What is the capital of France?"
    }

    @Test
    fun `Should deserialize CreateResponseRequest with Text input`() {
        val json =
            """
            {
              "model": "gpt-4o",
              "input": "What is the capital of France?",
              "temperature": 0.7
            }
            """.trimIndent()

        val request = jsonParser.decodeFromString<CreateResponseRequest>(json)

        request.model shouldBe "gpt-4o"
        request.temperature shouldBe 0.7
        request.input.shouldNotBeNull().shouldBeInstanceOf<Text>()
        (request.input as Text).text shouldBe "What is the capital of France?"
    }

    @Test
    fun `Should deserialize CreateResponseRequest with Schema`() {
        // language=json
        val json =
            $$"""
           {"input":"Create a red circle with radius 5.5 named 'MyCircle'",
           "instructions":"Generate a circle based on user requirements. Return valid JSON matching the schema.",
           "max_output_tokens":500,
           "model":"gpt-4o-2024-08-06",
           "temperature":0.7,
           "text":{
             "format":{
               "name":"json-schema-from-Circle",
               "schema":{
                 "$schema":"https://json-schema.org/draft/2020-12/schema",
                 "type":"object",
                 "properties":{
                   "color":{"type":"string"},
                   "name":{"type":"string"},
                   "radius":{"type":"number"}
                 },
                 "required":["color","name","radius"],
                 "additionalProperties":false},
                 "type":"json_schema",
                 "strict":true
               }
             }
           }
            """.trimIndent()

        val request = deserializeAndSerialize<CreateResponseRequest>(json)

        request.model shouldBe "gpt-4o-2024-08-06"
        request.temperature shouldBe 0.7
        request.maxOutputTokens shouldBe 500
        request.instructions shouldBe
            "Generate a circle based on user requirements. Return valid JSON matching the schema."
        request.input.shouldNotBeNull().shouldBeInstanceOf<Text>()
        (request.input as Text).text shouldBe "Create a red circle with radius 5.5 named 'MyCircle'"

        // Verify text configuration
        request.text.shouldNotBeNull {
            format.shouldNotBeNull {
                type shouldBe "json_schema"
                name shouldBe "json-schema-from-Circle"
                strict shouldBe true
                schema.shouldNotBeNull {
                    schema shouldBe "https://json-schema.org/draft/2020-12/schema"
                    type shouldBe listOf("object")
                    required shouldBe listOf("color", "name", "radius")
                    additionalProperties shouldBe JsonPrimitive(false)
                    properties shouldHaveSize 3
                    properties.keys shouldBe setOf("color", "name", "radius")
                }
            }
        }
    }

    @Test
    fun `Should deserialize CreateResponseRequest with InputItems`() {
        // language=json
        val json =
            """
            {
              "model": "gpt-4o",
              "input": [
                {
                  "role": "user",
                  "content": [
                    {
                      "type": "input_text",
                      "text": "What is the capital of France?"
                    }
                  ]
                }
              ],
              "temperature": 0.7
            }
            """.trimIndent()

        val request = deserializeAndSerialize<CreateResponseRequest>(json)

        request.model shouldBe "gpt-4o"
        request.temperature shouldBe 0.7
        request.input.shouldNotBeNull()
        // Note: We can't directly check the input type here due to the serialization approach
    }

    /**
     * See https://platform.openai.com/docs/api-reference/responses/object
     */
    @Test
    @Suppress("LongMethod")
    fun `Should deserialize Response`() {
        // language=JSON
        val json =
            """
            {
              "id": "resp_67ccd3a9da748190baa7f1570fe91ac604becb25c45c1d41",
              "object": "response",
              "created_at": 1741476777,
              "status": "completed",
              "error": null,
              "incomplete_details": null,
              "instructions": null,
              "max_output_tokens": null,
              "model": "gpt-4o-2024-08-06",
              "output": [
                {
                  "type": "message",
                  "id": "msg_67ccd3acc8d48190a77525dc6de64b4104becb25c45c1d41",
                  "status": "completed",
                  "role": "assistant",
                  "content": [
                    {
                      "type": "output_text",
                      "text": "The image depicts a scenic landscape with a wooden boardwalk or pathway leading through lush, green grass under a blue sky with some clouds. The setting suggests a peaceful natural area, possibly a park or nature reserve. There are trees and shrubs in the background.",
                      "annotations": []
                    }
                  ]
                }
              ],
              "parallel_tool_calls": true,
              "previous_response_id": null,
              "reasoning": {
                "effort": null,
                "generate_summary": null
              },
              "store": true,
              "temperature": 1.0,
              "text": {
                "format": {
                  "type": "text"
                }
              },
              "tool_choice": "auto",
              "tools": [],
              "top_p": 1.0,
              "truncation": "disabled",
              "usage": {
                "input_tokens": 328,
                "input_tokens_details": {
                  "cached_tokens": 0
                },
                "output_tokens": 52,
                "output_tokens_details": {
                  "reasoning_tokens": 0
                },
                "total_tokens": 380
              },
              "user": null,
              "metadata": {}
            }
            """.trimIndent()

        val response: Response = deserializeAndSerialize(json)

        // Basic fields
        response.id shouldBe "resp_67ccd3a9da748190baa7f1570fe91ac604becb25c45c1d41"
        response.objectType shouldBe "response"
        response.createdAt shouldBe 1741476777
        response.model shouldBe "gpt-4o-2024-08-06"
        response.temperature shouldBe 1.0
        response.topP shouldBe 1.0

        // Status and control fields
        response.status shouldBe Response.Status.COMPLETED
        response.parallelToolCalls shouldBe true
        response.store shouldBe true
        response.toolChoice shouldBe "auto"

        // Nullable fields
        response.error.shouldBeNull()
        response.incompleteDetails.shouldBeNull()
        response.instructions.shouldBeNull()
        response.maxOutputTokens.shouldBeNull()
        response.previousResponseId.shouldBeNull()
        response.user.shouldBeNull()

        // Truncation
        response.truncation shouldBe Truncation.DISABLED

        // Metadata
        response.metadata.shouldNotBeNull() shouldHaveSize 0

        // Tools
        response.tools shouldHaveSize 0

        // Reasoning
        response.reasoning.shouldNotBeNull {
            effort.shouldBeNull()
            generateSummary.shouldBeNull()
        }

        // Text configuration
        response.text.shouldNotBeNull {
            format.shouldNotBeNull {
                type shouldBe "text"
                name.shouldBeNull()
                schema.shouldBeNull()
                strict.shouldBeNull()
            }
        }

        // Output
        response.output shouldHaveSize 1
        response.output[0].shouldNotBeNull()

        // Usage
        response.usage.shouldNotBeNull {
            inputTokens shouldBe 328
            outputTokens shouldBe 52
            totalTokens shouldBe 380
            inputTokensDetails.cachedTokens shouldBe 0
            outputTokensDetails.reasoningTokens shouldBe 0
        }
    }

    @Test
    fun `Should deserialize Usage`() {
        // language=json
        val json =
            """
            {
              "input_tokens": 10,
              "input_tokens_details": {
                "cached_tokens": 5
              },
              "output_tokens": 8,
              "output_tokens_details": {
                "reasoning_tokens": 8
              },
              "total_tokens": 18
            }
            """.trimIndent()

        val usage = deserializeAndSerialize<Usage>(json)

        usage.inputTokens shouldBe 10
        usage.outputTokens shouldBe 8
        usage.totalTokens shouldBe 18
        usage.inputTokensDetails.cachedTokens shouldBe 5
        usage.outputTokensDetails.reasoningTokens shouldBe 8
    }

    @Test
    fun `Should deserialize InputTokensDetails`() {
        // language=json
        val json =
            """
            {
              "cached_tokens": 5
            }
            """.trimIndent()

        val details = deserializeAndSerialize<InputTokensDetails>(json)

        details.cachedTokens shouldBe 5
    }

    @Test
    fun `Should deserialize OutputTokensDetails`() {
        // language=json
        val json =
            """
            {
              "reasoning_tokens": 8
            }
            """.trimIndent()

        val details = jsonParser.decodeFromString<OutputTokensDetails>(json)

        details.reasoningTokens shouldBe 8
    }

    @Test
    fun `Should deserialize IncompleteDetails`() {
        // language=json
        val json =
            """
            {
              "reason": "max_tokens_exceeded"
            }
            """.trimIndent()

        val details = deserializeAndSerialize<IncompleteDetails>(json)

        details.reason shouldBe "max_tokens_exceeded"
    }

    @Test
    fun `Should deserialize TextConfig with simple text format`() {
        // language=json
        val json =
            """
            {
              "format": {
                "type": "text"
              }
            }
            """.trimIndent()

        val textConfig = deserializeAndSerialize<TextConfig>(json, jsonParser)

        textConfig.format.shouldNotBeNull {
            type shouldBe "text"
            name.shouldBeNull()
            schema.shouldBeNull()
            strict.shouldBeNull()
        }
    }

    @Test
    fun `Should deserialize TextConfig with json_schema format`() {
        // language=json
        val json =
            $$"""
            {
              "format": {
                "type": "json_schema",
                "name": "test-schema",
                "strict": true,
                "schema": {
                  "$schema": "https://json-schema.org/draft/2020-12/schema",
                  "type": "object",
                  "properties": {
                    "field1": {"type": "string"}
                  },
                  "required": ["field1"]
                }
              }
            }
            """.trimIndent()

        val textConfig = deserializeAndSerialize<TextConfig>(json, jsonParser)

        textConfig.format.shouldNotBeNull {
            type shouldBe "json_schema"
            name shouldBe "test-schema"
            strict shouldBe true
            schema.shouldNotBeNull {
                schema shouldBe "https://json-schema.org/draft/2020-12/schema"
                type shouldBe listOf("object")
                required shouldBe listOf("field1")
                properties shouldHaveSize 1
            }
        }
    }

    @Test
    fun `Should deserialize TextFormat with type only`() {
        // language=json
        val json =
            """
            {
              "type": "text"
            }
            """.trimIndent()

        val textFormat = deserializeAndSerialize<TextFormat>(json, jsonParser)

        textFormat.type shouldBe "text"
        textFormat.name.shouldBeNull()
        textFormat.schema.shouldBeNull()
        textFormat.strict.shouldBeNull()
    }

    @Test
    fun `Should deserialize TextFormat with all fields`() {
        // language=json
        val json =
            $$"""
            {
              "type": "json_schema",
              "name": "complete-schema",
              "strict": false,
              "schema": {
                "$schema": "https://json-schema.org/draft/2020-12/schema",
                "type": "string"
              }
            }
            """.trimIndent()

        val textFormat = deserializeAndSerialize<TextFormat>(json, jsonParser)

        textFormat.type shouldBe "json_schema"
        textFormat.name shouldBe "complete-schema"
        textFormat.strict shouldBe false
        textFormat.schema.shouldNotBeNull {
            schema shouldBe "https://json-schema.org/draft/2020-12/schema"
            type shouldBe listOf("string")
        }
    }
}
