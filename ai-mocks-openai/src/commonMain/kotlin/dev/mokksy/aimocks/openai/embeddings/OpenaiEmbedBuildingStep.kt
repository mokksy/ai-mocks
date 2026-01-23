package dev.mokksy.aimocks.openai.embeddings

import dev.mokksy.aimocks.core.AbstractBuildingStep
import dev.mokksy.aimocks.core.EmbeddingUtils
import dev.mokksy.aimocks.openai.model.embeddings.CreateEmbeddingsRequest
import dev.mokksy.aimocks.openai.model.embeddings.Embeddings
import dev.mokksy.aimocks.openai.model.embeddings.EmbeddingsResponse
import dev.mokksy.aimocks.openai.model.embeddings.Usage
import dev.mokksy.mokksy.BuildingStep
import dev.mokksy.mokksy.MokksyServer
import io.ktor.http.ContentType
import kotlin.random.Random.Default.nextInt

/**
 * OpenaiEmbedBuildingStep is a specialized implementation of [AbstractBuildingStep]
 * intended for constructing and managing embedding responses as part of the OpenAI
 * Mock Server setup.
 *
 * The class provides features to create responses for simulated embedding requests.
 * It extends the functionality of [AbstractBuildingStep] by applying specific logic
 * for generating fake responses compliant with OpenAI's embedding API.
 *
 * @constructor Initializes the building step with the provided mock server instance and
 *              a higher-level building step for configuring embedding responses.
 *
 * @param mokksy The mock server instance used for handling mock request and response lifecycle.
 * @param buildingStep The underlying building step for managing and supporting response configurations
 *                     for OpenAI Embedding requests.
 *
 * @see <a href="https://platform.openai.com/docs/api-reference/embeddings">OpenAI Embeddings API</a>
 */
public class OpenaiEmbedBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<CreateEmbeddingsRequest>,
) : AbstractBuildingStep<CreateEmbeddingsRequest, OpenaiEmbedResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    /**
     * Configures the mock embedding response for an OpenAI embedding request using the provided specification block.
     *
     * The block allows customization of the embedding response, including embeddings, model name, and response delay.
     * If embeddings are not explicitly set, they are automatically generated for each input string in the request.
     * The response follows the OpenAI embeddings API format to simulate realistic API behavior.
     *
     * @param block Lambda to customize the embedding response specification.
     * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/create">Create Embeddings</a>
     */
    @Suppress("MagicNumber")
    override infix fun responds(block: OpenaiEmbedResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val request = this.request.body
            val responseDefinition = this.build()
            val responseSpecification = OpenaiEmbedResponseSpecification(responseDefinition)
            block.invoke(responseSpecification)
            val embeddings =
                responseSpecification.embeddings
                    ?: request.input.map { EmbeddingUtils.generateEmbedding(it) }
            delay = responseSpecification.delay
            contentType = ContentType.Application.Json

            val promptTokens = nextInt(1, 100)
            val totalTokens = nextInt(promptTokens, promptTokens + 500)
            body =
                EmbeddingsResponse(
                    data =
                        embeddings.mapIndexed { index, list ->
                            Embeddings(
                                embeddings = list,
                                index = index,
                            )
                        },
                    model = request.model,
                    usage =
                        Usage(
                            promptTokens = promptTokens,
                            totalTokens = totalTokens,
                        ),
                )
        }
    }
}
