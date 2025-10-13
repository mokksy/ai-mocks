package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.ListTaskPushNotificationConfigRequest
import dev.mokksy.aimocks.a2a.model.ListTaskPushNotificationConfigResponse
import dev.mokksy.aimocks.core.AbstractBuildingStep
import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer

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
