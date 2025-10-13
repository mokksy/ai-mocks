@file:OptIn(ExperimentalSerializationApi::class)

package dev.mokksy.aimocks.openai.model.embeddings

import dev.mokksy.mokksy.serializers.StringOrListSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a request to the OpenAI embeddings API.
 *
 * @property model ID of the model to use for generating embeddings.
 * @property input Input text to embed, encoded as a string or array of strings.
 * @property dimensions The number of dimensions the resulting output embeddings should have.
 *  Only supported in text-embedding-3 and later models.
 * @property encodingFormat The format to return the embeddings in. Can be either "float" or "base64".
 * @property user A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
 *
 * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/create">OpenAI Embeddings API</a>
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
public data class CreateEmbeddingsRequest(
    val model: String,
    @Serializable(StringOrListSerializer::class)
    val input: List<String>,
    val dimensions: Int? = null,
    @SerialName("encoding_format")
    val encodingFormat: String? = "float",
    val user: String? = null,
)

/**
 * Represents a response from the OpenAI embeddings API.
 *
 * @property embeddings The list of embedding vectors, where each vector represents the embeddings for one input.
 * @property index The index of the embedding in the list of embeddings.
 * @property objectType The object type, which is always "embedding" for embedding objects.
 *
 * @see <a href="https://platform.openai.com/docs/api-reference/embeddings/object">OpenAI Embedding Object</a>
 */
@Serializable
@OptIn(ExperimentalSerializationApi::class)
public data class Embeddings(
    @SerialName("embedding")
    val embeddings: List<Float>,
    @EncodeDefault
    val index: Int = 0,
    @SerialName("object")
    @EncodeDefault
    val objectType: String = "embedding",
)

@Serializable
public data class Usage(
    @SerialName("prompt_tokens")
    val promptTokens: Int? = null,
    @SerialName("total_tokens")
    val totalTokens: Int? = null,
)

@Serializable
public data class EmbeddingsResponse(
    @SerialName("object")
    @EncodeDefault
    val objectType: String = "list",
    @Contextual
    val data: List<Embeddings>,
    val model: String? = null,
    val usage: Usage? = null,
)
