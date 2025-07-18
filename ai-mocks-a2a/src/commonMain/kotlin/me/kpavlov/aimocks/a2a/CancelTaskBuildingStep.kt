package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.CancelTaskRequest
import me.kpavlov.aimocks.a2a.model.CancelTaskResponse
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer

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
