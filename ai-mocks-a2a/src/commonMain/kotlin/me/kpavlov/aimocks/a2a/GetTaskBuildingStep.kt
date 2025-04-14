package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.GetTaskRequest
import me.kpavlov.aimocks.a2a.model.GetTaskResponse
import me.kpavlov.mokksy.MokksyServer

public class GetTaskBuildingStep(
    private val mokksy: MokksyServer,
) {
    public infix fun responds(block: GetTaskResponse.() -> Unit) {
        mokksy
            .post(requestType = GetTaskRequest::class) {
                this.path("/")
                this.bodyMatchesPredicate {
                    it?.method == "tasks/get"
                }
            }.respondsWith<GetTaskResponse> {
                val response = GetTaskResponse()
                block(response)
                this.body = response
            }
    }
}
