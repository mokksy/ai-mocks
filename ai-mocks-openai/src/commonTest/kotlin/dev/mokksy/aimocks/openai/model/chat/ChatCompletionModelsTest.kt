package dev.mokksy.aimocks.openai.model.chat

import dev.mokksy.aimocks.openai.model.ChatCompletionRole
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.instanceOf
import kotlinx.schema.json.StringPropertyDefinition
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Test

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

        assertSoftly(request) {
            model shouldBe "gpt-4o-mini"
            temperature shouldBe 0.7
            stream shouldBe false
            messages shouldHaveSize 2

            withClue("System message should be correct") {
                messages[0].role shouldBe ChatCompletionRole.SYSTEM
                messages[0].content shouldBe MessageContent.Text("You are a helpful assistant")
            }

            withClue("User message should be correct") {
                messages[1].role shouldBe ChatCompletionRole.USER
                messages[1].content shouldBe MessageContent.Text("Help me, please")
            }

            withClue("Response format should be present and correct") {
                responseFormat.shouldNotBeNull()
                responseFormat.type shouldBe "json_object"
            }
        }
    }

    @Test
    fun `Should deserialize ChatCompletionRequest with single tool`() {
        val systemMessage = "You're a witty and wise assistant.\nYou are built with **RAG**"
        val json =
            """
            {"messages":[
            {"role":"system","content":"$systemMessage"},
            {"role":"user","content":[
              {"type":"text","text":"User's input: ```To be or not to be?```.\nUse attachment as relevant context"},
              {"type":"text","text":"# Magical Bow\n\nThe magical bow shoots not ordinary arrows but light charges."}
            ]},
            {"role":"user","content":"To be or not to be, 82460322?"}
            ],
            "model":"gpt-4.1-mini","stream":false,
            "tools":[
              {"function":{
                "name":"stockPrice",
                "description":"Returns stock price of given symbol",
                "parameters":{
                  "type":"object",
                  "properties":{
                    "symbol":{"description":"Stock symbol, e.g. APPL","type":"string"}},
                    "required":["symbol"]}
                },
                "type":"function"}
            ]
            }
            """.trimIndent()

        val request = jsonParser.decodeFromString<ChatCompletionRequest>(json)

        assertSoftly(request) {
            model shouldBe "gpt-4.1-mini"
            temperature shouldBe 1.0
            stream shouldBe false
            messages shouldHaveSize 3

            withClue("System message should be correct") {
                messages[0].role shouldBe ChatCompletionRole.SYSTEM
                messages[0].content shouldBe MessageContent.Text(systemMessage)
            }

            withClue("First user message should be correct") {
                messages[1].role shouldBe ChatCompletionRole.USER
                val expectedContent = messages[1].content
                expectedContent.asText() shouldBe
                    "User's input: ```To be or not to be?```.\nUse attachment as relevant context # Magical Bow\n\nThe magical bow shoots not ordinary arrows but light charges."
            }

            withClue("Second user message should be correct") {
                messages[2].role shouldBe ChatCompletionRole.USER
                messages[2].content shouldBe MessageContent.Text("To be or not to be, 82460322?")
            }

            withClue("Tools should be present") {
                tools.shouldNotBeNull()
                tools shouldHaveSize 1

                withClue("Tool should be stockPrice function") {
                    tools[0].type shouldBe "function"
                    tools[0].function.name shouldBe "stockPrice"
                    tools[0].function.description shouldBe "Returns stock price of given symbol"
                }
            }
        }
    }

    @Test
    fun `Should deserialize ChatCompletionRequest with multiple tools`() {
        val json =
            """
            {"messages":[
            {"content":"You are a helpful assistant.","role":"system"},
            {"content":"What's the weather and current time?","role":"user"}],
            "model":"o1-mini",
            "max_completion_tokens":102,
            "seed":81619,
            "temperature":0.9086965130658635,
            "tools":[
            {"function":{"name":"get_weather","description":"Get the current weather in a given location","parameters":{"type":"object","properties":{"location":{"type":"string","description":"The city and state, e.g. San Francisco, CA"}},"required":["location"]}},"type":"function"},
            {"function":{"name":"get_current_time","description":"Get the current time in a given timezone","parameters":{"type":"object","properties":{"timezone":{"type":"string","description":"The timezone, e.g. America/New_York"}},"required":["timezone"]}},"type":"function"}
            ]}
            """.trimIndent()

        val request = jsonParser.decodeFromString<ChatCompletionRequest>(json)

        assertSoftly(request) {
            model shouldBe "o1-mini"
            temperature shouldBe 0.9086965130658635
            stream shouldBe false
            messages shouldHaveSize 2
            maxCompletionTokens shouldBe 102
            seed shouldBe 81619

            withClue("System message should be correct") {
                messages[0].role shouldBe ChatCompletionRole.SYSTEM
                messages[0].content shouldBe MessageContent.Text("You are a helpful assistant.")
            }

            withClue("User message should be correct") {
                messages[1].role shouldBe ChatCompletionRole.USER
                messages[1].content shouldBe
                    MessageContent.Text("What's the weather and current time?")
            }

            withClue("Tools should be present") {
                tools.shouldNotBeNull()
                tools shouldHaveSize 2

                withClue("First tool should be weather function") {
                    tools[0].type shouldBe "function"
                    tools[0].function.name shouldBe "get_weather"
                    tools[0].function.description shouldBe
                        "Get the current weather in a given location"
                }

                withClue("Second tool should be time function") {
                    tools[1].type shouldBe "function"
                    tools[1].function.name shouldBe "get_current_time"
                    tools[1].function.description shouldBe
                        "Get the current time in a given timezone"
                }
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
                messages[0].content shouldBe MessageContent.Text("Convert person to JSON")
            }

            withClue("User message should be correct") {
                messages[1].role shouldBe ChatCompletionRole.USER
                messages[1].content shouldBe
                    MessageContent.Text(
                        "Bob is 25 years old and weighs 0.075 tonnes.\nHis height is one meter eighty-five centimeters.\nHe is married.",
                    )
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
                                                this["name"] as? StringPropertyDefinition shouldNotBeNull
                                                    {
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

                    withClue("Completion tokens details should be correct") {
                        assertSoftly(completionTokensDetails) {
                            reasoningTokens shouldBe 5
                            acceptedPredictionTokens shouldBe 1
                            rejectedPredictionTokens shouldBe 1
                        }
                    }
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
                        message?.content shouldBe
                            MessageContent.Text("Hello! How can I help you today?")
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
                    finishReason shouldBe null

                    withClue("Delta should be present and correct") {
                        delta.shouldNotBeNull()
                        delta.content shouldBe "Hello"
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
            content shouldBe MessageContent.Text("Hello! How can I help you today?")
        }
    }

    @Test
    fun `Should deserialize Message with array of content parts`() {
        val json =
            """
            {
              "role": "assistant",
              "content": [
                {
                  "type": "output_text",
                  "text": "Under the soft glow of the moon, Luna the unicorn danced through fields of twinkling stardust, leaving trails of dreams for every child asleep.",
                  "annotations": []
                }
              ]
            }
            """.trimIndent()

        val message = jsonParser.decodeFromString<Message>(json)

        assertSoftly(message) {
            role shouldBe ChatCompletionRole.ASSISTANT
            withClue("Content should be Parts with one OutputText part") {
                content shouldBe instanceOf<MessageContent.Parts>()
                val parts = (content as MessageContent.Parts).parts
                parts shouldHaveSize 1
                parts[0] shouldBe instanceOf<ContentPart.OutputText>()
                val outputText = parts[0] as ContentPart.OutputText
                outputText.text shouldBe
                    "Under the soft glow of the moon, Luna the unicorn danced through fields of twinkling stardust, leaving trails of dreams for every child asleep."
                outputText.annotations shouldBe emptyList()
            }
        }
    }

    @Test
    fun `Should serialize Message with text content as string`() {
        val message =
            Message(
                role = ChatCompletionRole.ASSISTANT,
                content = MessageContent.Text("Hello, world!"),
            )

        val json = jsonParser.encodeToString(Message.serializer(), message)

        json shouldEqualJson
            """
            {
              "role": "assistant",
              "content": "Hello, world!"
            }
            """.trimIndent()
    }

    @Test
    fun `Should serialize Message with content parts as array`() {
        val message =
            Message(
                role = ChatCompletionRole.ASSISTANT,
                content =
                    MessageContent.Parts(
                        listOf(
                            ContentPart.OutputText(
                                text = "Test message",
                                annotations = emptyList(),
                            ),
                        ),
                    ),
            )

        val json = jsonParser.encodeToString(Message.serializer(), message)

        json shouldEqualJson
            """
            {
              "role": "assistant",
              "content": [
                {
                  "type": "output_text",
                  "text": "Test message"
                }
              ]
            }
            """.trimIndent()
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

                    withClue("Function parameters should be correct") {
                        parameters.shouldNotBeNull()
                        val paramsObj = parameters.jsonObject
                        paramsObj.size shouldBe 2
                        paramsObj["location"]?.jsonPrimitive?.content shouldBe
                            "The city and state, e.g. San Francisco, CA"
                        paramsObj["unit"]?.jsonPrimitive?.content shouldBe
                            "The temperature unit to use. Infer this from the user's location."
                    }
                }
            }
        }
    }

    @Test
    fun `Should deserialize ChatCompletionRequest with image URL`() {
        val json =
            """
            {
              "model": "gpt-4o",
              "messages": [
                {
                  "role": "user",
                  "content": [
                    {
                      "type": "text",
                      "text": "What's in this image?"
                    },
                    {
                      "type": "image_url",
                      "image_url": {
                        "url": "https://example.com/image.jpg",
                        "detail": "high"
                      }
                    }
                  ]
                }
              ],
              "max_completion_tokens": 300
            }
            """.trimIndent()

        val request = jsonParser.decodeFromString<ChatCompletionRequest>(json)

        assertSoftly(request) {
            model shouldBe "gpt-4o"
            maxCompletionTokens shouldBe 300
            messages shouldHaveSize 1

            withClue("User message should contain text and image") {
                messages[0].role shouldBe ChatCompletionRole.USER
                val content = messages[0].content
                content shouldBe instanceOf<MessageContent.Parts>()
                val parts = (content as MessageContent.Parts).parts
                parts shouldHaveSize 2

                withClue("First part should be text") {
                    parts[0] shouldBe instanceOf<ContentPart.Text>()
                    (parts[0] as ContentPart.Text).text shouldBe "What's in this image?"
                }

                withClue("Second part should be image URL") {
                    parts[1] shouldBe instanceOf<ContentPart.ImageUrl>()
                    val imageUrl = parts[1] as ContentPart.ImageUrl
                    imageUrl.imageUrl.url shouldBe "https://example.com/image.jpg"
                    imageUrl.imageUrl.detail shouldBe "high"
                }
            }
        }
    }

    @Test
    fun `Should deserialize ChatCompletionRequest with tool choice`() {
        val json =
            """
            {
              "model": "gpt-4o",
              "messages": [
                {
                  "role": "user",
                  "content": "What's the weather in Boston?"
                }
              ],
              "tools": [
                {
                  "type": "function",
                  "function": {
                    "name": "get_current_weather",
                    "description": "Get the current weather",
                    "parameters": {
                      "type": "object",
                      "properties": {
                        "location": {
                          "type": "string",
                          "description": "The city and state, e.g. San Francisco, CA"
                        },
                        "format": {
                          "type": "string",
                          "enum": ["celsius", "fahrenheit"],
                          "description": "The temperature unit to use."
                        }
                      },
                      "required": ["location", "format"]
                    }
                  }
                }
              ],
              "tool_choice": "auto"
            }
            """.trimIndent()

        val request = jsonParser.decodeFromString<ChatCompletionRequest>(json)

        assertSoftly(request) {
            model shouldBe "gpt-4o"
            messages shouldHaveSize 1
            tools.shouldNotBeNull()
            tools shouldHaveSize 1

            withClue("Tool choice should be auto") {
                toolChoice shouldBe instanceOf<ToolChoice.Auto>()
            }

            withClue("Tool should have enum parameter") {
                val tool = tools[0]
                tool.function.name shouldBe "get_current_weather"
            }
        }
    }

    @Test
    fun `Should deserialize ChatResponse with tool calls`() {
        val json =
            """
            {
              "id": "chatcmpl-abc123",
              "object": "chat.completions",
              "created": 1699896916,
              "model": "gpt-4o-2024-08-06",
              "choices": [
                {
                  "index": 0,
                  "message": {
                    "role": "assistant",
                    "content": null,
                    "tool_calls": [
                      {
                        "id": "call_abc123",
                        "type": "function",
                        "function": {
                          "name": "get_current_weather",
                          "arguments": "{\"location\":\"Boston, MA\",\"format\":\"fahrenheit\"}"
                        }
                      }
                    ]
                  },
                  "finish_reason": "tool_calls"
                }
              ],
              "usage": {
                "prompt_tokens": 82,
                "completion_tokens": 17,
                "total_tokens": 99,
                "completion_tokens_details": {
                  "reasoning_tokens": 0,
                  "accepted_prediction_tokens": 0,
                  "rejected_prediction_tokens": 0
                }
              }
            }
            """.trimIndent()

        val response = jsonParser.decodeFromString<ChatResponse>(json)

        assertSoftly(response) {
            id shouldBe "chatcmpl-abc123"
            model shouldBe "gpt-4o-2024-08-06"
            choices shouldHaveSize 1

            withClue("Choice should have tool calls") {
                val choice = choices[0]
                choice.finishReason shouldBe "tool_calls"
                choice.message.shouldNotBeNull()

                withClue("Message should have tool calls") {
                    val message = choice.message
                    val toolCalls = message?.toolCalls
                    toolCalls.shouldNotBeNull()
                    toolCalls shouldHaveSize 1

                    val toolCall = toolCalls[0]
                    toolCall.id shouldBe "call_abc123"
                    toolCall.type shouldBe "function"
                    toolCall.function.name shouldBe "get_current_weather"
                    toolCall.function.arguments shouldBe
                        "{\"location\":\"Boston, MA\",\"format\":\"fahrenheit\"}"
                }
            }
        }
    }

    @Test
    fun `Should deserialize ChatCompletionRequest with audio input`() {
        val json =
            """
            {
              "model": "gpt-4o-audio-preview",
              "modalities": ["text", "audio"],
              "audio": {
                "voice": "alloy",
                "format": "wav"
              },
              "messages": [
                {
                  "role": "user",
                  "content": [
                    {
                      "type": "input_audio",
                      "input_audio": {
                        "data": "BASE64_ENCODED_AUDIO",
                        "format": "wav"
                      }
                    }
                  ]
                }
              ]
            }
            """.trimIndent()

        val request = jsonParser.decodeFromString<ChatCompletionRequest>(json)

        assertSoftly(request) {
            model shouldBe "gpt-4o-audio-preview"
            messages shouldHaveSize 1

            withClue("User message should contain audio input") {
                messages[0].role shouldBe ChatCompletionRole.USER
                val content = messages[0].content
                content shouldBe instanceOf<MessageContent.Parts>()
                val parts = (content as MessageContent.Parts).parts
                parts shouldHaveSize 1

                withClue("Part should be audio input") {
                    parts[0] shouldBe instanceOf<ContentPart.InputAudio>()
                    val audioInput = parts[0] as ContentPart.InputAudio
                    audioInput.inputAudio.data shouldBe "BASE64_ENCODED_AUDIO"
                    audioInput.inputAudio.format shouldBe "wav"
                }
            }
        }
    }

    @Test
    fun `Should deserialize ChatCompletionRequest with reasoning effort`() {
        val json =
            """
            {
              "model": "o1",
              "messages": [
                {
                  "role": "user",
                  "content": "Solve this complex math problem: ..."
                }
              ],
              "reasoning_effort": "high"
            }
            """.trimIndent()

        val request = jsonParser.decodeFromString<ChatCompletionRequest>(json)

        assertSoftly(request) {
            model shouldBe "o1"
            reasoningEffort shouldBe "high"
            messages shouldHaveSize 1
            messages[0].role shouldBe ChatCompletionRole.USER
        }
    }
}
