package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.SetTaskPushNotificationRequest
import dev.mokksy.aimocks.a2a.model.SetTaskPushNotificationResponse
import dev.mokksy.aimocks.core.AbstractBuildingStep
import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer

public class SetTaskPushNotificationBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<SetTaskPushNotificationRequest>,
) : AbstractBuildingStep<
        SetTaskPushNotificationRequest,
        SetTaskPushNotificationResponseSpecification,
    >(
        mokksy,
        buildingStep,
    ) {
    override infix fun responds(block: SetTaskPushNotificationResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val requestBody = request.body
            val responseDefinition = this.build()
            val responseSpecification =
                SetTaskPushNotificationResponseSpecification(responseDefinition)
            block.invoke(responseSpecification)
            body =
                SetTaskPushNotificationResponse(
                    id = responseSpecification.id ?: requestBody.id,
                    result = responseSpecification.result,
                    error = responseSpecification.error,
                )
        }
    }
}
