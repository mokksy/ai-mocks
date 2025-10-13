package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.GetTaskRequest
import dev.mokksy.aimocks.a2a.model.GetTaskResponse
import dev.mokksy.aimocks.core.AbstractBuildingStep
import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer

public class GetTaskBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<GetTaskRequest>,
) : AbstractBuildingStep<GetTaskRequest, GetTaskResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    override infix fun responds(block: GetTaskResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val requestBody = request.body
            val responseDefinition = this.build()
            val responseSpecification = GetTaskResponseSpecification(responseDefinition)
            block.invoke(responseSpecification)
            body =
                GetTaskResponse(
                    id = responseSpecification.id ?: requestBody.id,
                    result = responseSpecification.result,
                    error = responseSpecification.error,
                )
        }
    }
}
