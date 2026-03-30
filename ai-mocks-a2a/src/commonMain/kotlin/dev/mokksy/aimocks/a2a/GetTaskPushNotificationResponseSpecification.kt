package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.GetTaskPushNotificationRequest
import dev.mokksy.aimocks.a2a.model.GetTaskPushNotificationResponse
import dev.mokksy.aimocks.a2a.model.JSONRPCError
import dev.mokksy.aimocks.a2a.model.RequestId
import dev.mokksy.aimocks.a2a.model.TaskPushNotificationConfig
import dev.mokksy.aimocks.core.AbstractResponseSpecification
import kotlin.time.Duration

public class GetTaskPushNotificationResponseSpecification(
    public var id: RequestId? = null,
    public var result: TaskPushNotificationConfig? = null,
    public var error: JSONRPCError? = null,
    delay: Duration = Duration.ZERO,
) : AbstractResponseSpecification<GetTaskPushNotificationRequest, GetTaskPushNotificationResponse>(
        delay = delay,
    )
