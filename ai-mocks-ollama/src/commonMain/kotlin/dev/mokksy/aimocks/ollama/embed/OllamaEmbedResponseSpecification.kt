package dev.mokksy.aimocks.ollama.embed

import dev.mokksy.aimocks.core.AbstractResponseSpecification
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Specification for configuring embedding responses.
 *
 * This class is used to specify the content and behavior of responses to embedding requests.
 * It allows specifying the embeddings, model, and other response properties.
 *
 * @property embeddings The embeddings to include in the response
 * @property model The model name to include in the response
 */
public class OllamaEmbedResponseSpecification(
    response: AbstractResponseDefinition<EmbeddingsResponse>,
    public var embeddings: List<List<Float>>? = null,
    public var model: String? = null,
    delay: Duration = 0.seconds,
) : AbstractResponseSpecification<EmbeddingsRequest, EmbeddingsResponse>(
        response = response,
        delay = delay,
    ) {
    /**
     * Sets the embeddings to be included in the response.
     *
     * @param embeddings A list of embedding vectors to use in the response.
     * @return This specification instance for method chaining.
     */
    public fun embeddings(embeddings: List<List<Float>>): OllamaEmbedResponseSpecification {
        this.embeddings = embeddings
        return this
    }

    /**
     * Sets the embeddings to include in the response using one or more embedding vectors.
     *
     * @param embedding One or more embedding vectors to be included in the response.
     * @return This specification instance for method chaining.
     */
    public fun embeddings(vararg embedding: List<Float>): OllamaEmbedResponseSpecification {
        this.embeddings = embedding.toList()
        return this
    }

    /**
     * Sets the model name to be included in the embedding response.
     *
     * @param model The name of the model to use.
     * @return This specification instance for method chaining.
     */
    public fun model(model: String): OllamaEmbedResponseSpecification {
        this.model = model
        return this
    }
}
