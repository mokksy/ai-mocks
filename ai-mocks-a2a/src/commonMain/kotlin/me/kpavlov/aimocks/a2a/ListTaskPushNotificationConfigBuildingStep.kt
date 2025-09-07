package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.ListTaskPushNotificationConfigRequest
import me.kpavlov.aimocks.a2a.model.ListTaskPushNotificationConfigResponse
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer

public class ListTaskPushNotificationConfigBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<ListTaskPushNotificationConfigRequest>,
) : AbstractBuildingStep<
        ListTaskPushNotificationConfigRequest,
        ListTaskPushNotificationConfigResponseSpecification,
    >(
        mokksy,
        buildingStep,
    ) {
    override infix fun responds(
        block: ListTaskPushNotificationConfigResponseSpecification.() -> Unit,
    ) {
        buildingStep.respondsWith {
            val requestBody = request.body
            val responseDefinition = this.build()
            val responseSpecification =
                ListTaskPushNotificationConfigResponseSpecification(responseDefinition)
            block.invoke(responseSpecification)
            body =
                ListTaskPushNotificationConfigResponse(
                    id = responseSpecification.id ?: requestBody.id,
                    result = responseSpecification.result,
                    error = responseSpecification.error,
                )
        }
    }
}
