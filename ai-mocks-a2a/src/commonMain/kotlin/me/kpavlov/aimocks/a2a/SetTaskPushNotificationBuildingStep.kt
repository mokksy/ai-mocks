package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.SetTaskPushNotificationRequest
import me.kpavlov.aimocks.a2a.model.SetTaskPushNotificationResponse
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer

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
        buildingStep.respondsWith<SetTaskPushNotificationResponse> {
            val requestBody = request.body
            val responseDefinition = this.build()
            val responseSpecification =
                SetTaskPushNotificationResponseSpecification(responseDefinition)
            block.invoke(responseSpecification)
            val task = requireNotNull(responseSpecification.result) { "Task must be defined" }
            body =
                SetTaskPushNotificationResponse(
                    id = responseSpecification.id ?: requestBody.id,
                    result = task,
                )
        }
    }
}
