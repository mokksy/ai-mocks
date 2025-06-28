package me.kpavlov.aimocks.gemini.content

import me.kpavlov.aimocks.gemini.Candidate
import me.kpavlov.aimocks.gemini.Content
import me.kpavlov.aimocks.gemini.GenerateContentResponse
import me.kpavlov.aimocks.gemini.Part
import me.kpavlov.aimocks.gemini.PromptFeedback


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
        modelVersion = modelVersion ?: "gemini-pro-text-001",
        responseId = responseId
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
        modelVersion = modelVersion ?: "gemini-pro-text-001",
        responseId = responseId
    )
}

