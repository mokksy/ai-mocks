package dev.mokksy.aimocks.openai.embeddings

import dev.mokksy.aimocks.core.AbstractResponseSpecification
import dev.mokksy.aimocks.openai.model.embeddings.CreateEmbeddingsRequest
import dev.mokksy.aimocks.openai.model.embeddings.EmbeddingsResponse
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Specification for configuring OpenAI embedding responses.
 *
 * This class is used to specify the content and behavior of responses to OpenAI embedding requests.
 * It allows specifying the embeddings, index, and other response properties according to the OpenAI API format.
 *
 * @property embeddings The list of embedding vectors to include in the response
 * @property index The index of the embedding in the list of embeddings
 *
 * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/object">OpenAI Embedding Object</a>
 */
public class OpenaiEmbedResponseSpecification(
    response: AbstractResponseDefinition<EmbeddingsResponse>,
    public var embeddings: List<List<Float>>? = null,
    public var index: Int? = null,
    delay: Duration = 0.seconds,
) : AbstractResponseSpecification<CreateEmbeddingsRequest, EmbeddingsResponse>(
        response = response,
        delay = delay,
    ) {
    /**
     * Sets the embeddings to be included in the response.
     *
     * @param embeddings A list of embedding vectors, where each vector represents the embeddings for one input.
     * @return This specification instance for method chaining.
     * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/object#embeddings-object-embedding">embedding property</a>
     */
    public fun embeddings(embeddings: List<List<Float>>): OpenaiEmbedResponseSpecification {
        this.embeddings = embeddings
        return this
    }

    /**
     * Sets the embeddings to include in the response using one or more embedding vectors.
     *
     * @param embedding One or more embedding vectors to be included in the response.
     * @return This specification instance for method chaining.
     * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/object#embeddings-object-embedding">embedding property</a>
     */
    public fun embeddings(vararg embedding: List<Float>): OpenaiEmbedResponseSpecification {
        this.embeddings = embedding.toList()
        return this
    }

    /**
     * Sets the index of the embedding in the list of embeddings.
     *
     * @param index The index of the embedding in the list of embeddings returned by the OpenAI API.
     * @return This specification instance for method chaining.
     * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/object#embeddings-object-index">index property</a>
     */
    public fun index(index: Int?): OpenaiEmbedResponseSpecification {
        this.index = index
        return this
    }
}
