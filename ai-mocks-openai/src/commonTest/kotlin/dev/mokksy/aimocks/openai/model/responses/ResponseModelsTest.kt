package dev.mokksy.aimocks.openai.model.responses

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
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
        request.input.shouldNotBeNull()
        // Note: We can't directly check the input type here due to the serialization approach
    }

    @Test
    fun `Should deserialize CreateResponseRequest with InputItems`() {
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

        val request = jsonParser.decodeFromString<CreateResponseRequest>(json)

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

        val response = jsonParser.decodeFromString<Response>(json)

        response.id shouldBe "resp_67ccd3a9da748190baa7f1570fe91ac604becb25c45c1d41"
    }

    @Test
    fun `Should deserialize Usage`() {
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

        val usage = jsonParser.decodeFromString<Usage>(json)

        usage.inputTokens shouldBe 10
        usage.outputTokens shouldBe 8
        usage.totalTokens shouldBe 18
        usage.inputTokensDetails.cachedTokens shouldBe 5
        usage.outputTokensDetails.reasoningTokens shouldBe 8
    }

    @Test
    fun `Should deserialize InputTokensDetails`() {
        val json =
            """
            {
              "cached_tokens": 5
            }
            """.trimIndent()

        val details = jsonParser.decodeFromString<InputTokensDetails>(json)

        details.cachedTokens shouldBe 5
    }

    @Test
    fun `Should deserialize OutputTokensDetails`() {
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
        val json =
            """
            {
              "reason": "max_tokens_exceeded"
            }
            """.trimIndent()

        val details = jsonParser.decodeFromString<IncompleteDetails>(json)

        details.reason shouldBe "max_tokens_exceeded"
    }
}
