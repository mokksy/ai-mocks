@file:OptIn(ExperimentalSerializationApi::class)

package dev.mokksy.aimocks.openai

import dev.mokksy.aimocks.openai.model.ChatCompletionRole
import dev.mokksy.aimocks.openai.model.ChatCompletionStreamOptions
import dev.mokksy.aimocks.openai.model.chat.MessageContent
import kotlinx.schema.json.JsonSchema
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode.ALWAYS
import kotlinx.serialization.EncodeDefault.Mode.NEVER
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

// Full OpenAPI Spec is here: https://raw.githubusercontent.com/openai/openai-openapi/refs/heads/master/openapi.yaml

@Serializable
internal data class Chunk(
    val id: String,
    /**
     * Always "chat.completion.chunk"
     */
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
 * Represents a single choice in a chat completion response.
 *
 * @property index The index of this choice in the list of choices.
 * @property delta A delta message for streaming responses.
 * @property message The full message for non-streaming responses.
 * @property logprobs Log probability information for the choice.
 * @property finishReason The reason the model stopped generating tokens.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/object#chat/object-choices">Chat Completion Choice</a>
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
 * Represents an incremental message update in a streaming chat completion.
 *
 * @property role The role of the author of this message.
 * @property content The content of the message.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/streaming">Chat Completion Streaming</a>
 */
@Serializable
public data class Delta(
    val role: ChatCompletionRole? = null,
    val content: String? = null,
)

/**
 * Represents a chat completion response from the OpenAI API.
 *
 * @property id A unique identifier for the chat completion.
 * @property objectType The object type, which is always "chat.completions".
 * @property created Unix timestamp (in seconds) of when the chat completion was created.
 * @property model The model used for the chat completion.
 * @property serviceTier The service tier used for processing the request.
 * @property systemFingerprint A fingerprint representing the backend configuration.
 * @property usage Usage statistics for the completion request.
 * @property choices A list of chat completion choices.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/object">Chat Completion Object</a>
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
 * Usage statistics for a completion request.
 *
 * @property promptTokens Number of tokens in the prompt.
 * @property completionTokens Number of tokens in the generated completion.
 * @property totalTokens Total number of tokens used in the request (prompt + completion).
 * @property completionTokensDetails Breakdown of completion tokens by category.
 * @property promptTokensDetails Breakdown of prompt tokens by category.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/object#chat/object-usage">Usage Object</a>
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
 * Detailed breakdown of completion tokens by category.
 *
 * @property reasoningTokens Tokens generated as part of reasoning.
 * @property acceptedPredictionTokens Tokens from predictions that were accepted.
 * @property rejectedPredictionTokens Tokens from predictions that were rejected.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/object#chat/object-completion_tokens_details">Completion Tokens Details</a>
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
 * Detailed breakdown of prompt tokens by category.
 *
 * @property cachedTokens Number of tokens that were cached and reused.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/object#chat/object-prompt_tokens_details">Prompt Tokens Details</a>
 */
@Serializable
public data class TokenDetails(
    @SerialName("cached_tokens")
    val cachedTokens: Int? = null,
)

/**
 * Optional metadata associated with a request.
 *
 * @property tags A map of key-value pairs for tagging requests.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-metadata">Metadata</a>
 */
@Serializable
public data class Metadata(
    val tags: Map<String, String>? = null,
)

/**
 * Represents a request for generating a chat-based completions in an OpenAI-like environment.
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
 * @property responseFormat Defines the response format, including optional JSON schema support.
 * @property temperature A value between 0.0 and 1.0 that controls the randomness of the generated response.
 * @property seed Can be used to produce deterministic responses in testing.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/create">Create Chat Completion</a>
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
    @SerialName("response_format")
    val responseFormat: ResponseFormat? = null,
    val temperature: Double = 1.0,
    val seed: Int? = null,
    val stream: Boolean = false,
    @SerialName("stream_options")
    val streamOptions: ChatCompletionStreamOptions? = null,
    val tools: List<Tool>? = null,
)

/**
 * Represents a tool that can be called by the model.
 *
 * @property type The type of the tool. Currently, only "function" is supported.
 * @property function The function definition.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-tools">Tools</a>
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
 * @property name The name of the function to be called. Must be a-z, A-Z, 0-9,
 *                or contain underscores and dashes, with a maximum length of 64.
 * @property description A description of what the function does, used by the model to choose
 *                       when and how to call the function.
 * @property parameters The parameters the function accepts, described as a JSON Schema object.
 * @property strict Whether to enable strict schema adherence when generating the function call.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-tools">Function Calling</a>
 */
@Serializable
public data class FunctionObject(
    @SerialName(value = "name") @Required val name: String,
    @SerialName(value = "description") val description: String? = null,
    @SerialName(value = "parameters")
    val parameters: JsonElement? = null,
    @SerialName(value = "strict") val strict: Boolean? = false,
)

/**
 * Represents a message in a chat completion.
 *
 * According to the OpenAI specification, the content field can be either:
 * - A string for simple text messages
 * - An array of content parts for multimodal messages (text, images, audio, etc.)
 *
 * @property role The role of the message author (system, user, assistant, or tool).
 * @property content The content of the message as either a string or array of content parts.
 * @property refusal The refusal message if the model refused to generate a response.
 * @property toolCalls The tool calls generated by the model.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-messages">Messages</a>
 */
@Serializable
public data class Message(
    val role: ChatCompletionRole,
    @Serializable(dev.mokksy.aimocks.openai.model.chat.MessageContentSerializer::class)
    val content: MessageContent,
    val refusal: String? = null,
    @SerialName("tool_calls")
    val toolCalls: List<ToolCall>? = null,
)

/**
 * Represents a tool call generated by the model.
 *
 * @property id The ID of the tool call.
 * @property type The type of the tool. Currently, only "function" is supported.
 * @property function The function to be called.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/object#chat/object-choices-message-tool_calls">Tool Calls</a>
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
 * Note that the model does not always generate valid JSON,
 * and may hallucinate parameters not defined by your function schema.
 * Validate the arguments in your code before calling your function.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/object#chat/object-choices-message-tool_calls-function">
 *     Function Object</a>
 */
@Serializable
public data class CallableFunction(
    val name: String,
    val arguments: String,
)

/**
 * Specifies the format that the model must output.
 *
 * @property type The type of response format (e.g., "text", "json_object", "json_schema").
 * @property jsonSchema The JSON schema that the output should conform to.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-response_format">
 *     Response Format</a>
 */
@Serializable
public data class ResponseFormat(
    val type: String,
    @SerialName("json_schema")
    val jsonSchema: JsonSchema? = null,
)
