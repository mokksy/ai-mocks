package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.GetTaskPushNotificationRequest
import dev.mokksy.aimocks.a2a.model.GetTaskPushNotificationResponse
import dev.mokksy.aimocks.core.AbstractBuildingStep
import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer

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
    override infix fun responds(
        block: suspend GetTaskPushNotificationResponseSpecification.() -> Unit,
    ) {
        buildingStep.respondsWith {
            val requestBody = request.body()
            val responseSpecification =
                GetTaskPushNotificationResponseSpecification()
            block.invoke(responseSpecification)
            body =
                GetTaskPushNotificationResponse(
                    id = responseSpecification.id ?: requestBody.id,
                    result = responseSpecification.result,
                    error = responseSpecification.error,
                )
        }
    }
}
