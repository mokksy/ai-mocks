@file:OptIn(ExperimentalSerializationApi::class)

package dev.mokksy.aimocks.ollama.generate

import dev.mokksy.aimocks.ollama.model.Format
import dev.mokksy.aimocks.ollama.model.ModelOptions
import kotlinx.datetime.Instant
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a request to generate a completion for a given prompt with a provided model.
 *
 * @property model The model name to use for generation (required)
 * @property prompt The prompt to generate a response for
 * @property suffix The text after the model response
 * @property images A list of base64-encoded images (for multimodal models such as llava)
 * @property think Should the model think before responding? (for thinking models)
 * @property format The format to return a response in. Format can be "json" or a JSON schema
 * @property options Additional model parameters such as temperature
 * @property system System message (overrides what is defined in the Modelfile)
 * @property template The prompt template to use (overrides what is defined in the Modelfile)
 * @property stream If false the response will be returned as a single response object, rather than a stream of objects
 * @property raw If true no formatting will be applied to the prompt
 * @property keepAlive Controls how long the model will stay loaded into memory following the request (default: "5m")
 * @property context The context parameter returned from a previous request, used to keep a short conversational memory
 */
@Serializable
public data class GenerateRequest(
    val model: String,
    val prompt: String? = null,
    val suffix: String? = null,
    val images: List<String>? = null,
    val think: Boolean? = null,
    val format: Format? = null,
    val options: ModelOptions? = null,
    val system: String? = null,
    val template: String? = null,
    @EncodeDefault
    val stream: Boolean = true,
    val raw: Boolean? = null,
    @SerialName("keep_alive")
    val keepAlive: String? = null,
    val context: List<Int>? = null,
)

/**
 * Represents a response from the generate completion endpoint.
 *
 * @property model The model name used for generation
 * @property createdAt Timestamp when the response was created
 * @property response The generated text response (empty if streamed)
 * @property done Indicates if the generation is complete
 * @property doneReason The reason why generation completed (e.g., "stop", "length")
 * @property context An encoding of the conversation used in this response
 * @property totalDuration Time spent generating the response (in nanoseconds)
 * @property loadDuration Time spent loading the model (in nanoseconds)
 * @property promptEvalCount Number of tokens in the prompt
 * @property promptEvalDuration Time spent evaluating the prompt (in nanoseconds)
 * @property evalCount Number of tokens in the response
 * @property evalDuration Time spent generating the response (in nanoseconds)
 */
@Serializable
public data class GenerateResponse(
    val model: String,
    @SerialName("created_at")
    val createdAt: Instant,
    val response: String,
    val done: Boolean,
    @SerialName("done_reason")
    val doneReason: String? = null,
    val context: List<Int>? = null,
    @SerialName("total_duration")
    val totalDuration: Long? = null,
    @SerialName("load_duration")
    val loadDuration: Long? = null,
    @SerialName("prompt_eval_count")
    val promptEvalCount: Int? = null,
    @SerialName("prompt_eval_duration")
    val promptEvalDuration: Long? = null,
    @SerialName("eval_count")
    val evalCount: Int? = null,
    @SerialName("eval_duration")
    val evalDuration: Long? = null,
)
