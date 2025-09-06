package me.kpavlov.aimocks.a2a

import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.a2a.model.SendStreamingMessageRequest
import me.kpavlov.aimocks.a2a.model.SendStreamingMessageResponse
import me.kpavlov.aimocks.core.AbstractStreamingBuildingStep
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer

public class SendStreamingMessageBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<SendStreamingMessageRequest>,
) : AbstractStreamingBuildingStep<SendStreamingMessageRequest, SendStreamingMessageResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    public override infix fun respondsStream(block: SendStreamingMessageResponseSpecification.() -> Unit) {
        buildingStep.respondsWithStream {
            val requestBody = request.body
            val responseDefinition = this.build()
            val responseSpecification =
                SendStreamingMessageResponseSpecification(responseDefinition)
            block.invoke(responseSpecification)
            flow =
                responseSpecification.responseFlow
                    ?.map {
                        SendStreamingMessageResponse(
                            id = requestBody.id,
                            result = it,
                        )
                    }?.map { Json.encodeToString(it) }
                    ?.map { ServerSentEvent(data = it).toString() + '\n' }
        }
    }
}
