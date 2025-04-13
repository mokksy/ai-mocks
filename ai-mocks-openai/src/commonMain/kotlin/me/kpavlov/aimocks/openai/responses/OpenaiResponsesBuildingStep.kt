package me.kpavlov.aimocks.openai.responses

import me.kpavlov.aimocks.core.LlmBuildingStep
import me.kpavlov.aimocks.openai.model.OutputContent
import me.kpavlov.aimocks.openai.model.OutputMessage
import me.kpavlov.aimocks.openai.model.responses.CreateResponseRequest
import me.kpavlov.aimocks.openai.model.responses.Response
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

/**
 * Represents a building step in the MokksyServer framework for configuring OpenAI responses.
 *
 * @constructor Initializes the `OpenaiResponsesBuildingStep` with the specified Mokksy server instance
 * and a building step for handling the creation of responses.
 *
 * @param mokksy The instance of `MokksyServer` used for this building step.
 * @param buildingStep The building step associated with configuring responses of type `CreateResponseRequest`.
 * @author Konstantin Pavlov
 */
public class OpenaiResponsesBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<CreateResponseRequest>,
) : LlmBuildingStep<CreateResponseRequest, OpenaiResponsesResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    private var counter: AtomicInteger = AtomicInteger(1)

    @OptIn(ExperimentalStdlibApi::class)
    @Suppress("MagicNumber")
    public override infix fun responds(block: OpenaiResponsesResponseSpecification.() -> Unit) {
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
                    id = "resp_${counter.addAndGet(1).toHexString()}",
                    model = request.model,
                    metadata = null,
                    instructions = null,
                    tools = emptyList(),
                    toolChoice = "auto",
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
}
