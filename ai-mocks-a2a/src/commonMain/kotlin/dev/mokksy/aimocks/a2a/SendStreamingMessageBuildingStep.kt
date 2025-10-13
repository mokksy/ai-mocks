package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.SendStreamingMessageRequest
import dev.mokksy.aimocks.a2a.model.SendStreamingMessageResponse
import dev.mokksy.aimocks.core.AbstractStreamingBuildingStep
import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

public class SendStreamingMessageBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<SendStreamingMessageRequest>,
) : AbstractStreamingBuildingStep<SendStreamingMessageRequest, SendStreamingMessageResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    public override infix fun respondsStream(
        block: SendStreamingMessageResponseSpecification.() -> Unit,
    ) {
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
