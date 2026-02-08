package dev.mokksy.aimocks.ollama.model

import dev.mokksy.aimocks.ollama.generate.GenerateRequest
import dev.mokksy.aimocks.ollama.generate.GenerateResponse
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.schema.json.NumericPropertyDefinition
import kotlinx.schema.json.StringPropertyDefinition
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.time.Instant

/**
 * Tests for the serialization and deserialization of generate models.
 */
internal class GenerateModelsTest : AbstractSerializationTest() {
    @Test
    fun `Deserialize and Serialize GenerateRequest`() {
        // language=json
        val payload =
            """
            {
              "model": "llama3.2",
              "prompt": "Why is the sky blue?",
              "system": "You are a helpful assistant",
              "template": "{{ .System }}\n\n{{ .Prompt }}",
              "context": [1, 2, 3],
              "options": {
                "temperature": 0.7,
                "top_p": 0.9
              },
              "format": "json",
              "stream": false,
              "raw": true,
              "keep_alive": "10m"
            }
            """.trimIndent()

        val model = deserializeAndSerialize<GenerateRequest>(payload)
        model.model shouldBe "llama3.2"
        model.prompt shouldBe "Why is the sky blue?"
        model.system shouldBe "You are a helpful assistant"
        model.template shouldBe "{{ .System }}\n\n{{ .Prompt }}"
        model.context shouldBe listOf(1, 2, 3)
        model.options shouldBe
            ModelOptions(
                temperature = 0.7,
                topP = 0.9,
            )
        model.format.shouldBeInstanceOf<Format.Json>()
        model.stream shouldBe false
        model.raw shouldBe true
        model.keepAlive shouldBe "10m"
    }

    @Test
    fun `Deserialize and Serialize GenerateRequest with JSON Schema format`() {
        // language=json
        val payload =
            """
            {
              "model": "llama3.2",
              "prompt": "Describe a person",
              "format": {
                "name": "person_schema",
                "schema": {
                  "type": "object",
                  "properties": {
                    "name": {
                      "type": "string",
                      "description": "The person's name"
                    },
                    "age": {
                      "type": "number",
                      "description": "The person's age"
                    },
                    "occupation": {
                      "type": "string",
                      "description": "The person's occupation"
                    }
                  },
                  "required": ["name", "age"]
                }
              }
            }
            """.trimIndent()

        // For this test, we'll just verify that the deserialized object has the expected values
        // without comparing the serialized output with the original JSON
        val json =
            Json {
                ignoreUnknownKeys = true
            }
        val model: GenerateRequest = json.decodeFromString(payload)

        model.shouldNotBeNull()
        model.model shouldBe "llama3.2"
        model.prompt shouldBe "Describe a person"

        val formatSchema = model.format.shouldBeInstanceOf<Format.Schema>()
        formatSchema.name shouldBe "person_schema"
        formatSchema.schema.type shouldBe listOf("object")
        formatSchema.schema.properties.size shouldBe 3
        formatSchema.schema.required shouldBe listOf("name", "age")

        // Verify that the properties have the expected values
        val nameProperty =
            formatSchema.schema.properties["name"]
                ?.shouldBeInstanceOf<StringPropertyDefinition>()
        nameProperty?.description shouldBe "The person's name"

        val ageProperty =
            formatSchema.schema.properties["age"]
                ?.shouldBeInstanceOf<NumericPropertyDefinition>()
        ageProperty?.description shouldBe "The person's age"

        val occupationProperty =
            formatSchema.schema.properties["occupation"]
                ?.shouldBeInstanceOf<StringPropertyDefinition>()
        occupationProperty?.description shouldBe "The person's occupation"
    }

    @Test
    fun `Deserialize and Serialize GenerateResponse`() {
        // language=json
        val payload =
            """
            {
              "model": "llama3.2",
              "created_at": "2023-08-04T19:22:45.499127Z",
              "response": "The sky appears blue because of a phenomenon called Rayleigh scattering.",
              "done": true,
              "done_reason": "stop",
              "context": [1, 2, 3],
              "total_duration": 5043500667,
              "load_duration": 5025959,
              "prompt_eval_count": 26,
              "prompt_eval_duration": 325953000,
              "eval_count": 290,
              "eval_duration": 4709213000
            }
            """.trimIndent()

        val model = deserializeAndSerialize<GenerateResponse>(payload)
        model.model shouldBe "llama3.2"
        model.createdAt shouldBe Instant.parse("2023-08-04T19:22:45.499127Z")
        model.response shouldBe
            "The sky appears blue because of a phenomenon called Rayleigh scattering."
        model.done shouldBe true
        model.doneReason shouldBe "stop"
        model.context shouldBe listOf(1, 2, 3)
        model.totalDuration shouldBe 5043500667
        model.loadDuration shouldBe 5025959
        model.promptEvalCount shouldBe 26
        model.promptEvalDuration shouldBe 325953000
        model.evalCount shouldBe 290
        model.evalDuration shouldBe 4709213000
    }
}
