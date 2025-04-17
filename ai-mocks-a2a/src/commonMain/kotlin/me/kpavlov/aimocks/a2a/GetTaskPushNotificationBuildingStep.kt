package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.GetTaskPushNotificationRequest
import me.kpavlov.aimocks.a2a.model.GetTaskPushNotificationResponse
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer

public class GetTaskPushNotificationBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<GetTaskPushNotificationRequest>,
) : AbstractBuildingStep<
        GetTaskPushNotificationRequest,
        GetTaskPushNotificationResponseSpecification,
    >(
        mokksy,
        buildingStep,
    ) {
    override infix fun responds(block: GetTaskPushNotificationResponseSpecification.() -> Unit) {
        buildingStep.respondsWith<GetTaskPushNotificationResponse> {
            val requestBody = request.body
            val responseDefinition = this.build()
            val responseSpecification =
                GetTaskPushNotificationResponseSpecification(responseDefinition)
            block.invoke(responseSpecification)
            val config =
                requireNotNull(
                    responseSpecification.result,
                ) { "TaskPushNotificationConfig must be defined" }
            body =
                GetTaskPushNotificationResponse(
                    id = responseSpecification.id ?: requestBody.id,
                    result = config,
                )
        }
    }
}
