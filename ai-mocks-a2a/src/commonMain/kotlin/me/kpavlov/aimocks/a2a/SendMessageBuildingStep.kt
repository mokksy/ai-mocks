package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.SendMessageRequest
import me.kpavlov.aimocks.a2a.model.SendMessageResponse
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer

public class SendMessageBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<SendMessageRequest>,
) : AbstractBuildingStep<SendMessageRequest, SendMessageResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    override infix fun responds(block: SendMessageResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val requestBody = request.body
            val responseDefinition = this.build()
            val responseSpecification = SendMessageResponseSpecification(responseDefinition)
            block.invoke(responseSpecification)
            body =
                SendMessageResponse(
                    id = responseSpecification.id ?: requestBody.id,
                    result = responseSpecification.result,
                    error = responseSpecification.error,
                )
        }
    }
}
