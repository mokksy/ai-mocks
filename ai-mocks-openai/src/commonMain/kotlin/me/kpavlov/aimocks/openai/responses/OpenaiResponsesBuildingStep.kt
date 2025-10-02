package me.kpavlov.aimocks.openai.responses

import io.ktor.http.ContentType
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.aimocks.openai.model.OutputContent
import me.kpavlov.aimocks.openai.model.OutputMessage
import me.kpavlov.aimocks.openai.model.responses.CreateResponseRequest
import me.kpavlov.aimocks.openai.model.responses.InputTokensDetails
import me.kpavlov.aimocks.openai.model.responses.OutputTokensDetails
import me.kpavlov.aimocks.openai.model.responses.Response
import me.kpavlov.aimocks.openai.model.responses.Usage
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
 * @see <a href="https://platform.openai.com/docs/api-reference/responses">Responses API</a>
 * @author Konstantin Pavlov
 */
public class OpenaiResponsesBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<CreateResponseRequest>,
) : AbstractBuildingStep<CreateResponseRequest, OpenaiResponsesResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    private var counter: AtomicInteger = AtomicInteger(1)

    @Suppress("MagicNumber")
    public override infix fun responds(block: OpenaiResponsesResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val request = this.request.body
            val responseDefinition = this.build()
            val chatResponseSpecification = OpenaiResponsesResponseSpecification(responseDefinition)
            block.invoke(chatResponseSpecification)
            val assistantContent = chatResponseSpecification.assistantContent
            delay = chatResponseSpecification.delay
            contentType = ContentType.Application.Json

            val inputTokens = Random.Default.nextInt(1, 200)
            val outputTokens = Random.Default.nextInt(1, request.maxOutputTokens ?: 1500)
            val reasoningTokens = outputTokens / 3

            body =
                Response(
                    id = "resp_${Integer.toHexString(counter.addAndGet(1))}",
                    model = request.model,
                    metadata = null,
                    instructions = null,
                    tools = emptyList(),
                    toolChoice = "auto",
                    createdAt = Instant.now().epochSecond,
                    temperature = request.temperature,
                    maxOutputTokens = request.maxOutputTokens,
                    error = null,
                    incompleteDetails = null,
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
                    usage =
                        Usage(
                            inputTokens = inputTokens,
                            inputTokensDetails =
                                InputTokensDetails(
                                    cachedTokens = 0,
                                ),
                            outputTokens = outputTokens,
                            outputTokensDetails =
                                OutputTokensDetails(
                                    reasoningTokens = reasoningTokens,
                                ),
                            totalTokens = inputTokens + outputTokens,
                        ),
                )
        }
    }
}
