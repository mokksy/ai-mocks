package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.TaskResubscriptionRequest
import dev.mokksy.aimocks.a2a.model.TaskUpdateEvent
import dev.mokksy.aimocks.core.AbstractStreamingResponseSpecification
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

/**
 * Response specification for task resubscription operation.
 */
public class TaskResubscriptionResponseSpecification(
    response: AbstractResponseDefinition<String>,
    responseFlow: Flow<TaskUpdateEvent>? = null,
    delayBetweenChunks: Duration = Duration.ZERO,
    delay: Duration = Duration.ZERO,
) : AbstractStreamingResponseSpecification<TaskResubscriptionRequest, TaskUpdateEvent, String>(
        responseFlow = responseFlow,
        response = response,
        delayBetweenChunks = delayBetweenChunks,
        delay = delay,
        responseChunks = null,
    )
