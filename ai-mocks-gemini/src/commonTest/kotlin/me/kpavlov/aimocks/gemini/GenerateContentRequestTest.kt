package me.kpavlov.aimocks.gemini

import dev.mokksy.test.utils.deserializeAndSerialize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GenerateContentRequestTest {
    @Test
    fun `Deserialize and Serialize GenerateContentRequest`() {
        // language=json
        val payload =
            """
            {"contents":[
            {"role":"user","parts":[{"text":"Just say 'Hello!'"}]}
            ],
            "generationConfig":{
              "temperature":0.7,
              "topP":0.8,
              "topK":3.0,
              "maxOutputTokens":348,
              "seed":60566
            },
            "systemInstruction":{
              "parts":[
                {"text":"You are a helpful pirate"}
              ]}}
            """.trimIndent()

        val model = deserializeAndSerialize<GenerateContentRequest>(payload)
        model.contents.size shouldBe 1
        model.contents[0] shouldNotBeNull {
            role shouldBe "user"
            parts.size shouldBe 1
            parts[0].text shouldBe "Just say 'Hello!'"
        }
        model.generationConfig.shouldNotBeNull {
            this.temperature shouldBe 0.7
            this.topP shouldBe 0.8
            this.topK shouldBe 3
        }
        model.systemInstruction.shouldNotBeNull {
            this.parts.size shouldBe 1
            this.parts[0].text shouldBe "You are a helpful pirate"
        }
    }
}
