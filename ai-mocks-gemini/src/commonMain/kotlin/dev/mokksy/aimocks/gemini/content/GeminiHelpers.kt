package dev.mokksy.aimocks.gemini.content

import dev.mokksy.aimocks.gemini.Candidate
import dev.mokksy.aimocks.gemini.Content
import dev.mokksy.aimocks.gemini.GenerateContentResponse
import dev.mokksy.aimocks.gemini.Part
import dev.mokksy.aimocks.gemini.PromptFeedback
import dev.mokksy.aimocks.gemini.UsageMetadata

internal fun generateContentResponse(
    assistantContent: String,
    finishReason: String? = null,
    responseId: String? = null,
    modelVersion: String? = null,
): GenerateContentResponse {
    val candidate =
        Candidate(
            content =
                Content(
                    parts =
                        listOf(
                            Part(
                                text = assistantContent,
                            ),
                        ),
                ),
            finishReason = finishReason,
            safetyRatings = null,
        )

    return GenerateContentResponse(
        candidates = listOf(candidate),
        promptFeedback =
            PromptFeedback(
                safetyRatings = null,
            ),
        usageMetadata =
            UsageMetadata(
                promptTokenCount = 0,
                candidatesTokenCount = 0,
                totalTokenCount = 0,
            ),
        modelVersion = modelVersion ?: "gemini-pro-text-001",
        responseId = responseId,
    )
}

internal fun generateFinalContentResponse(
    finishReason: String,
    responseId: String? = null,
    modelVersion: String? = null,
): GenerateContentResponse {
    val candidate =
        Candidate(
            content =
                Content(
                    parts =
                        listOf(
                            Part(
                                text = "",
                            ),
                        ),
                ),
            finishReason = finishReason,
            safetyRatings = null,
        )

    return GenerateContentResponse(
        candidates = listOf(candidate),
        promptFeedback =
            PromptFeedback(
                safetyRatings = null,
            ),
        usageMetadata =
            UsageMetadata(
                promptTokenCount = 0,
                candidatesTokenCount = 0,
                totalTokenCount = 0,
            ),
        modelVersion = modelVersion ?: "gemini-pro-text-001",
        responseId = responseId,
    )
}
