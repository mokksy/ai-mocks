package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.SendTaskRequest
import me.kpavlov.aimocks.a2a.model.SendTaskResponse
import me.kpavlov.mokksy.MokksyServer

public class SendTaskBuildingStep(
    private val mokksy: MokksyServer,
) {
    public infix fun responds(block: SendTaskResponse.() -> Unit) {
        mokksy
            .post(requestType = SendTaskRequest::class) {
                this.path("/")
                this.bodyMatchesPredicate {
                    it?.method == "tasks/send"
                }
            }.respondsWith<SendTaskResponse> {
                val response = SendTaskResponse()
                block(response)
                this.body = response
            }
    }
}
