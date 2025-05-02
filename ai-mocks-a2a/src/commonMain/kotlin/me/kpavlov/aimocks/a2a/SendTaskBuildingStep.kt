package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.SendTaskRequest
import me.kpavlov.aimocks.a2a.model.SendTaskResponse
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer

public class SendTaskBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<SendTaskRequest>,
) : AbstractBuildingStep<SendTaskRequest, SendTaskResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    override infix fun responds(block: SendTaskResponseSpecification.() -> Unit) {
        buildingStep.respondsWith<SendTaskResponse> {
            val requestBody = request.body
            val responseDefinition = this.build()
            val responseSpecification = SendTaskResponseSpecification(responseDefinition)
            block.invoke(responseSpecification)
            body =
                SendTaskResponse(
                    id = responseSpecification.id ?: requestBody.id,
                    result = responseSpecification.result,
                    error = responseSpecification.error,
                )
        }
    }
}
