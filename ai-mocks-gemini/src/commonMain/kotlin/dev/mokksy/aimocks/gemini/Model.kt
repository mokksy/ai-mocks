@file:OptIn(ExperimentalSerializationApi::class)

package dev.mokksy.aimocks.gemini

import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Based on Google Gemini API documentation: https://ai.google.dev/gemini-api/docs/text-generation#rest

@Serializable
public data class GenerateContentRequest(
    val contents: List<Content>,
    val model: String? = null,
    val generationConfig: GenerationConfig? = null,
    @SerialName("safety_settings")
    val safetySettings: List<SafetySetting>? = null,
    val tools: List<Tool>? = null,
    val systemInstruction: Content? = null,
)

/**
 * Represents a part of the content in a request or response.
 *
 * @property parts The parts that make up this content.
 * @property role The role of the content (e.g., "user" or "model").
 */
@Serializable
public data class Content(
    val parts: List<Part>,
    val role: String? = null,
)

/**
 * Represents a part of the content, which can be text or other media.
 *
 * @property text The text content.
 */
@Serializable
public data class Part(
    val text: String? = null,
)

/**
 * Configuration options for text generation.
 *
 * @property temperature Controls the randomness of the output.
 * @property topP Controls diversity via nucleus sampling.
 * @property topK Controls diversity via top-k sampling.
 * @property candidateCount The number of candidates to generate.
 * @property maxOutputTokens The maximum number of tokens to generate.
 * @property stopSequences Sequences where the model will stop generating further tokens.
 * @property responseMimeType The MIME type of the response.
 * @property responseSchema The schema for the response.
 * @property responseModalities The modalities for the response.
 * @property seed A seed to use for random sampling.
 * @property presencePenalty Penalty for token presence.
 * @property frequencyPenalty Penalty for token frequency.
 * @property responseLogprobs Whether to include log probabilities in the response.
 * @property logprobs Number of log probabilities to include.
 * @property enableEnhancedCivicAnswers Whether to enable enhanced civic answers.
 * @property speechConfig Configuration for speech generation.
 * @property thinkingConfig Configuration for thinking.
 * @property mediaResolution Resolution for media generation.
 */
@Serializable
public data class GenerationConfig(
    val temperature: Double? = null,
    val topP: Double? = null,
    val topK: Float? = null,
    val candidateCount: Int? = null,
    val maxOutputTokens: Int? = null,
    val stopSequences: List<String>? = null,
    val responseMimeType: String? = null,
    val responseSchema: Schema? = null,
    val responseModalities: List<Modality>? = null,
    val seed: Int? = null,
    val presencePenalty: Double? = null,
    val frequencyPenalty: Double? = null,
    val responseLogprobs: Boolean? = null,
    val logprobs: Int? = null,
    val enableEnhancedCivicAnswers: Boolean? = null,
    val speechConfig: SpeechConfig? = null,
    val thinkingConfig: ThinkingConfig? = null,
    val mediaResolution: MediaResolution? = null,
)

/**
 * Settings to control the safety of generated content.
 *
 * @property category The category to apply the threshold to.
 * @property threshold The threshold level for blocking content.
 */
@Serializable
public data class SafetySetting(
    val category: SafetyCategory,
    val threshold: HarmBlockThreshold,
)

/**
 * Categories for safety settings.
 */
@Serializable
public enum class SafetyCategory {
    @SerialName("HARM_CATEGORY_UNSPECIFIED")
    HARM_CATEGORY_UNSPECIFIED,

    @SerialName("HARM_CATEGORY_HATE_SPEECH")
    HARM_CATEGORY_HATE_SPEECH,

    @SerialName("HARM_CATEGORY_DANGEROUS_CONTENT")
    HARM_CATEGORY_DANGEROUS_CONTENT,

    @SerialName("HARM_CATEGORY_HARASSMENT")
    HARM_CATEGORY_HARASSMENT,

    @SerialName("HARM_CATEGORY_SEXUALLY_EXPLICIT")
    HARM_CATEGORY_SEXUALLY_EXPLICIT,
}

/**
 * Threshold levels for blocking harmful content.
 */
@Serializable
public enum class HarmBlockThreshold {
    @SerialName("HARM_BLOCK_THRESHOLD_UNSPECIFIED")
    HARM_BLOCK_THRESHOLD_UNSPECIFIED,

    @SerialName("BLOCK_LOW_AND_ABOVE")
    BLOCK_LOW_AND_ABOVE,

    @SerialName("BLOCK_MEDIUM_AND_ABOVE")
    BLOCK_MEDIUM_AND_ABOVE,

    @SerialName("BLOCK_ONLY_HIGH")
    BLOCK_ONLY_HIGH,

    @SerialName("BLOCK_NONE")
    BLOCK_NONE,
}

/**
 * Represents a tool that the model may call.
 *
 * @property functionDeclarations The functions that the model may call.
 */
@Serializable
public data class Tool(
    @SerialName("function_declarations")
    val functionDeclarations: List<FunctionDeclaration>,
)

/**
 * Represents a function declaration for a tool.
 *
 * @property name The name of the function.
 * @property description A description of what the function does.
 * @property parameters The parameters that the function accepts.
 */
@Serializable
public data class FunctionDeclaration(
    val name: String,
    val description: String? = null,
    val parameters: Map<String, @Contextual Any>? = null,
)

/**
 * Represents a response from the Gemini API.
 *
 * @property candidates The generated candidates.
 * @property promptFeedback Feedback related to the prompt.
 */
