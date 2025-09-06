package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.JSONRPCError
import me.kpavlov.aimocks.a2a.model.RequestId
import me.kpavlov.aimocks.a2a.model.SendMessageRequest
import me.kpavlov.aimocks.a2a.model.SendMessageResponse
import me.kpavlov.aimocks.a2a.model.Task
import me.kpavlov.aimocks.core.AbstractResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

public class SendMessageResponseSpecification(
    response: AbstractResponseDefinition<SendMessageResponse>,
    public var id: RequestId? = null,
    public var result: Task? = null,
    public var error: JSONRPCError? = null,
    delay: Duration = Duration.ZERO,
) : AbstractResponseSpecification<SendMessageRequest, SendMessageResponse>(
        response = response,
        delay = delay,
    )
