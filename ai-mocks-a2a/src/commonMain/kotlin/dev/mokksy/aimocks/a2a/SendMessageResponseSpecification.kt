package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.JSONRPCError
import dev.mokksy.aimocks.a2a.model.RequestId
import dev.mokksy.aimocks.a2a.model.SendMessageRequest
import dev.mokksy.aimocks.a2a.model.SendMessageResponse
import dev.mokksy.aimocks.a2a.model.Task
import dev.mokksy.aimocks.core.AbstractResponseSpecification
import kotlin.time.Duration

public class SendMessageResponseSpecification(
    public var id: RequestId? = null,
    public var result: Task? = null,
    public var error: JSONRPCError? = null,
    delay: Duration = Duration.ZERO,
) : AbstractResponseSpecification<SendMessageRequest, SendMessageResponse>(
        delay = delay,
    )
