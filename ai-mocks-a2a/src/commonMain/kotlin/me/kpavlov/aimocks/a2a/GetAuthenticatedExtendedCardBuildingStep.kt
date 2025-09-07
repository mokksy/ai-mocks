package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.GetAuthenticatedExtendedCardRequest
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer

/**
 * Building step for configuring get authenticated extended card responses.
 */
public class GetAuthenticatedExtendedCardBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<GetAuthenticatedExtendedCardRequest>,
) : AbstractBuildingStep<GetAuthenticatedExtendedCardRequest, GetAuthenticatedExtendedCardResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    override infix fun responds(
        block: GetAuthenticatedExtendedCardResponseSpecification.() -> Unit,
    ) {
        buildingStep.respondsWith {
            val responseDefinition = this.build()
            val responseSpec = GetAuthenticatedExtendedCardResponseSpecification(responseDefinition)
            block.invoke(responseSpec)
            delay = responseSpec.delay
            body = responseSpec.build()
        }
    }
}
