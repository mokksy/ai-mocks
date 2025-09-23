package me.kpavlov.aimocks.openai.moderation

import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.aimocks.openai.model.moderation.CreateModerationRequest
import me.kpavlov.aimocks.openai.model.moderation.Moderation
import me.kpavlov.aimocks.openai.model.moderation.ModerationResult
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer
import java.util.concurrent.atomic.AtomicInteger

public class OpenaiModerationBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<CreateModerationRequest>,
) : AbstractBuildingStep<CreateModerationRequest, OpenaiModerationResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    private var counter: AtomicInteger = AtomicInteger(1)

    override infix fun responds(block: OpenaiModerationResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val responseDefinition = this.build()
            val spec = OpenaiModerationResponseSpecification(responseDefinition)
            block.invoke(spec)
            delay = spec.delay
            val id = spec.id ?: "modr-${Integer.toHexString(counter.addAndGet(1))}"
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
