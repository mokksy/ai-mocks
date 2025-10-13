package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.CancelTaskRequest
import dev.mokksy.aimocks.a2a.model.CancelTaskResponse
import dev.mokksy.aimocks.core.AbstractBuildingStep
import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer

public class CancelTaskBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<CancelTaskRequest>,
) : AbstractBuildingStep<CancelTaskRequest, CancelTaskResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    override infix fun responds(block: CancelTaskResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val requestBody = request.body
            val responseDefinition = this.build()
            val responseSpecification = CancelTaskResponseSpecification(responseDefinition)
            block.invoke(responseSpecification)
            body =
                CancelTaskResponse(
                    id = responseSpecification.id ?: requestBody.id,
                    result = responseSpecification.result,
                    error = responseSpecification.error,
                )
        }
    }
}
