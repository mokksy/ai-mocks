package dev.mokksy.aimocks.gemini.content

import dev.mokksy.aimocks.gemini.Content
import dev.mokksy.aimocks.gemini.Part
import dev.mokksy.aimocks.gemini.PromptFeedback
import dev.mokksy.aimocks.gemini.UsageMetadata
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class GeminiHelpersTest {
    @Test
    fun `generateContentResponse should create response with default values`() {
        // when
        val response =
            generateContentResponse(
                assistantContent = "Hello, world!",
            )

        // then
        assertSoftly(response) {
            candidates.size shouldBe 1
            candidates[0].content.parts.size shouldBe 1
            candidates[0].content.parts[0].text shouldBe "Hello, world!"
            candidates[0].finishReason shouldBe null
            candidates[0].safetyRatings shouldBe null
            promptFeedback shouldBe PromptFeedback(safetyRatings = null)
            usageMetadata shouldBe
                UsageMetadata(
                    promptTokenCount = 0,
                    candidatesTokenCount = 0,
                    totalTokenCount = 0,
                )
            modelVersion shouldBe "gemini-pro-text-001"
            responseId shouldBe null
        }
    }

    @Test
    fun `generateContentResponse should create response with finish reason`() {
        // when
        val response =
            generateContentResponse(
                assistantContent = "Test content",
                finishReason = "stop",
            )

        // then
        assertSoftly(response) {
            candidates.size shouldBe 1
            candidates[0].content.parts[0].text shouldBe "Test content"
            candidates[0].finishReason shouldBe "stop"
            candidates[0].safetyRatings shouldBe null
        }
    }

    @Test
    fun `generateContentResponse should create response with response ID`() {
        // when
        val response =
            generateContentResponse(
                assistantContent = "Content",
                responseId = "response-123",
            )

        // then
        assertSoftly(response) {
            candidates[0].content.parts[0].text shouldBe "Content"
            responseId shouldBe "response-123"
        }
    }

    @Test
    fun `generateContentResponse should create response with custom model version`() {
        // when
        val response =
            generateContentResponse(
                assistantContent = "Content",
                modelVersion = "gemini-1.5-pro",
            )

        // then
        assertSoftly(response) {
            candidates[0].content.parts[0].text shouldBe "Content"
            modelVersion shouldBe "gemini-1.5-pro"
        }
    }

    @Test
    fun `generateContentResponse should create response with all parameters`() {
        // when
        val response =
            generateContentResponse(
                assistantContent = "Complete response",
                finishReason = "stop",
                responseId = "resp-456",
                modelVersion = "gemini-2.0",
            )

        // then
        assertSoftly(response) {
            candidates.size shouldBe 1
            candidates[0].content.parts.size shouldBe 1
            candidates[0].content.parts[0].text shouldBe "Complete response"
            candidates[0].finishReason shouldBe "stop"
            candidates[0].safetyRatings shouldBe null
            promptFeedback shouldBe PromptFeedback(safetyRatings = null)
            usageMetadata shouldBe
                UsageMetadata(
                    promptTokenCount = 0,
                    candidatesTokenCount = 0,
                    totalTokenCount = 0,
                )
            modelVersion shouldBe "gemini-2.0"
            responseId shouldBe "resp-456"
        }
    }

    @Test
    fun `generateContentResponse should handle empty content`() {
        // when
        val response =
            generateContentResponse(
                assistantContent = "",
            )

        // then
        assertSoftly(response) {
            candidates[0].content.parts[0].text shouldBe ""
            modelVersion shouldBe "gemini-pro-text-001"
        }
    }

    @Test
    fun `generateContentResponse should handle multiline content`() {
        // given
        val multilineContent =
            """
            Line 1
            Line 2
            Line 3
            """.trimIndent()

        // when
        val response =
            generateContentResponse(
                assistantContent = multilineContent,
            )

        // then
        assertSoftly(response) {
            candidates[0].content.parts[0].text shouldBe multilineContent
        }
    }

    @Test
    fun `generateContentResponse should handle content with special characters`() {
        // given
        val specialContent = "Content with 特殊文字 and émojis 🎉"

        // when
        val response =
            generateContentResponse(
                assistantContent = specialContent,
            )

        // then
        assertSoftly(response) {
            candidates[0].content.parts[0].text shouldBe specialContent
        }
    }

    @Test
    fun `generateFinalContentResponse should create response with finish reason`() {
        // when
        val response =
            generateFinalContentResponse(
                finishReason = "stop",
            )

        // then
        assertSoftly(response) {
            candidates.size shouldBe 1
            candidates[0].content.parts.size shouldBe 1
            candidates[0].content.parts[0].text shouldBe ""
            candidates[0].finishReason shouldBe "stop"
            candidates[0].safetyRatings shouldBe null
            promptFeedback shouldBe PromptFeedback(safetyRatings = null)
            usageMetadata shouldBe
                UsageMetadata(
                    promptTokenCount = 0,
                    candidatesTokenCount = 0,
                    totalTokenCount = 0,
                )
            modelVersion shouldBe "gemini-pro-text-001"
            responseId shouldBe null
        }
    }

    @Test
    fun `generateFinalContentResponse should create response with response ID`() {
        // when
        val response =
            generateFinalContentResponse(
                finishReason = "length",
                responseId = "final-123",
            )

        // then
        assertSoftly(response) {
            candidates[0].content.parts[0].text shouldBe ""
            candidates[0].finishReason shouldBe "length"
            responseId shouldBe "final-123"
        }
    }

    @Test
    fun `generateFinalContentResponse should create response with custom model version`() {
        // when
        val response =
            generateFinalContentResponse(
                finishReason = "stop",
                modelVersion = "gemini-1.5-flash",
            )

        // then
        assertSoftly(response) {
            candidates[0].content.parts[0].text shouldBe ""
            candidates[0].finishReason shouldBe "stop"
            modelVersion shouldBe "gemini-1.5-flash"
        }
    }

    @Test
    fun `generateFinalContentResponse should create response with all parameters`() {
        // when
        val response =
            generateFinalContentResponse(
                finishReason = "max_tokens",
                responseId = "final-456",
                modelVersion = "gemini-ultra",
            )

        // then
        assertSoftly(response) {
            candidates.size shouldBe 1
            candidates[0].content.parts.size shouldBe 1
            candidates[0].content.parts[0].text shouldBe ""
            candidates[0].finishReason shouldBe "max_tokens"
            candidates[0].safetyRatings shouldBe null
            promptFeedback shouldBe PromptFeedback(safetyRatings = null)
            usageMetadata shouldBe
                UsageMetadata(
                    promptTokenCount = 0,
                    candidatesTokenCount = 0,
                    totalTokenCount = 0,
                )
            modelVersion shouldBe "gemini-ultra"
            responseId shouldBe "final-456"
        }
    }

    @Test
    fun `generateFinalContentResponse should handle different finish reasons`() {
        // given
        val finishReasons = listOf("stop", "length", "max_tokens", "safety", "recitation")

        // when & then
        finishReasons.forEach { reason ->
            val response = generateFinalContentResponse(finishReason = reason)
            assertSoftly(response) {
                candidates[0].finishReason shouldBe reason
                candidates[0].content.parts[0].text shouldBe ""
            }
        }
    }

    @Test
    fun `generateContentResponse should create consistent structure`() {
        // when
        val response =
            generateContentResponse(
                assistantContent = "Test",
            )

        // then
        assertSoftly(response) {
            candidates.size shouldBe 1
            val candidate = candidates[0]
            candidate.content.parts.size shouldBe 1
            candidate.content.parts[0] shouldBe Part(text = "Test")
            candidate.content shouldBe
                Content(
                    parts = listOf(Part(text = "Test")),
                )
        }
    }

    @Test
    fun `generateFinalContentResponse should create consistent structure`() {
        // when
        val response =
            generateFinalContentResponse(
                finishReason = "stop",
            )

        // then
        assertSoftly(response) {
            candidates.size shouldBe 1
            val candidate = candidates[0]
            candidate.content.parts.size shouldBe 1
            candidate.content.parts[0] shouldBe Part(text = "")
            candidate.content shouldBe
                Content(
                    parts = listOf(Part(text = "")),
                )
        }
    }

    @Test
    fun `both functions should have consistent usage metadata structure`() {
        // when
        val response1 = generateContentResponse("Content")
        val response2 = generateFinalContentResponse("stop")

        // then
        assertSoftly {
            response1.usageMetadata shouldBe
                UsageMetadata(
                    promptTokenCount = 0,
                    candidatesTokenCount = 0,
                    totalTokenCount = 0,
                )
            response2.usageMetadata shouldBe
                UsageMetadata(
                    promptTokenCount = 0,
                    candidatesTokenCount = 0,
                    totalTokenCount = 0,
                )
        }
    }

    @Test
    fun `both functions should have consistent prompt feedback structure`() {
        // when
        val response1 = generateContentResponse("Content")
        val response2 = generateFinalContentResponse("stop")

        // then
        assertSoftly {
            response1.promptFeedback shouldBe PromptFeedback(safetyRatings = null)
            response2.promptFeedback shouldBe PromptFeedback(safetyRatings = null)
        }
    }

    @Test
    fun `generateContentResponse should handle very long content`() {
        // given
        val longContent = "A".repeat(10000)

        // when
        val response =
            generateContentResponse(
                assistantContent = longContent,
            )

        // then
        assertSoftly(response) {
            candidates[0].content.parts[0].text shouldBe longContent
            candidates[0]
                .content.parts[0]
                .text
                ?.length shouldBe 10000
        }
    }

    @Test
    fun `generateContentResponse should handle content with JSON`() {
        // given
        val jsonContent = """{"name": "test", "value": 123}"""

        // when
        val response =
            generateContentResponse(
                assistantContent = jsonContent,
            )

        // then
        assertSoftly(response) {
            candidates[0].content.parts[0].text shouldBe jsonContent
        }
    }

    @Test
    fun `generateContentResponse should handle content with code`() {
        // given
        val codeContent =
            """
            fun main() {
                println("Hello, World!")
            }
            """.trimIndent()

        // when
        val response =
            generateContentResponse(
                assistantContent = codeContent,
            )

        // then
        assertSoftly(response) {
            candidates[0].content.parts[0].text shouldBe codeContent
        }
    }
}
