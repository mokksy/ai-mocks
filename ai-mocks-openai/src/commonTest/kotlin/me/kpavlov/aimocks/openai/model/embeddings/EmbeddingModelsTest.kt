package me.kpavlov.aimocks.openai.model.embeddings

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

internal class EmbeddingModelsTest {
    private val jsonParser =
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

    /**
     * https://platform.openai.com/docs/api-reference/moderations/create
     */
    @Test
    fun `Should deserialize simple CreateEmbeddingsRequest`() {
        // language=json
        val json =
            """
            {
            "model":"text-embedding-3-XXsmall",
            "input":"To be or not to be?"
            }
            """.trimIndent()

        val request = jsonParser.decodeFromString<CreateEmbeddingsRequest>(json)

        request shouldNotBeNull {
        }
        assertSoftly(request) {
            it.input shouldBe listOf("To be or not to be?")
            it.model shouldBe "text-embedding-3-XXsmall"
        }
    }

    @Test
    fun `Should deserialize full CreateEmbeddingsRequest`() {
        // language=json
        val json =
            """
            {
                "model":"super-model",
                "input":  ["To be", "or not to be?"],
                "encoding_format": "float",
                "user": "alice"
            }
            """.trimIndent()

        val request = jsonParser.decodeFromString<CreateEmbeddingsRequest>(json)

        request shouldNotBeNull {
        }
        assertSoftly(request) {
            it.input shouldBe listOf("To be", "or not to be?")
            it.model shouldBe "super-model"
        }
    }

    @Test
    @Suppress("LongMethod")
    fun `Should deserialize EmbeddingsResponse`() {
        // language=json
        val json =
            """
            {
              "object": "list",
              "data": [
                {
                  "object": "embedding",
                  "embedding": [
                    0.0023064255,
                    -0.009327292,
                    -0.0028842222
                  ],
                  "index": 0
                }
              ],
              "model": "text-embedding-ada-002",
              "usage": {
                "prompt_tokens": 8,
                "total_tokens": 8
              }
            }
            """.trimIndent()

        val response = jsonParser.decodeFromString<EmbeddingsResponse>(json)

        response shouldNotBeNull {
            assertSoftly(response) {
                it.objectType shouldBe "list"
                it.data.size shouldBe 1
                it.model shouldBe "text-embedding-ada-002"
                it.data.single() shouldNotBeNull {
                    objectType shouldBe "embedding"
                    embeddings shouldBe listOf(0.0023064255f, -0.009327292f, -0.0028842222f)
                    index shouldBe 0
                }
                it.usage shouldNotBeNull {
                    promptTokens shouldBe 8
                    totalTokens shouldBe 8
                }
            }
        }
    }
}
