@file:OptIn(ExperimentalSerializationApi::class)

package dev.mokksy.aimocks.openai.model.chat

import dev.mokksy.aimocks.openai.model.ChatCompletionRole
import dev.mokksy.aimocks.openai.model.ChatCompletionStreamOptions
import kotlinx.schema.json.JsonSchema
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode.ALWAYS
import kotlinx.serialization.EncodeDefault.Mode.NEVER
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive

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
 * Represents a content part in a message.
 *
 * Content can be text, images, audio, or other media types.
 * Each content part has a type field that identifies its structure.
 *
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/create">OpenAI Chat API</a>
 */
@Serializable
public sealed class ContentPart {
    /**
     * Text content part.
     *
     * @property type The type of content, always "text".
     * @property text The text content.
     */
    @Serializable
    @SerialName("text")
    public data class Text(
        @EncodeDefault(ALWAYS)
        val type: String = "text",
        val text: String,
    ) : ContentPart()

    /**
     * Output text content part (used in assistant responses).
     *
     * @property type The type of content, always "output_text".
     * @property text The output text content.
     * @property annotations Optional list of annotations for the text.
     */
    @Serializable
    @SerialName("output_text")
    public data class OutputText(
        @EncodeDefault(ALWAYS)
        val type: String = "output_text",
        val text: String,
        val annotations: List<String> = emptyList(),
    ) : ContentPart()

    /**
     * Image URL content part.
     *
     * @property type The type of content, always "image_url".
     * @property imageUrl The image URL object containing the URL and optional detail level.
     */
    @Serializable
    @SerialName("image_url")
    public data class ImageUrl(
        @EncodeDefault(ALWAYS)
        val type: String = "image_url",
        @SerialName("image_url")
        val imageUrl: ImageUrlObject,
    ) : ContentPart()

    /**
     * Input audio content part.
     *
     * @property type The type of content, always "input_audio".
     * @property inputAudio The audio input object containing the audio data.
     */
    @Serializable
    @SerialName("input_audio")
    public data class InputAudio(
        @EncodeDefault(ALWAYS)
        val type: String = "input_audio",
        @SerialName("input_audio")
        val inputAudio: AudioInputObject,
    ) : ContentPart()
}

/**
 * Represents an image URL object.
 *
 * @property url The URL of the image or base64 encoded image data.
 * @property detail Optional detail level for image processing ("auto", "low", or "high").
 */
@Serializable
public data class ImageUrlObject(
    val url: String,
    val detail: String? = null,
)

/**
 * Represents an audio input object.
 *
 * @property data Base64 encoded audio data.
 * @property format The format of the audio data (e.g., "wav", "mp3").
 */
@Serializable
public data class AudioInputObject(
    val data: String,
    val format: String,
)

/**
 * Represents a message in a chat conversation.
 *
 * According to the OpenAI specification, the content field can be either:
 * - A string for simple text messages
 * - An array of [ContentPart] objects for multimodal messages (text, images, audio, etc.)
 *
 * @property role The role of the message sender (system, user, assistant, etc.).
 * @property content The content of the message as either a string or array of content parts.
 * @property refusal Optional refusal message if the content was refused.
 * @property toolCalls Optional list of tool calls made in this message.
 * @property name Optional name of the author of this message.
 * @property toolCallId Optional ID of the tool call this message is responding to.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/create">OpenAI Chat API</a>
 * @author Konstantin Pavlov
 */
@Serializable
public data class Message(
    val role: ChatCompletionRole,
    @Serializable(MessageContentSerializer::class)
    val content: MessageContent,
    val refusal: String? = null,
    @SerialName("tool_calls")
    val toolCalls: List<ToolCall>? = null,
    val name: String? = null,
    @SerialName("tool_call_id")
    val toolCallId: String? = null,
)

/**
 * Represents the content of a message, which can be either a simple string or an array of content parts.
 */
@Serializable
public sealed class MessageContent {
    /**
     * Simple string content.
     */
    @Serializable
    public data class Text(
        val text: String,
    ) : MessageContent()

    /**
     * Array of content parts for multimodal messages.
     */
    @Serializable
    public data class Parts(
        val parts: List<ContentPart>,
    ) : MessageContent()

    /**
     * Extracts all text content from the message, regardless of whether it's a simple string
     * or an array of content parts.
     *
     * For [Text], returns the text directly.
     * For [Parts], concatenates all text from text-based content parts.
     *
     * @return The concatenated text content, or empty string if no text is available.
     */
    public fun asText(): String =
        when (this) {
            is Text -> {
                text
            }

            is Parts -> {
                parts
                    .mapNotNull { part ->
                        when (part) {
                            is ContentPart.Text -> part.text
                            is ContentPart.OutputText -> part.text
                            else -> null
                        }
                    }.joinToString(" ")
            }
        }

