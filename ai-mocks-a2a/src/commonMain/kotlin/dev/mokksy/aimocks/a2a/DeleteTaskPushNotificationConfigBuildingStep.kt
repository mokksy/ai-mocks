package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.DeleteTaskPushNotificationConfigRequest
import dev.mokksy.aimocks.core.AbstractBuildingStep
import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer

/**
 * Building step for configuring delete task push notification config responses.
 */
public class DeleteTaskPushNotificationConfigBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<DeleteTaskPushNotificationConfigRequest>,
) : AbstractBuildingStep<
        DeleteTaskPushNotificationConfigRequest,
        DeleteTaskPushNotificationConfigResponseSpecification,
    >(
        mokksy,
        buildingStep,
) {
    override infix fun responds(
        block: suspend DeleteTaskPushNotificationConfigResponseSpecification.() -> Unit,
    ) {
        buildingStep.respondsWith {
            val responseSpec =
                DeleteTaskPushNotificationConfigResponseSpecification()
            block.invoke(responseSpec)
            delay = responseSpec.delay
            body = responseSpec.build()
        }
    }
}
