package me.kpavlov.aimocks.openai.moderation

import me.kpavlov.aimocks.core.AbstractResponseSpecification
import me.kpavlov.aimocks.openai.model.moderation.CreateModerationRequest
import me.kpavlov.aimocks.openai.model.moderation.InputType
import me.kpavlov.aimocks.openai.model.moderation.InputType.TEXT
import me.kpavlov.aimocks.openai.model.moderation.Moderation
import me.kpavlov.aimocks.openai.model.moderation.ModerationCategory
import me.kpavlov.aimocks.openai.model.moderation.ModerationResult
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

/**
 * Response specification for OpenAI Moderation endpoint.
 */
public class OpenaiModerationResponseSpecification(
    response: AbstractResponseDefinition<Moderation>,
    delay: Duration = Duration.ZERO,
) : AbstractResponseSpecification<CreateModerationRequest, Moderation>(
        response = response,
        delay = delay,
    ) {
    public var id: String? = null
    public var model: String = "omni-moderation-latest"
    public var flagged: Boolean = false
    public var categories: MutableMap<ModerationCategory, Boolean> = mutableMapOf()

    @Suppress("MagicNumber")
    public var categoryScores: MutableMap<ModerationCategory, Double> = mutableMapOf()

    /**
     * A list of the categories along with the input type(s) that the score applies to.
     */
    public var categoryAppliedInputTypes: MutableMap<ModerationCategory, List<InputType>> =
        mutableMapOf()

    init {
        // set default values
        category(name = "harassment", score = 0.0, inputTypes = listOf(TEXT))
        category(name = "harassment/threatening", score = 0.0, inputTypes = listOf(TEXT))
        category(name = "sexual", score = 0.0, inputTypes = listOf(TEXT))
        category(name = "hate", score = 0.0, inputTypes = listOf(TEXT))
        category(name = "hate/threatening", score = 0.0, inputTypes = listOf(TEXT))
        category(name = "illicit", score = 0.0, inputTypes = listOf(TEXT))
        category(name = "illicit/violent", score = 0.0, inputTypes = listOf(TEXT))
        category(name = "self-harm/intent", score = 0.0, inputTypes = listOf(TEXT))
        category(name = "self-harm/instructions", score = 0.0, inputTypes = listOf(TEXT))
        category(name = "self-harm", score = 0.0, inputTypes = listOf(TEXT))
        category(name = "sexual/minors", score = 0.0, inputTypes = listOf(TEXT))
        category(name = "violence", score = 0.0, inputTypes = listOf(TEXT))
        category(name = "violence/graphic", score = 0.0, inputTypes = listOf(InputType.IMAGE))
    }

    public fun model(value: String): OpenaiModerationResponseSpecification =
        apply { this.model = value }

    public fun flagged(value: Boolean): OpenaiModerationResponseSpecification =
        apply { this.flagged = value }

    public fun category(
        name: String,
        value: Boolean,
    ): OpenaiModerationResponseSpecification =
        apply {
            this.categories[ModerationCategory(name)] = value
        }

    public fun category(
        name: ModerationCategory,
        score: Double,
        inputTypes: List<InputType> = listOf(TEXT),
    ): OpenaiModerationResponseSpecification =
        apply {
            this.categories[name] = score > 0.0
            this.categoryScores[name] = score
            this.categoryAppliedInputTypes[name] = inputTypes
        }

    public fun category(
        name: String,
        score: Double,
        inputTypes: List<InputType> = listOf(TEXT),
    ): OpenaiModerationResponseSpecification =
        category(name = ModerationCategory(name), score = score, inputTypes = inputTypes)

    public fun score(
        name: ModerationCategory,
        value: Double,
    ): OpenaiModerationResponseSpecification =
        apply {
            this.categoryScores[name] = value
        }

    public fun appliedInputTypes(
        name: ModerationCategory,
        value: List<InputType>,
    ): OpenaiModerationResponseSpecification =
        apply {
            this.categoryAppliedInputTypes[name] = value
        }

    internal fun toResult(): ModerationResult =
        ModerationResult(
            flagged = flagged,
            categories = categories.toMap(),
            categoryScores = categoryScores.toMap(),
            categoryAppliedInputTypes = categoryAppliedInputTypes.toMap(),
        )
}