    /**
     * Checks if the message content contains the specified substring.
     *
     * @param substring The substring to search for.
     * @param ignoreCase Whether to ignore case when searching (default: false).
     * @return `true` if the content contains the substring, `false` otherwise.
     */
    public fun contains(
        substring: String,
        ignoreCase: Boolean = false,
    ): Boolean = asText().contains(substring, ignoreCase)
}

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
 * @property parameters Optional parameters the function accepts, described as a JSON Schema object.
 * @property strict Whether to enable strict schema adherence.
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
@Serializable(with = ToolChoiceSerializer::class)
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
 * Custom serializer for [ToolChoice] that handles both string and object formats.
 *
 * According to the OpenAI specification, tool_choice can be:
 * - A simple string ("auto" or "none")
 * - An object with type "function" and a function field
 *
 * This serializer automatically converts between these formats.
 */
public class ToolChoiceSerializer : KSerializer<ToolChoice> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ToolChoice")

    @Suppress("ThrowsCount")
    override fun deserialize(decoder: Decoder): ToolChoice {
        val jsonDecoder =
            decoder as? JsonDecoder
                ?: throw SerializationException("This serializer can only be used with JSON")

        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> {
                when (element.contentOrNull) {
                    "auto" -> ToolChoice.Auto

                    "none" -> ToolChoice.None

                    else -> throw SerializationException(
                        "Unknown tool choice: ${element.contentOrNull}",
                    )
                }
            }

            is JsonObject -> {
                val type = element["type"]?.jsonPrimitive?.contentOrNull ?: "function"
                if (type != "function") {
                    throw SerializationException("Unknown tool choice type: $type")
                }
                val functionElement =
                    element["function"]
                        ?: throw SerializationException("tool_choice.function is required")
                val function =
                    jsonDecoder.json.decodeFromJsonElement(
                        ToolChoiceFunction.serializer(),
                        functionElement,
                    )
                ToolChoice.Function(function)
            }

            else -> {
                throw SerializationException("Unsupported tool choice payload: $element")
            }
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: ToolChoice,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: throw SerializationException("This serializer can only be used with JSON")

        when (value) {
            is ToolChoice.Auto -> {
                jsonEncoder.encodeJsonElement(JsonPrimitive("auto"))
            }

            is ToolChoice.None -> {
                jsonEncoder.encodeJsonElement(JsonPrimitive("none"))
            }

            is ToolChoice.Function -> {
                val functionElement =
                    jsonEncoder.json.encodeToJsonElement(
                        ToolChoiceFunction.serializer(),
                        value.function,
                    )
                jsonEncoder.encodeJsonElement(
                    JsonObject(
                        mapOf(
                            "type" to JsonPrimitive("function"),
                            "function" to functionElement,
                        ),
                    ),
                )
            }
        }
    }
}

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

/**
 * Custom serializer for [MessageContent] that handles both string and array formats.
 *
 * According to the OpenAI specification, message content can be:
 * - A simple string (serialized as JSON string)
 * - An array of content parts (serialized as JSON array)
 *
 * This serializer automatically converts between these formats.
 */
public class MessageContentSerializer : KSerializer<MessageContent> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MessageContent")

    override fun deserialize(decoder: Decoder): MessageContent {
        val jsonDecoder =
            decoder as? JsonDecoder
                ?: throw SerializationException("This serializer can only be used with JSON")

        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> {
                // Simple string content
                MessageContent.Text(element.contentOrNull.orEmpty())
            }

            is JsonArray -> {
                // Array of content parts
                val parts =
                    element.map { partElement ->
                        jsonDecoder.json.decodeFromJsonElement<ContentPart>(partElement)
                    }
                MessageContent.Parts(parts)
            }

            else -> {
                throw SerializationException("Expected string or array for message content")
            }
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: MessageContent,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: throw SerializationException("This serializer can only be used with JSON")

        when (value) {
            is MessageContent.Text -> {
                jsonEncoder.encodeJsonElement(JsonPrimitive(value.text))
            }

            is MessageContent.Parts -> {
                val array =
                    JsonArray(
                        value.parts.map { part ->
                            when (part) {
                                is ContentPart.Text -> {
                                    jsonEncoder.json.encodeToJsonElement(
                                        ContentPart.Text.serializer(),
                                        part,
                                    )
                                }

                                is ContentPart.OutputText -> {
                                    jsonEncoder.json.encodeToJsonElement(
                                        ContentPart.OutputText.serializer(),
                                        part,
                                    )
                                }

                                is ContentPart.ImageUrl -> {
                                    jsonEncoder.json.encodeToJsonElement(
                                        ContentPart.ImageUrl.serializer(),
                                        part,
                                    )
                                }

                                is ContentPart.InputAudio -> {
                                    jsonEncoder.json.encodeToJsonElement(
                                        ContentPart.InputAudio.serializer(),
                                        part,
                                    )
                                }
                            }
                        },
                    )
                jsonEncoder.encodeJsonElement(array)
            }
        }
    }
}
