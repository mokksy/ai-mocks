package me.kpavlov.aimocks.gemini

import dev.mokksy.test.utils.deserializeAndSerialize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GenerateContentResponseTest {
    @Test
    fun `Deserialize and Serialize GenerateContentResponse`() {
        // language=json
        val payload =
            """
           {
  "candidates": [
    {
       "content": {
               "parts": [ {
                    "text": "This is the answer to everything"
                           }
               ],
               "role": "model"
      }
    }
  ],

  "modelVersion": "gemini-pro-text-001"
}
            """.trimIndent()

        val model = deserializeAndSerialize<GenerateContentResponse>(payload)
        model.shouldNotBeNull {
            candidates.size shouldBe 1
            candidates[0].content.shouldNotBeNull {
                parts.size shouldBe 1
                parts[0].text shouldBe "This is the answer to everything"
            }
        }
    }
}
