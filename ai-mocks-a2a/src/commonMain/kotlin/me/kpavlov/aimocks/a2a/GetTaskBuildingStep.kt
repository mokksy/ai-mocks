package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.GetTaskRequest
import me.kpavlov.aimocks.a2a.model.GetTaskResponse
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer

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
