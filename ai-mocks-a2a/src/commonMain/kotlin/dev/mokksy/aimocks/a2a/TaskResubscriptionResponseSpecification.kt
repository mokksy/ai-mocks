package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.TaskResubscriptionRequest
import dev.mokksy.aimocks.a2a.model.TaskUpdateEvent
import dev.mokksy.aimocks.core.AbstractStreamingResponseSpecification
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

/**
 * Response specification for task resubscription operation.
 */
public class TaskResubscriptionResponseSpecification(
    responseFlow: Flow<TaskUpdateEvent>? = null,
    delayBetweenChunks: Duration = Duration.ZERO,
    delay: Duration = Duration.ZERO,
) : AbstractStreamingResponseSpecification<TaskResubscriptionRequest, TaskUpdateEvent, String>(
        responseFlow = responseFlow,
        delayBetweenChunks = delayBetweenChunks,
        delay = delay,
        responseChunks = null,
    )
