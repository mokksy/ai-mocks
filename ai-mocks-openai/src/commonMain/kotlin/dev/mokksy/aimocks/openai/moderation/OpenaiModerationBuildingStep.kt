package dev.mokksy.aimocks.openai.moderation

import dev.mokksy.aimocks.core.AbstractBuildingStep
import dev.mokksy.aimocks.openai.model.moderation.CreateModerationRequest
import dev.mokksy.aimocks.openai.model.moderation.Moderation
import dev.mokksy.aimocks.openai.model.moderation.ModerationResult
import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer
import io.ktor.http.ContentType
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Builder step for configuring mock responses to OpenAI moderation requests.
 *
 * @param mokksy The mock server instance.
 * @param buildingStep The underlying building step for moderation requests.
 * @see <a href="https://platform.openai.com/docs/api-reference/moderations">OpenAI Moderations API</a>
 */
public class OpenaiModerationBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<CreateModerationRequest>,
) : AbstractBuildingStep<CreateModerationRequest, OpenaiModerationResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    @OptIn(ExperimentalAtomicApi::class)
    private val counter: AtomicLong = AtomicLong(0)

    @OptIn(ExperimentalAtomicApi::class)
    @Suppress("MagicNumber")
    override infix fun responds(block: suspend OpenaiModerationResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val spec = OpenaiModerationResponseSpecification()
            block.invoke(spec)
            delay = spec.delay
            contentType = ContentType.Application.Json
            val id = spec.id ?: "modr-${counter.addAndFetch(1).toString(16)}"
            val createdModel = spec.model
            val result: ModerationResult = spec.toResult()

            body =
                Moderation(
                    id = id,
                    model = createdModel,
                    results = listOf(result),
                )
        }
    }
}
