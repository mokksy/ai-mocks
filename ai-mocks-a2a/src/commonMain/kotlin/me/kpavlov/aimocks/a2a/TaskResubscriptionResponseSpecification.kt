package me.kpavlov.aimocks.a2a

import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.a2a.model.TaskResubscriptionRequest
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent
import me.kpavlov.aimocks.core.StreamingResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

/**
 * Response specification for task resubscription operation.
 */
public class TaskResubscriptionResponseSpecification(
    response: AbstractResponseDefinition<String>,
    responseFlow: Flow<TaskUpdateEvent>? = null,
    delayBetweenChunks: Duration = Duration.ZERO,
    delay: Duration = Duration.ZERO,
) : StreamingResponseSpecification<TaskResubscriptionRequest, TaskUpdateEvent, String>(
    responseFlow = responseFlow,
    response = response,
    delayBetweenChunks = delayBetweenChunks,
    delay = delay,
    responseChunks = null,
)
