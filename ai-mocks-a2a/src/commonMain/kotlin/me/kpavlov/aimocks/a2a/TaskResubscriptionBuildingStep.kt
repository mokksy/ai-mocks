package me.kpavlov.aimocks.a2a

import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.a2a.model.SendTaskStreamingResponse
import me.kpavlov.aimocks.a2a.model.TaskResubscriptionRequest
import me.kpavlov.aimocks.core.AbstractStreamingBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer

/**
 * Building step for task resubscription operation.
 */
public class TaskResubscriptionBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<TaskResubscriptionRequest>,
) : AbstractStreamingBuildingStep<TaskResubscriptionRequest, TaskResubscriptionResponseSpecification>(
    mokksy,
    buildingStep,
) {
    public override infix fun respondsStream(block: TaskResubscriptionResponseSpecification.() -> Unit) {
        buildingStep.respondsWithStream {
            val requestBody = request.body
            val responseDefinition = this.build()
            val responseSpecification = TaskResubscriptionResponseSpecification(responseDefinition)
            block.invoke(responseSpecification)
            flow =
                responseSpecification.responseFlow
                    ?.map {
                        SendTaskStreamingResponse(
                            id = requestBody.id,
                            result = it,
                        )
                    }?.map { Json.encodeToString(it) }
                    ?.map { ServerSentEvent(data = it).toString() + '\n' }
        }
    }
}
