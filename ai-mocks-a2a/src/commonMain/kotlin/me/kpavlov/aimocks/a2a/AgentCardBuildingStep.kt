package me.kpavlov.aimocks.a2a

import io.ktor.http.ContentType
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer

public class AgentCardBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<Nothing>,
) : AbstractBuildingStep<Nothing, AgentCardResponseSpecification>(mokksy, buildingStep) {
    override infix fun responds(block: AgentCardResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val responseDefinition = this.build()
            val responseSpecification = AgentCardResponseSpecification(responseDefinition)
            block.invoke(responseSpecification)
            contentType = ContentType.Application.Json
            body = requireNotNull(responseSpecification.card) { "Card must be defined" }
        }
    }
}
