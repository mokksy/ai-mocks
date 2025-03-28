package me.kpavlov.aimocks.openai.responses

import me.kpavlov.aimocks.core.LlmBuildingStep
import me.kpavlov.aimocks.openai.model.OutputContent
import me.kpavlov.aimocks.openai.model.OutputMessage
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

public class OpenaiResponsesBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<CreateResponseRequest>,
) : LlmBuildingStep<CreateResponseRequest, OpenaiResponsesResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    private var counter: AtomicInteger = AtomicInteger(1)

    @Suppress("MagicNumber")
    public infix fun respond(block: OpenaiResponsesResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val request = this.request.body
            val responseDefinition = this.build()
            val chatResponseSpecification = OpenaiResponsesResponseSpecification(responseDefinition)
            block.invoke(chatResponseSpecification)
            val assistantContent = chatResponseSpecification.assistantContent
            val finishReason = chatResponseSpecification.finishReason
            delay = chatResponseSpecification.delay

            val promptTokens = Random.Default.nextInt(1, 200)
            val completionTokens = Random.Default.nextInt(1, 1500)
            val reasoningTokens = completionTokens / 3
            val acceptedPredictionTokens = (completionTokens - reasoningTokens) / 2
            val rejectedPredictionTokens =
                completionTokens - reasoningTokens - acceptedPredictionTokens

            body =
                Response(
                    id = "chatcmpl-abc${counter.addAndGet(1)}",
                    model = request.model,
                    metadata = null,
                    instructions = null,
                    tools = emptyList(),
                    toolChoice = emptyMap(),
                    createdAt = Instant.now().epochSecond,
//                    error = null,
//                    incompleteDetails = null,
                    output =
                        listOf(
                            OutputMessage(
                                id = "msg_",
                                type = OutputMessage.Type.MESSAGE,
                                role = OutputMessage.Role.ASSISTANT,
                                content =
                                    listOf(
                                        OutputContent(
                                            type = OutputContent.Type.OUTPUT_TEXT,
                                            text = assistantContent,
                                            annotations = emptyList(),
                                            refusal = "",
                                        ),
                                    ),
                                status = OutputMessage.Status.COMPLETED,
                            ),
                        ),
                )
        }
    }

    override fun responds(block: OpenaiResponsesResponseSpecification.() -> Unit) {
        TODO("Not yet implemented")
    }
}
