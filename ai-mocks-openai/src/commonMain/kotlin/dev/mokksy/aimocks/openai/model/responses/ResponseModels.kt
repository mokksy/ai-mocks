@file:OptIn(ExperimentalSerializationApi::class)

package dev.mokksy.aimocks.openai.model.responses

import dev.mokksy.aimocks.openai.model.OutputMessage
import dev.mokksy.aimocks.openai.model.Reasoning
import dev.mokksy.aimocks.openai.model.ResponseError
import dev.mokksy.aimocks.openai.model.chat.Tool
import kotlinx.serialization.Contextual
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode.ALWAYS
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

/**
 * Represents a request to create a response in the OpenAI API.
 *
 * @property model The model to use for generating the response.
 * @property input The input for the response (text, messages, etc.).
 * @property metadata Optional metadata for the request.
 * @property temperature Controls the randomness of the response.
 * @property topP Controls the diversity of the response.
 * @property user Optional user identifier.
 * @property previousResponseId Optional ID of the previous response.
 * @property reasoning Optional reasoning configuration.
 * @property maxOutputTokens Optional maximum number of tokens to generate.
 * @property instructions Optional instructions for the model.
 * @property text Optional text input.
 * @property tools Optional tools the model may call.
 * @property toolChoice Optional control for which function is called.
 * @property truncation Optional truncation strategy.
 * @property include Optional additional output data to include.
 * @property parallelToolCalls Whether to allow parallel tool calls.
 * @property store Whether to store the response.
 * @property stream Whether to stream the response.
 * @author Konstantin Pavlov
 */
@Serializable
public data class CreateResponseRequest(
    @SerialName(value = "model") @Required val model: String,
    @SerialName(value = "input") @Contextual val input: Input? = null,
    @SerialName(value = "metadata") val metadata: Map<String, String>? = null,
    @SerialName(value = "temperature") val temperature: Double? = 1.0,
    @SerialName(value = "top_p") val topP: Double? = 1.0,
    @SerialName(value = "user") val user: String? = null,
    @SerialName(value = "previous_response_id") val previousResponseId: String? = null,
    @SerialName(value = "reasoning") val reasoning: Reasoning? = null,
    @SerialName(value = "max_output_tokens") val maxOutputTokens: Int? = null,
    @SerialName(value = "instructions") val instructions: String? = null,
    @SerialName(value = "text") @Contextual val text: Map<String, String>? = null,
    @SerialName(value = "tools") val tools: List<Tool>? = null,
    @SerialName(value = "tool_choice") @Contextual val toolChoice: String? = null,
    @SerialName(value = "truncation") val truncation: Truncation? = Truncation.DISABLED,
    @SerialName(value = "include") val include: List<String>? = null,
    @SerialName(value = "parallel_tool_calls") val parallelToolCalls: Boolean? = true,
    @SerialName(value = "store") val store: Boolean? = true,
    @SerialName(value = "stream") val stream: Boolean? = false,
)

@Serializable(InputSerializer::class)
public sealed interface Input

@Serializable(StringAsTextSerializer::class)
public data class Text(
    val text: String,
) : Input

@Serializable(ArrayAsInputItemsSerializer::class)
public data class InputItems(
    val items: List<InputMessageResource>,
) : Input

internal object InputSerializer : JsonContentPolymorphicSerializer<Input>(Input::class) {
    override fun selectDeserializer(element: JsonElement) =
        when {
            element is JsonPrimitive && element.isString
            -> Text.serializer()

            else -> InputItems.serializer()
        }
}

internal object StringAsTextSerializer : KSerializer<Text> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Text", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: Text,
    ) {
        encoder.encodeString(value.text)
    }

    override fun deserialize(decoder: Decoder): Text = Text(decoder.decodeString())
}

internal object ArrayAsInputItemsSerializer : KSerializer<InputItems> {
    override val descriptor: SerialDescriptor =
        listSerialDescriptor(InputMessageResource.serializer().descriptor)

    override fun serialize(
        encoder: Encoder,
        value: InputItems,
    ) {
        TODO("Unsupported")
    }

    override fun deserialize(decoder: Decoder): InputItems {
        val array =
            ArraySerializer(InputMessageResource.serializer())
                .deserialize(decoder)

        return InputItems(
            items = array.asList(),
        )
    }
}

