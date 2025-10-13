package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.JSONRPCError
import dev.mokksy.aimocks.a2a.model.ListTaskPushNotificationConfigRequest
import dev.mokksy.aimocks.a2a.model.ListTaskPushNotificationConfigResponse
import dev.mokksy.aimocks.a2a.model.RequestId
import dev.mokksy.aimocks.a2a.model.TaskPushNotificationConfig
import dev.mokksy.aimocks.core.AbstractResponseSpecification
import dev.mokksy.mokksy.response.AbstractResponseDefinition
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
