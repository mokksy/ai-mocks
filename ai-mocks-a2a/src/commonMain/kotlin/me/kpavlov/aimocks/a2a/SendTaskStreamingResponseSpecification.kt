package me.kpavlov.aimocks.a2a

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.stream.consumeAsFlow
import me.kpavlov.aimocks.a2a.model.JSONRPCError
import me.kpavlov.aimocks.a2a.model.SendTaskStreamingRequest
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent
import me.kpavlov.aimocks.core.AbstractResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import java.util.stream.Stream
import kotlin.time.Duration

public class SendTaskStreamingResponseSpecification(
    response: AbstractResponseDefinition<String>,
    public var responseFlow: Flow<TaskUpdateEvent>? = null,
    public var delayBetweenChunks: Duration = Duration.ZERO,
    public var error: JSONRPCError? = null,
    delay: Duration = Duration.ZERO,
) : AbstractResponseSpecification<SendTaskStreamingRequest, String>(
    response = response,
    delay = delay
) {
    /**
     * Java-friendly setter for [responseFlow].
     */
    public fun responseFlow(flow: Flow<TaskUpdateEvent>): SendTaskStreamingResponseSpecification =
        apply {
            this.responseFlow = flow
        }

    public fun delayBetweenChunks(delay: Duration): SendTaskStreamingResponseSpecification =
        apply { this.delayBetweenChunks = delay }

    public fun delay(delay: Duration): SendTaskStreamingResponseSpecification =
        apply { this.delay = delay }

    public fun stream(stream: Stream<TaskUpdateEvent>): SendTaskStreamingResponseSpecification =
        apply { this.responseFlow = stream.consumeAsFlow() }
}
