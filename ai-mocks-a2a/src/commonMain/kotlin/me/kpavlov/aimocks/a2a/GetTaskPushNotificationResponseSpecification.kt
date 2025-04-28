package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.GetTaskPushNotificationRequest
import me.kpavlov.aimocks.a2a.model.GetTaskPushNotificationResponse
import me.kpavlov.aimocks.a2a.model.JSONRPCError
import me.kpavlov.aimocks.a2a.model.RequestId
import me.kpavlov.aimocks.a2a.model.TaskPushNotificationConfig
import me.kpavlov.aimocks.core.ResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

public class GetTaskPushNotificationResponseSpecification(
    response: AbstractResponseDefinition<GetTaskPushNotificationResponse>,
    public var id: RequestId? = null,
    public var result: TaskPushNotificationConfig? = null,
    public var error: JSONRPCError? = null,
    public var delay: Duration = Duration.ZERO,
) : ResponseSpecification<GetTaskPushNotificationRequest, GetTaskPushNotificationResponse>(
        response = response,
    )
