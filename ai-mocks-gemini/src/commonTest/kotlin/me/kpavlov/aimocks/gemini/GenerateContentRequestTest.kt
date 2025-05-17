package me.kpavlov.aimocks.gemini

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GenerateContentRequestTest : AbstractSerializationTest() {
    @Test
    fun `Deserialize and Serialize CancelTaskRequest`() {
        // language=json
        val payload =
            """
            {"contents":[
            {"role":"user","parts":[{"text":"Just say 'Hello!'"}]}
            ],
            "generationConfig":{
              "temperature":0.7,
              "topP":1.0
            },
            "systemInstruction":{
            "parts":[
            {"text":"You are a helpful pirate"}
            ]}}
            """.trimIndent()

        val model = deserializeAndSerialize<GenerateContentRequest>(payload)
        model.contents.size shouldBe 1
        model.contents[0].role shouldBe "user"
        model.contents[0].parts.size shouldBe 1
        model.contents[0].parts[0].text shouldBe "Just say 'Hello!'"
        model.generationConfig.shouldNotBeNull {
            this.temperature shouldBe 0.7
            this.topP shouldBe 1.0
        }
        model.systemInstruction.shouldNotBeNull {
            this.parts.size shouldBe 1
            this.parts[0].text shouldBe "You are a helpful pirate"
        }
    }
}
