package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.JSONRPCError
import me.kpavlov.aimocks.a2a.model.RequestId
import me.kpavlov.aimocks.a2a.model.SetTaskPushNotificationRequest
import me.kpavlov.aimocks.a2a.model.SetTaskPushNotificationResponse
import me.kpavlov.aimocks.a2a.model.TaskPushNotificationConfig
import me.kpavlov.aimocks.a2a.model.TaskPushNotificationConfigBuilder
import me.kpavlov.aimocks.core.AbstractResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
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
