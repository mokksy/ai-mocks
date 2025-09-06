@file:OptIn(ExperimentalSerializationApi::class)

package me.kpavlov.aimocks.openai.model.chat

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode.ALWAYS
import kotlinx.serialization.EncodeDefault.Mode.NEVER
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.kpavlov.aimocks.core.json.schema.JsonSchema
import me.kpavlov.aimocks.openai.model.ChatCompletionRole
import me.kpavlov.aimocks.openai.model.ChatCompletionStreamOptions

/**
 * Represents a request for generating a chat-based completions in an OpenAI-like environment.
 * See [Create chat completions](https://platform.openai.com/docs/api-reference/chat/create).
 *
 * This data class is used for serialization and defines the parameters required to send
 * a chat completions request, including the input messages, model to use, and various tuning parameters.
 *
 * @property messages A list of input messages that define the conversation context, each with a role and content.
 * @property model The identifier of the language model to be used for generating the completions.
 * @property store A flag indicating whether the conversation context should be stored for further use.
 * @property reasoningEffort Specifies the level of computational effort to apply during reasoning
 * ("low", "medium", "high").
 * @property metadata Optional metadata associated with the request, such as tags.
 * @property maxCompletionTokens The maximum number of tokens allowed in the generated completions.
 * @property frequencyPenalty The penalty value for repetitive token usage in the response.
 * @property presencePenalty The penalty value for new token usage in the response.
 * @property responseFormat Defines the response format, including optional JSON schema support.
 * @property temperature A value between 0.0 and 1.0 that controls the randomness of the generated response.
 * @property topP An alternative to sampling with temperature, called nucleus sampling.
 * @property n How many chat completion choices to generate for each input message.
 * @property stop Up to 4 sequences where the API will stop generating further tokens.
 * @property logitBias Modify the likelihood of specified tokens appearing in the completion.
 * @property user A unique identifier representing your end-user.
 * @property seed Can be used to produce deterministic responses in testing.
 * @property stream Whether to stream the response.
 * @property streamOptions Options for streaming responses.
 * @property tools List of tools the model may call.
 * @property toolChoice Controls which (if any) function is called by the model.
 * @author Konstantin Pavlov
 */
@Serializable
public data class ChatCompletionRequest(
    val messages: List<Message>,
    val model: String,
    val store: Boolean = false,
    @SerialName("reasoning_effort")
    val reasoningEffort: String = "medium",
    val metadata: Metadata? = null,
    @SerialName("max_completion_tokens")
    val maxCompletionTokens: Int? = null,
    @SerialName("frequency_penalty")
    val frequencyPenalty: Double? = 0.0,
    @SerialName("presence_penalty")
    val presencePenalty: Double? = 0.0,
    @SerialName("response_format")
    val responseFormat: ResponseFormat? = null,
    val temperature: Double = 1.0,
    @SerialName("top_p")
    val topP: Double? = 1.0,
    val n: Int? = 1,
    val stop: List<String>? = null,
    @SerialName("logit_bias")
    val logitBias: Map<String, Int>? = null,
    val user: String? = null,
    val seed: Int? = null,
    val stream: Boolean = false,
    @SerialName("stream_options")
    val streamOptions: ChatCompletionStreamOptions? = null,
    val tools: List<Tool>? = null,
    @SerialName("tool_choice")
    val toolChoice: ToolChoice? = null,
)

/**
 * Represents a message in a chat conversation.
 *
 * @property role The role of the message sender (system, user, assistant, etc.).
 * @property content The content of the message.
 * @property refusal Optional refusal message if the content was refused.
 * @property toolCalls Optional list of tool calls made in this message.
 * @property name Optional name of the author of this message.
 * @property toolCallId Optional ID of the tool call this message is responding to.
 * @author Konstantin Pavlov
 */
@Serializable
public data class Message(
    val role: ChatCompletionRole,
    val content: String,
    val refusal: String? = null,
    @SerialName("tool_calls")
    val toolCalls: List<ToolCall>? = null,
    val name: String? = null,
    @SerialName("tool_call_id")
    val toolCallId: String? = null,
)

/**
 * Represents metadata for a request.
 *
 * @property tags Optional map of tags associated with the request.
 * @author Konstantin Pavlov
 */
@Serializable
public data class Metadata(
    val tags: Map<String, String>? = null,
)

/**
 * Represents a tool that can be used by the model.
 *
 * @property type The type of the tool, always "function" for now.
 * @property function The function object that describes the tool.
 * @author Konstantin Pavlov
 */
@Serializable
public data class Tool(
    @EncodeDefault(ALWAYS)
    val type: String = "function",
    val function: FunctionObject,
)

/**
 * Represents a function that can be called by the model.
 *
 * @property name The name of the function.
 * @property description Optional description of what the function does.
 * @property parameters Optional parameters the function accepts.
 * @property strict Whether to enable strict schema adherence.
 */
@Serializable
public data class FunctionObject(
    @SerialName(value = "name") @Required val name: String,
    @SerialName(value = "description") val description: String? = null,
    @SerialName(value = "parameters")
    val parameters: Map<String, String>? = null,
    @SerialName(value = "strict") val strict: Boolean? = false,
)

/**
 * Represents a tool call made by the model.
 *
 * @property id The unique identifier for this tool call.
 * @property type The type of the tool call, always "function" for now.
 * @property function The function that was called.
 */
@Serializable
public data class ToolCall(
    val id: String,
    @EncodeDefault(ALWAYS)
    val type: String = "function",
    val function: CallableFunction,
)

