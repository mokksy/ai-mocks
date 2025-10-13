package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.GetAuthenticatedExtendedCardRequest
import dev.mokksy.aimocks.core.AbstractBuildingStep
import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer

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
