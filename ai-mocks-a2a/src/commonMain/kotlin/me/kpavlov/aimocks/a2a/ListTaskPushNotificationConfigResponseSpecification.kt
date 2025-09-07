package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.JSONRPCError
import me.kpavlov.aimocks.a2a.model.ListTaskPushNotificationConfigRequest
import me.kpavlov.aimocks.a2a.model.ListTaskPushNotificationConfigResponse
import me.kpavlov.aimocks.a2a.model.RequestId
import me.kpavlov.aimocks.a2a.model.TaskPushNotificationConfig
import me.kpavlov.aimocks.core.AbstractResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

public class ListTaskPushNotificationConfigResponseSpecification(
    response: AbstractResponseDefinition<ListTaskPushNotificationConfigResponse>,
    public var id: RequestId? = null,
    public var result: List<TaskPushNotificationConfig>? = null,
    public var error: JSONRPCError? = null,
    delay: Duration = Duration.ZERO,
) : AbstractResponseSpecification<ListTaskPushNotificationConfigRequest, ListTaskPushNotificationConfigResponse>(
        response = response,
        delay = delay,
    )
