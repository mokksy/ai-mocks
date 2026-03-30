package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.SendMessageRequest
import dev.mokksy.aimocks.a2a.model.SendMessageResponse
import dev.mokksy.aimocks.core.AbstractBuildingStep
import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer

public class SendMessageBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<SendMessageRequest>,
) : AbstractBuildingStep<SendMessageRequest, SendMessageResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    override infix fun responds(block: suspend SendMessageResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val requestBody = request.body()
            val responseSpecification = SendMessageResponseSpecification()
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
