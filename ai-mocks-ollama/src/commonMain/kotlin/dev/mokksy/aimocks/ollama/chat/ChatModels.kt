@file:OptIn(ExperimentalSerializationApi::class)

package dev.mokksy.aimocks.ollama.chat

import dev.mokksy.aimocks.core.json.schema.SchemaDefinition
import dev.mokksy.aimocks.ollama.model.Format
import dev.mokksy.aimocks.ollama.model.ModelOptions
import kotlinx.datetime.Instant
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a request to generate the next message in a chat with a provided model.
 *
 * See [Ollama API](https://github.com/ollama/ollama/blob/main/docs/api.md) for details.
 *
 * @property model The model name to use for generation (required)
 * @property messages The messages of the chat, used to keep chat memory
 * @property tools List of tools in JSON for the model to use if supported
 * @property think Should the model think before responding? (for thinking models)
 * @property format The format to return a response in. Format can be "json" or a JSON schema
 * @property options Additional model parameters such as temperature.
 *  See [Valid Parameters and Values](https://github.com/ollama/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values)
 * @property stream If false the response will be returned as a single response object, rather than a stream of objects
 * @property keepAlive Controls how long the model will stay loaded into memory following the request (default: "5m")
 */
@Serializable
public data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val tools: List<Tool>? = null,
    val think: Boolean? = null,
    val format: Format? = null,
    val options: ModelOptions? = null,
    @EncodeDefault
    val stream: Boolean = true,
    @SerialName("keep_alive")
    val keepAlive: String? = null,
)

/**
 * Represents a message in a chat conversation.
 *
 * @property role The role of the message, either "system", "user", "assistant", or "tool"
 * @property content The content of the message
 * @property thinking The model's thinking process (for thinking models)
 * @property images A list of images to include in the message (for multimodal models)
 * @property toolCalls A list of tools in JSON that the model wants to use
 * @property toolName The name of the tool that was executed to inform the model of the result
 */
@Serializable
public data class Message(
    val role: String,
    val content: String,
    val thinking: String? = null,
    val images: List<String>? = null,
    @SerialName("tool_calls")
    val toolCalls: List<ToolCall>? = null,
    @SerialName("tool_name")
    val toolName: String? = null,
)

/**
 * Represents a tool that can be used by the model.
 *
 * @property type The type of the tool, always "function" for now
 * @property function The function object that describes the tool
 */
@Serializable
public data class Tool(
    @EncodeDefault
    val type: String = "function",
    val function: FunctionDefinition,
)

/**
 * Represents a function definition that can be called by the model.
 *
 * @property name The name of the function
 * @property description A description of what the function does
 * @property parameters The parameters the function accepts, in JSON Schema format
 */
@Serializable
public data class FunctionDefinition(
    val name: String,
    val description: String? = null,
    val parameters: SchemaDefinition,
)

/**
 * Represents a tool call made by the model.
 *
 * @property id The unique identifier for this tool call
 * @property type The type of the tool call, always "function" for now
 * @property function The function that was called
 */
@Serializable
public data class ToolCall(
    val id: String,
    @EncodeDefault
    val type: String = "function",
    val function: FunctionCall,
)

/**
 * Represents a function call made by the model.
 *
 * @property name The name of the function
 * @property arguments The arguments to call the function with, in JSON format
 */
@Serializable
public data class FunctionCall(
    val name: String,
    val arguments: String,
)

/**
 * Represents a response from the chat completion endpoint.
 *
 * @property model The model name used for generation
 * @property createdAt Timestamp when the response was created
 * @property message The generated message
 * @property done Indicates if the generation is complete
 * @property totalDuration Time spent generating the response (in nanoseconds)
 * @property loadDuration Time spent loading the model (in nanoseconds)
 * @property promptEvalCount Number of tokens in the prompt
 * @property promptEvalDuration Time spent evaluating the prompt (in nanoseconds)
 * @property evalCount Number of tokens in the response
 * @property evalDuration Time spent generating the response (in nanoseconds)
 */
@Serializable
public data class ChatResponse(
    val model: String,
    @SerialName("created_at")
    val createdAt: Instant,
    val message: Message,
    val done: Boolean,
    @SerialName("done_reason")
    val doneReason: String? = null,
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
