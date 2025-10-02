package me.kpavlov.aimocks.ollama.embed

import io.ktor.http.ContentType
import kotlinx.datetime.Clock
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.aimocks.core.EmbeddingUtils
import me.kpavlov.mokksy.BuildingStep
import me.kpavlov.mokksy.MokksyServer
import kotlin.random.Random.Default.nextInt

/**
 * OllamaEmbedBuildingStep is a specialized implementation of [AbstractBuildingStep]
 * intended for constructing and managing embedding responses as part of the Ollama
 * Mock Server setup.
 *
 * The class provides features to create responses for simulated embedding requests.
 * It extends the functionality of [AbstractBuildingStep] by applying specific logic
 * for generating fake responses compliant with Ollama's embedding API.
 *
 * @constructor Initializes the building step with the provided mock server instance and
 *              a higher-level building step for configuring embedding responses.
 *
 * @param mokksy The mock server instance used for handling mock request and response lifecycle.
 * @param buildingStep The underlying building step for managing and supporting response configurations
 *                     for Ollama Embedding requests.
 */
public class OllamaEmbedBuildingStep(
    mokksy: MokksyServer,
    buildingStep: BuildingStep<EmbeddingsRequest>,
) : AbstractBuildingStep<EmbeddingsRequest, OllamaEmbedResponseSpecification>(
        mokksy,
        buildingStep,
    ) {
    /**
     * Configures the mock embedding response for an embedding request using the provided specification block.
     *
     * The block allows customization of the embedding response, including embeddings, model name, and response delay.
     * If embeddings are not explicitly set, they are generated for each input string in the request.
     * The response includes randomized timing and evaluation count values to simulate realistic API behavior.
     *
     * @param block Lambda to customize the embedding response specification.
     */
    override infix fun responds(block: OllamaEmbedResponseSpecification.() -> Unit) {
        buildingStep.respondsWith {
            val request = this.request.body
            val responseDefinition = this.build()
            val embedResponseSpecification =
                OllamaEmbedResponseSpecification(responseDefinition)
            block.invoke(embedResponseSpecification)
            val embeddings =
                embedResponseSpecification.embeddings
                    ?: request.input.map { EmbeddingUtils.generateEmbedding(it) }
            val modelName = embedResponseSpecification.model ?: request.model
            delay = embedResponseSpecification.delay
            contentType = ContentType.Application.Json

            @Suppress("MagicNumber")
            val promptEvalCount = nextInt(1, 200)

            body =
                EmbeddingsResponse(
                    embeddings = embeddings,
                    model = modelName,
                    createdAt = Clock.System.now(),
                    totalDuration = nextInt(10, 5000).toLong(),
                    loadDuration = nextInt(10, 1000).toLong(),
                    promptEvalCount = promptEvalCount,
                    promptEvalDuration = nextInt(10, 5000).toLong(),
                )
        }
    }
}
