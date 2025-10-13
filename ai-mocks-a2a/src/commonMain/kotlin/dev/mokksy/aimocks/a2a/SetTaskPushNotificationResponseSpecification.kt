package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.JSONRPCError
import dev.mokksy.aimocks.a2a.model.RequestId
import dev.mokksy.aimocks.a2a.model.SetTaskPushNotificationRequest
import dev.mokksy.aimocks.a2a.model.SetTaskPushNotificationResponse
import dev.mokksy.aimocks.a2a.model.TaskPushNotificationConfig
import dev.mokksy.aimocks.a2a.model.TaskPushNotificationConfigBuilder
import dev.mokksy.aimocks.core.AbstractResponseSpecification
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

public class SetTaskPushNotificationResponseSpecification(
    response: AbstractResponseDefinition<SetTaskPushNotificationResponse>,
    public var id: RequestId? = null,
    public var result: TaskPushNotificationConfig? = null,
    public var error: JSONRPCError? = null,
    delay: Duration = Duration.ZERO,
) : AbstractResponseSpecification<SetTaskPushNotificationRequest, SetTaskPushNotificationResponse>(
        response = response,
        delay = delay,
    ) {
    public fun result(block: TaskPushNotificationConfigBuilder.() -> Unit) {
        result = TaskPushNotificationConfigBuilder().apply(block).build()
    }
}