/**
 * Represents a response from the OpenAI API.
 *
 * @property metadata Metadata for the response.
 * @property temperature The temperature used for generating the response.
 * @property topP The top_p value used for generating the response.
 * @property model The model used for generating the response.
 * @property instructions The instructions provided to the model.
 * @property tools The tools available to the model.
 * @property toolChoice The tool choice configuration.
 * @property id The unique identifier for the response.
 * @property objectType The type of the object, always "response".
 * @property createdAt The timestamp when the response was created.
 * @property output The output content generated by the model.
 * @property parallelToolCalls Whether parallel tool calls were allowed.
 * @property user Optional user identifier.
 * @property previousResponseId Optional ID of the previous response.
 * @property reasoning Optional reasoning information.
 * @property maxOutputTokens Optional maximum number of tokens generated.
 * @property truncation Optional truncation strategy used.
 * @property status The status of the response generation.
 * @property outputText Optional convenience property with aggregated text output.
 * @author Konstantin Pavlov
 */
@Serializable
public data class Response
    @OptIn(ExperimentalSerializationApi::class)
    constructor(
        @SerialName(value = "metadata") @Required val metadata: Map<String, String>?,
        @SerialName(value = "temperature") @Required val temperature: Double? = 1.0,
        @SerialName(value = "top_p") @Required val topP: Double? = 1.0,
        @SerialName(value = "model") @Required val model: String,
        @SerialName(value = "instructions") @Required val instructions: String?,
        @SerialName(value = "tools") @Required val tools: List<Tool> = emptyList(),
        @SerialName(value = "tool_choice")
        @Required val toolChoice: String = "auto",
        @SerialName(value = "id") @Required val id: String,
        @EncodeDefault(ALWAYS)
        @SerialName("object")
        @Required
        val objectType: String = "response",
        @SerialName(value = "created_at") @Required val createdAt: Long,
        @SerialName(value = "output") @Required val output: List<OutputMessage>,
        @SerialName(value = "parallel_tool_calls") @Required val parallelToolCalls: Boolean = true,
        @SerialName(value = "user") val user: String? = null,
        @SerialName(value = "previous_response_id") val previousResponseId: String? = null,
        @SerialName(value = "reasoning") val reasoning: Reasoning? = null,
        @SerialName(value = "max_output_tokens") val maxOutputTokens: Int? = null,
        @SerialName(value = "truncation") val truncation: Truncation? = Truncation.DISABLED,
        @SerialName(value = "status") val status: Status? = null,
        @SerialName(value = "output_text") val outputText: String? = null,
        @SerialName(value = "incomplete_details") val incompleteDetails: IncompleteDetails? = null,
        @SerialName(value = "usage") val usage: Usage,
        @EncodeDefault(ALWAYS)
        @SerialName(value = "error") val error: ResponseError? = null,
    ) {
        /**
         * The status of the response generation.
         *
         * @author Konstantin Pavlov
         */
        @Serializable
        public enum class Status(
            public val value: String,
        ) {
            @SerialName(value = "completed")
            COMPLETED("completed"),

            @SerialName(value = "failed")
            FAILED("failed"),

            @SerialName(value = "in_progress")
            IN_PROGRESS("in_progress"),

            @SerialName(value = "incomplete")
            INCOMPLETE("incomplete"),
        }
    }

/**
 * Represents token usage details including input tokens, output tokens,
 * a breakdown of output tokens, and the total tokens used.
 *
 * @param inputTokens The number of input tokens.
 * @param inputTokensDetails
 * @param outputTokens The number of output tokens.
 * @param outputTokensDetails
 * @param totalTokens The total number of tokens used.
 */
@Serializable
public data class Usage(
    // The number of input tokens.
    @SerialName(value = "input_tokens") @Required val inputTokens: Int,
    @SerialName(value = "input_tokens_details") @Required val inputTokensDetails: InputTokensDetails,
    // The number of output tokens.
    @SerialName(value = "output_tokens") @Required val outputTokens: Int,
    @SerialName(value = "output_tokens_details") @Required val outputTokensDetails: OutputTokensDetails,
    // The total number of tokens used.
    @SerialName(value = "total_tokens") @Required val totalTokens: Int,
)

@Serializable
public data class InputTokensDetails(
    @SerialName(value = "cached_tokens") @Required val cachedTokens: Int,
)

@Serializable
public data class OutputTokensDetails(
    @SerialName(value = "reasoning_tokens") @Required val reasoningTokens: Int,
)

/**
 * Details about why the response is incomplete.
 */
@Serializable
public data class IncompleteDetails(
    val reason: String,
)

/**
 * The truncation strategy to use for the model response.
 *
 * @author Konstantin Pavlov
 */
@Serializable
public enum class Truncation(
    public val value: String,
) {
    @SerialName(value = "auto")
    AUTO("auto"),

    @SerialName(value = "disabled")
    DISABLED("disabled"),
}
