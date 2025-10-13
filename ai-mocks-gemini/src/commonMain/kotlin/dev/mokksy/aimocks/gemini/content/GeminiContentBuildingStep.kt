package dev.mokksy.aimocks.gemini.content

import dev.mokksy.aimocks.core.AbstractBuildingStep
import dev.mokksy.aimocks.gemini.GenerateContentRequest
import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer
import io.ktor.http.ContentType

/**
 * Building step for configuring responses to Gemini content generation requests.
 *
 * This class provides methods for configuring both regular and streaming responses
 * to Gemini content generation requests.
 *
 * @property mokksy The MokksyServer instance to use for configuring responses.
 * @property buildingStep The BuildingStep instance to use for configuring responses.
 */
public class GeminiContentBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<GenerateContentRequest>,
) : AbstractBuildingStep<GenerateContentRequest, GeminiContentResponseSpecification>(
        mokksy = mokksy,
        buildingStep = buildingStep,
    ) {
    /**
     * Configures a regular (non-streaming) response to a Gemini content generation request.
     *
     * @param block A lambda that configures the response specification.
     * @return This building step instance for method chaining.
     */
    public override infix fun responds(block: GeminiContentResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val generateContentRequest = this.request.body
            val responseDefinition = this.build()
            val chatResponseSpecification = GeminiContentResponseSpecification(responseDefinition)
            block.invoke(chatResponseSpecification)
            val assistantContent = chatResponseSpecification.content
            delay = chatResponseSpecification.delay
            contentType = ContentType.Application.Json
            body =
                generateContentResponse(
                    assistantContent = assistantContent,
                    finishReason = chatResponseSpecification.finishReason.uppercase(),
                    modelVersion = generateContentRequest.model,
                )
        }
    }
}
