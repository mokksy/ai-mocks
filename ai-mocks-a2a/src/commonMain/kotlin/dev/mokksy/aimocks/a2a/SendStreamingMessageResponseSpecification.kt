package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.JSONRPCError
import dev.mokksy.aimocks.a2a.model.SendStreamingMessageRequest
import dev.mokksy.aimocks.a2a.model.TaskUpdateEvent
import dev.mokksy.aimocks.core.AbstractResponseSpecification
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.stream.consumeAsFlow
import java.util.stream.Stream
import kotlin.time.Duration

public class SendStreamingMessageResponseSpecification(
    response: AbstractResponseDefinition<String>,
    public var responseFlow: Flow<TaskUpdateEvent>? = null,
    public var delayBetweenChunks: Duration = Duration.ZERO,
    public var error: JSONRPCError? = null,
    delay: Duration = Duration.ZERO,
) : AbstractResponseSpecification<SendStreamingMessageRequest, String>(
        response = response,
        delay = delay,
    ) {
    /**
     * Java-friendly setter for [responseFlow].
     */
    public fun responseFlow(
        flow: Flow<TaskUpdateEvent>,
    ): SendStreamingMessageResponseSpecification =
        apply {
            this.responseFlow = flow
        }

    public fun delayBetweenChunks(delay: Duration): SendStreamingMessageResponseSpecification =
        apply { this.delayBetweenChunks = delay }

    public fun delay(delay: Duration): SendStreamingMessageResponseSpecification =
        apply {
            this.delay =
                delay
        }

    public fun stream(stream: Stream<TaskUpdateEvent>): SendStreamingMessageResponseSpecification =
        apply { this.responseFlow = stream.consumeAsFlow() }
}
