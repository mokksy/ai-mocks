package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.DeleteTaskPushNotificationConfigRequest
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer

/**
 * Building step for configuring delete task push notification config responses.
 */
public class DeleteTaskPushNotificationConfigBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<DeleteTaskPushNotificationConfigRequest>,
) : AbstractBuildingStep<DeleteTaskPushNotificationConfigRequest, DeleteTaskPushNotificationConfigResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    override infix fun responds(
        block: DeleteTaskPushNotificationConfigResponseSpecification.() -> Unit,
    ) {
        buildingStep.respondsWith {
            val responseDefinition = this.build()
            val responseSpec =
                DeleteTaskPushNotificationConfigResponseSpecification(responseDefinition)
            block.invoke(responseSpec)
            delay = responseSpec.delay
            body = responseSpec.build()
        }
    }
}