/**
 * Represents a function that can be called by the AI model.
 *
 * @property name The name of the function.
 * @property arguments The arguments to call the function with,
 * as generated by the model in JSON format.
 */
@Serializable
public data class CallableFunction(
    val name: String,
    val arguments: String,
)

/**
 * Represents the format for responses.
 *
 * @property type The type of the response format.
 * @property jsonSchema Optional JSON schema for the response.
 */
@Serializable
public data class ResponseFormat(
    val type: String,
    @SerialName("json_schema")
    val jsonSchema: JsonSchema? = null,
)

/**
 * Represents a sealed class that defines choices for tools used in a process or operation.
 *
 * This class serves as the base for specifying different tool-related choices.
 * Possible options include automatic tool selection, no tool usage, or specifying
 * a function-based tool choice.
 *
 * Subclasses:
 * - [Auto]: Represents the automatic tool selection mode.
 * - [None]: Represents a state where no tool is selected.
 * - [Function]: Represents a specific tool choice based on a [ToolChoiceFunction].
 */
@Serializable
public sealed class ToolChoice {
    @Serializable
    @SerialName("auto")
    public object Auto : ToolChoice()

    @Serializable
    @SerialName("none")
    public object None : ToolChoice()

    @Serializable
    @SerialName("function")
    public data class Function(
        val function: ToolChoiceFunction,
    ) : ToolChoice()
}

/**
 * Represents a function that the model should call.
 *
 * @property name The name of the function to call.
 */
@Serializable
public data class ToolChoiceFunction(
    val name: String,
)

/**
 * Represents a response to a chat completion request.
 *
 * @property id The unique identifier for this response.
 * @property objectType The type of the object, always "chat.completions".
 * @property created The timestamp when this response was created.
 * @property model The model used to generate this response.
 * @property serviceTier Optional service tier information.
 * @property systemFingerprint Optional system fingerprint.
 * @property usage Usage statistics for this response.
 * @property choices The list of completion choices.
 */
@Serializable
public data class ChatResponse(
    val id: String,
    @EncodeDefault(ALWAYS)
    @SerialName("object")
    @Required
    val objectType: String = "chat.completions",
    val created: Long,
    val model: String,
    @SerialName("service_tier")
    val serviceTier: String? = null,
    @SerialName("system_fingerprint")
    val systemFingerprint: String? = null,
    val usage: Usage,
    val choices: List<Choice>,
)

/**
 * Represents a chunk of a streaming response.
 *
 * @property id The unique identifier for this chunk.
 * @property objectType The type of the object, always "chat.completion.chunk".
 * @property created The timestamp when this chunk was created.
 * @property model The model used to generate this chunk.
 * @property serviceTier Optional service tier information.
 * @property systemFingerprint The system fingerprint.
 * @property usage Optional usage statistics for this chunk.
 * @property choices The list of completion choices in this chunk.
 */
@Serializable
public data class Chunk(
    val id: String,
    @SerialName("object")
    @EncodeDefault(ALWAYS)
    val objectType: String = "chat.completion.chunk",
    val created: Long,
    val model: String,
    @SerialName("service_tier")
    val serviceTier: String? = null,
    @SerialName("system_fingerprint")
    val systemFingerprint: String,
    val usage: Usage? = null,
    val choices: List<Choice>,
)

/**
 * Represents a choice in a completion response.
 *
 * @property index The index of this choice.
 * @property delta Optional delta for streaming responses.
 * @property message Optional message for non-streaming responses.
 * @property logprobs Optional log probabilities.
 * @property finishReason Optional reason why the completion finished.
 */
@Serializable
public data class Choice(
    val index: Int,
    @EncodeDefault(NEVER)
    val delta: Delta? = null,
    @EncodeDefault(NEVER)
    val message: Message? = null,
    @EncodeDefault(ALWAYS)
    val logprobs: String? = null,
    @EncodeDefault(ALWAYS)
    @SerialName("finish_reason")
    val finishReason: String? = null,
)

/**
 * Represents a delta in a streaming response.
 *
 * @property role Optional role of the message sender.
 * @property content Optional content of the message.
 */
@Serializable
public data class Delta(
    val role: ChatCompletionRole? = null,
    val content: String? = null,
)

/**
 * Represents usage statistics for a completion.
 *
 * @property promptTokens The number of tokens in the prompt.
 * @property completionTokens The number of tokens in the completion.
 * @property totalTokens The total number of tokens.
 * @property completionTokensDetails Details about the completion tokens.
 * @property promptTokensDetails Optional details about the prompt tokens.
 */
@Serializable
public data class Usage(
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int,
    @SerialName("completion_tokens_details")
    val completionTokensDetails: CompletionTokensDetails,
    @SerialName("prompt_tokens_details")
    val promptTokensDetails: TokenDetails? = null,
)

/**
 * Represents details about completion tokens.
 *
 * @property reasoningTokens The number of tokens used for reasoning.
 * @property acceptedPredictionTokens The number of accepted prediction tokens.
 * @property rejectedPredictionTokens The number of rejected prediction tokens.
 */
@Serializable
public data class CompletionTokensDetails(
    @SerialName("reasoning_tokens")
    val reasoningTokens: Int,
    @SerialName("accepted_prediction_tokens")
    val acceptedPredictionTokens: Int,
    @SerialName("rejected_prediction_tokens")
    val rejectedPredictionTokens: Int,
)

/**
 * Represents details about tokens.
 *
 * @property cachedTokens Optional number of cached tokens.
 */
@Serializable
public data class TokenDetails(
    @SerialName("cached_tokens")
    val cachedTokens: Int? = null,
)
