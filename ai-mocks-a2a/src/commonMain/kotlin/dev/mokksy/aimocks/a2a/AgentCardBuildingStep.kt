package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.core.AbstractBuildingStep
import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer
import io.ktor.http.ContentType

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
