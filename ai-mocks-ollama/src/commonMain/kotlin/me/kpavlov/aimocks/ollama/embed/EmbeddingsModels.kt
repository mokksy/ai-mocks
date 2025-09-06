package me.kpavlov.aimocks.ollama.embed

import kotlinx.datetime.Instant
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.kpavlov.aimocks.ollama.model.ModelOptions
import me.kpavlov.mokksy.serializers.StringOrListSerializer

/**
 * Represents a request to generate embeddings for a single string or list of string inputs with a provided model.
 *
 * @property model The model name to use for generation (required)
 * @property input List of text to generate embeddings for
 * @property truncate Truncates the end of each input to fit within context length.
 * Returns error if false and context length is exceeded. Defaults to true
 * @property options Additional model parameters
 * @property keepAlive Controls how long the model will stay loaded into memory following the request (default: "5m")
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
public data class EmbeddingsRequest(
    val model: String,
    @Serializable(StringOrListSerializer::class)
    val input: List<String>,
    @EncodeDefault
    val truncate: Boolean? = true,
    val options: ModelOptions? = null,
    @SerialName("keep_alive")
    val keepAlive: String? = null,
)

/**
 * Represents a response from the embeddings endpoint.
 *
 * @property embeddings The generated embedding vectors
 * @property model The model name used for generation
 * @property createdAt Timestamp when the response was created
 * @property totalDuration Time spent generating the embeddings (in nanoseconds)
 * @property loadDuration Time spent loading the model (in nanoseconds)
 * @property promptEvalCount Number of tokens in the prompt
 * @property promptEvalDuration Time spent evaluating the prompt (in nanoseconds)
 */
@Serializable
public data class EmbeddingsResponse(
    val embeddings: List<List<Float>>,
    val model: String,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("total_duration")
    val totalDuration: Long? = null,
    @SerialName("load_duration")
    val loadDuration: Long? = null,
    @SerialName("prompt_eval_count")
    val promptEvalCount: Int? = null,
    @SerialName("prompt_eval_duration")
    val promptEvalDuration: Long? = null,
)
