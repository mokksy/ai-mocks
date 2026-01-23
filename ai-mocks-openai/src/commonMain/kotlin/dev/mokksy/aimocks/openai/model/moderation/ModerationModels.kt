package dev.mokksy.aimocks.openai.model.moderation

import dev.mokksy.mokksy.serializers.StringOrListSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a request to classify text and/or images for policy violations.
 *
 * @property input The input(s) to classify. Can be a single string or an array of strings.
 * @property model The moderation model to use (e.g., "text-moderation-latest").
 * @see <a href="https://platform.openai.com/docs/api-reference/moderations/create">Create Moderation</a>
 */
@Serializable
public data class CreateModerationRequest(
    @Serializable(StringOrListSerializer::class)
    @SerialName("input")
    val input: List<String>,
    @SerialName("model")
    val model: String? = null,
)

/**
 * Represents the moderation results returned by the OpenAI moderation endpoint.
 *
 * See https://platform.openai.com/docs/api-reference/moderations/object
 *
 * @property id The unique identifier for the moderation response.
 * @property model The name of the model that generated the moderation results.
 * @property results The list of individual moderation results that provide details about flagged content
 * and the categories associated with the moderation check.
 */
@Serializable
@OptIn(ExperimentalSerializationApi::class)
public data class Moderation(
    val id: String,
    @EncodeDefault
    val model: String = "omni-moderation-latest",
    @EncodeDefault
    val results: List<ModerationResult> = emptyList(),
)

/**
 * Represents the result of a moderation check performed by the OpenAI moderation endpoint.
 *
 * @property flagged Indicates whether the input was flagged based on the moderation categories.
 * @property categories A map where the keys are moderation categories and the values indicate
 * if the category was triggered.
 * @property categoryScores A map of moderation categories to their respective confidence scores.
 * @property categoryAppliedInputTypes A map of moderation categories to the list of input types
 * the category and score apply to.
 */
@Serializable
@OptIn(ExperimentalSerializationApi::class)
public data class ModerationResult(
    @EncodeDefault
    public val flagged: Boolean = false,
    @EncodeDefault
    public val categories: Map<ModerationCategory, Boolean> = emptyMap(),
    @SerialName("category_scores")
    @EncodeDefault
    public val categoryScores: Map<ModerationCategory, Double> = emptyMap(),
    @SerialName("category_applied_input_types")
    @EncodeDefault
    public val categoryAppliedInputTypes: Map<ModerationCategory, List<InputType>> = emptyMap(),
)

/**
 * Represents a moderation category used to classify content violations.
 *
 * @property name The name of the moderation category.
 * @see <a href="https://platform.openai.com/docs/api-reference/moderations/object#moderations/object-results-categories">Moderation Categories</a>
 */
@JvmInline
@Serializable
public value class ModerationCategory(
    public val name: String,
) {
    public companion object {
        public val HARASSMENT: ModerationCategory = ModerationCategory("harassment")
        public val HARASSMENT_THREATENING: ModerationCategory =
            ModerationCategory("harassment/threatening")
        public val SEXUAL: ModerationCategory = ModerationCategory("sexual")
        public val HATE: ModerationCategory = ModerationCategory("hate")
        public val HATE_THREATENING: ModerationCategory = ModerationCategory("hate/threatening")
        public val ILLICIT: ModerationCategory = ModerationCategory("illicit")
        public val ILLICIT_VIOLENT: ModerationCategory = ModerationCategory("illicit/violent")
        public val SELF_HARM_INTENT: ModerationCategory = ModerationCategory("self-harm/intent")
        public val SELF_HARM_INSTRUCTIONS: ModerationCategory =
            ModerationCategory("self-harm/instructions")
        public val SELF_HARM: ModerationCategory = ModerationCategory("self-harm")
        public val SEXUAL_MINORS: ModerationCategory = ModerationCategory("sexual/minors")
        public val VIOLENCE: ModerationCategory = ModerationCategory("violence")
        public val VIOLENCE_GRAPHIC: ModerationCategory = ModerationCategory("violence/graphic")
    }
}

/**
 * Represents the type of input being moderated.
 *
 * @see <a href="https://platform.openai.com/docs/api-reference/moderations/object#moderations/object-results-category_applied_input_types">
 *     Category Applied Input Types</a>
 */
public enum class InputType {
    @SerialName("image")
    IMAGE,

    @SerialName("text")
    TEXT,
}
