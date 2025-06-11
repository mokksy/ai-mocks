package me.kpavlov.aimocks.a2a

import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.a2a.model.TaskResubscriptionRequest
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent
import me.kpavlov.aimocks.core.ResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

/**
 * Response specification for task resubscription operation.
 */
public class TaskResubscriptionResponseSpecification(
    response: AbstractResponseDefinition<String>,
    public var responseFlow: Flow<TaskUpdateEvent>? = null,
    public var delayBetweenChunks: Duration = Duration.ZERO,
    delay: Duration = Duration.ZERO,
) : ResponseSpecification<TaskResubscriptionRequest, String>(
    response = response,
    delay = delay
)
