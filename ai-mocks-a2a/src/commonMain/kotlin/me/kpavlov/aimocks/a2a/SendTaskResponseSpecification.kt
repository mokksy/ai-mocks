package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.JSONRPCError
import me.kpavlov.aimocks.a2a.model.RequestId
import me.kpavlov.aimocks.a2a.model.SendTaskRequest
import me.kpavlov.aimocks.a2a.model.SendTaskResponse
import me.kpavlov.aimocks.a2a.model.Task
import me.kpavlov.aimocks.core.ResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

public class SendTaskResponseSpecification(
    response: AbstractResponseDefinition<SendTaskResponse>,
    public var id: RequestId? = null,
    public var result: Task? = null,
    public var error: JSONRPCError? = null,
    delay: Duration = Duration.ZERO,
) : ResponseSpecification<SendTaskRequest, SendTaskResponse>(response = response, delay = delay)
