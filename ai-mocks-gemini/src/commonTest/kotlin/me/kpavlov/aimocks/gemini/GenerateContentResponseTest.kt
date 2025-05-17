package me.kpavlov.aimocks.gemini

import kotlin.test.Test

internal class GenerateContentResponseTest : AbstractSerializationTest() {
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
    }
}
