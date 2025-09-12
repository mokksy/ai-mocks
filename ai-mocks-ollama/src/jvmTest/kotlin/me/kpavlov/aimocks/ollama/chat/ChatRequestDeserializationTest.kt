package me.kpavlov.aimocks.ollama.chat

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.json.Json
import kotlin.test.Test

internal class ChatRequestDeserializationTest {
    private val json = Json { ignoreUnknownKeys = true }

    // MockOllama's exact JSON configuration
    private val mockOllamaJson =
        Json {
            ignoreUnknownKeys = false
            prettyPrint = true
        }

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
            temperature shouldBe 0.30081615737430945
            topK shouldBe 404
            topP shouldBe 0.30994638421186493
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
        request.options shouldNotBe null
        request.options!!.temperature shouldBe 0.015273115109424307
        request.options!!.topK shouldBe 721
        request.options!!.topP shouldBe 0.4836426825417919
        request.options!!.stop shouldBe emptyList()
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

        val request = mockOllamaJson.decodeFromString<ChatRequest>(langchain4jJson)
        request shouldNotBe null
        request.model shouldBe "llama3.1"
        request.messages.size shouldBe 1
        request.stream shouldBe true
        request.options shouldNotBe null
        request.options!!.temperature shouldBe 0.015273115109424307
        request.options!!.topK shouldBe 721
        request.options!!.topP shouldBe 0.4836426825417919
        request.options!!.stop shouldBe emptyList()
    }
}
