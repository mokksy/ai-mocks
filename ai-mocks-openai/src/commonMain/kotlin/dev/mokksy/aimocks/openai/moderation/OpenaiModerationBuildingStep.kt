package dev.mokksy.aimocks.openai.moderation

import dev.mokksy.aimocks.core.AbstractBuildingStep
import dev.mokksy.aimocks.openai.model.moderation.CreateModerationRequest
import dev.mokksy.aimocks.openai.model.moderation.Moderation
import dev.mokksy.aimocks.openai.model.moderation.ModerationResult
import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer
import io.ktor.http.ContentType
import java.util.concurrent.atomic.AtomicInteger

/**
 * Builder step for configuring mock responses to OpenAI moderation requests.
 *
 * @param mokksy The mock server instance.
 * @param buildingStep The underlying building step for moderation requests.
 * @see <a href="https://platform.openai.com/docs/api-reference/moderations">OpenAI Moderations API</a>
 */
public class OpenaiModerationBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<CreateModerationRequest>,
) : AbstractBuildingStep<CreateModerationRequest, OpenaiModerationResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    private val counter: AtomicInteger = AtomicInteger(1)

    override infix fun responds(block: OpenaiModerationResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val responseDefinition = this.build()
            val spec = OpenaiModerationResponseSpecification(responseDefinition)
            block.invoke(spec)
            delay = spec.delay
            contentType = ContentType.Application.Json
            val id = spec.id ?: "modr-${Integer.toHexString(counter.addAndGet(1))}"
            val createdModel = spec.model
            val result: ModerationResult = spec.toResult()

            body =
                Moderation(
                    id = id,
                    model = createdModel,
                    results = listOf(result),
                )
        }
    }
}