@Serializable
public data class GenerateContentResponse(
    val candidates: List<Candidate>,
    val promptFeedback: PromptFeedback? = null,
    val usageMetadata: UsageMetadata? = null,
    val modelVersion: String,
    val responseId: String? = null,
)

/**
 * Represents a candidate response from the model.
 *
 * @property content The content of the candidate.
 * @property finishReason The reason why the model stopped generating tokens.
 * @property safetyRatings Safety ratings for the content.
 */
@Serializable
public data class Candidate(
    val content: Content,
    @SerialName("finish_reason")
    val finishReason: String? = null,
    @SerialName("safety_ratings")
    val safetyRatings: List<SafetyRating>? = null,
)

/**
 * @see <a href="https://ai.google.dev/api/generate-content#UsageMetadata">UsageMetadata spec</a>
 */
@Serializable
public data class UsageMetadata(
    @SerialName("promptTokenCount")
    val promptTokenCount: Int? = null,
    @SerialName("cachedContentTokenCount")
    val cachedContentTokenCount: Int? = null,
    @SerialName("candidatesTokenCount")
    val candidatesTokenCount: Int? = null,
    @SerialName("toolUsePromptTokenCount")
    val toolUsePromptTokenCount: Int? = null,
    @SerialName("thoughtsTokenCount")
    val thoughtsTokenCount: Int? = null,
    @SerialName("totalTokenCount")
    val totalTokenCount: Int? = null,
    @SerialName("promptTokensDetails")
    val promptTokensDetails: List<ModalityTokenCount>? = null,
    @SerialName("cacheTokensDetails")
    val cacheTokensDetails: List<ModalityTokenCount>? = null,
    @SerialName("candidatesTokensDetails")
    val candidatesTokensDetails: List<ModalityTokenCount>? = null,
    @SerialName("toolUsePromptTokensDetails")
    val toolUsePromptTokensDetails: List<ModalityTokenCount>? = null,
)

/**
 * https://ai.google.dev/api/generate-content#v1beta.ModalityTokenCount
 */
@Serializable
public data class ModalityTokenCount(
    @SerialName("modality")
    val modality: Modality,
    @SerialName("tokenCount")
    val tokenCount: Int,
)

/**
 * Represents feedback related to the prompt.
 *
 * @property safetyRatings Safety ratings for the prompt.
 */
@Serializable
public data class PromptFeedback(
    @SerialName("blockReason")
    val blockReason: BlockReason? = null,
    @SerialName("safetyRatings")
    val safetyRatings: List<SafetyRating>? = null,
)

/**
 * Represents a safety rating for content.
 *
 * @property category The category for this rating.
 * @property probability The probability that the content is harmful.
 */
@Serializable
public data class SafetyRating(
    val category: SafetyCategory,
    val probability: HarmProbability,
)

/**
 * Probability levels for harmful content.
 */
@Serializable
public enum class HarmProbability {
    @SerialName("HARM_PROBABILITY_UNSPECIFIED")
    HARM_PROBABILITY_UNSPECIFIED,

    @SerialName("NEGLIGIBLE")
    NEGLIGIBLE,

    @SerialName("LOW")
    LOW,

    @SerialName("MEDIUM")
    MEDIUM,

    @SerialName("HIGH")
    HIGH,
}

public enum class BlockReason {
    /**
     * Default value. This value is unused.
     */
    BLOCK_REASON_UNSPECIFIED,

    /**
     * Prompt was blocked due to safety reasons. Inspect safetyRatings to understand which safety category blocked it.
     */
    SAFETY,

    /**Prompt was blocked due to unknown reasons.*/
    OTHER,

    /**
     * Prompt was blocked due to the terms which are included from the terminology blocklist.
     */
    BLOCKLIST,

    /**
     * Prompt was blocked due to prohibited content.
     */
    PROHIBITED_CONTENT,

    /**
     * Candidates blocked due to unsafe image generation content.
     */
    IMAGE_SAFETY,
}

/**
 * Schema for the response.
 */
@Serializable
public data class Schema(
    // This is a placeholder for the Schema object
    // The actual implementation would depend on the specific schema structure
    val type: String? = null,
    val properties: Map<String, @Contextual Any>? = null,
)

/**
 * Modality for the response.
 *
 * See: https://ai.google.dev/api/generate-content#Modality
 */
@Serializable
public enum class Modality {
    @SerialName("MODALITY_UNSPECIFIED")
    MODALITY_UNSPECIFIED,

    @SerialName("TEXT")
    TEXT,

    @SerialName("IMAGE")
    IMAGE,

    @SerialName("VIDEO")
    VIDEO,

    @SerialName("AUDIO")
    AUDIO,
}

/**
 * Configuration for speech generation.
 */
@Serializable
public data class SpeechConfig(
    // This is a placeholder for the SpeechConfig object
    // The actual implementation would depend on the specific configuration options
    val voice: String? = null,
)

/**
 * Configuration for thinking.
 */
@Serializable
public data class ThinkingConfig(
    // This is a placeholder for the ThinkingConfig object
    // The actual implementation would depend on the specific configuration options
    val enabled: Boolean? = null,
)

/**
 * Resolution for media generation.
 */
@Serializable
public enum class MediaResolution {
    @SerialName("MEDIA_RESOLUTION_UNSPECIFIED")
    MEDIA_RESOLUTION_UNSPECIFIED,

    @SerialName("LOW")
    LOW,

    @SerialName("MEDIUM")
    MEDIUM,

    @SerialName("HIGH")
    HIGH,
}
