package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.AgentCard
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer

public class AgentCardBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<Nothing>,
) : AbstractBuildingStep<Nothing, AgentCardResponseSpecification>(mokksy, buildingStep) {
    override infix fun responds(block: AgentCardResponseSpecification.() -> Unit) {
        buildingStep.respondsWith<AgentCard> {
            val responseDefinition = this.build()
            val responseSpecification = AgentCardResponseSpecification(responseDefinition)
            block.invoke(responseSpecification)
            body = requireNotNull(responseSpecification.card) { "Card must be defined" }
        }
    }
}
