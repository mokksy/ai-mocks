package me.kpavlov.aimocks.ollama.embed

import me.kpavlov.aimocks.core.AbstractResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
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
     * Specifies the embeddings to include in the response.
     *
     * @param embeddings The embeddings
     * @return This specification for method chaining
     */
    public fun embeddings(embeddings: List<List<Float>>): OllamaEmbedResponseSpecification {
        this.embeddings = embeddings
        return this
    }

    /**
     * Specifies a single or multiple embedding to include in the response.
     *
     * @param embedding The embedding
     * @return This specification for method chaining
     */
    public fun embeddings(vararg embedding: List<Float>): OllamaEmbedResponseSpecification {
        this.embeddings = embedding.toList()
        return this
    }

    /**
     * Specifies the model name to include in the response.
     *
     * @param model The model name
     * @return This specification for method chaining
     */
    public fun model(model: String): OllamaEmbedResponseSpecification {
        this.model = model
        return this
    }
}
