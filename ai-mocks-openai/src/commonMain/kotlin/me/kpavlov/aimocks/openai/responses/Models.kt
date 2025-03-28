package me.kpavlov.aimocks.openai.responses

// import me.kpavlov.aimocks.openai.model.CreateResponseAllOfInput
// import me.kpavlov.aimocks.openai.model.Includable
// import me.kpavlov.aimocks.openai.model.ModelIdsResponses
// import me.kpavlov.aimocks.openai.model.Reasoning
// import me.kpavlov.aimocks.openai.model.ResponsePropertiesText
// import me.kpavlov.aimocks.openai.model.ResponsePropertiesToolChoice
// import me.kpavlov.aimocks.openai.model.Tool

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode.ALWAYS
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.kpavlov.aimocks.openai.Tool
import me.kpavlov.aimocks.openai.model.OutputMessage
import me.kpavlov.aimocks.openai.model.Reasoning

/**
 *
 *
 * @param model
 * @param input
 * @param metadata Set of 16 key-value pairs that can be attached to an object. This can be useful for storing additional information about the object in a structured format, and querying for objects via API or the dashboard.   Keys are strings with a maximum length of 64 characters. Values are strings with a maximum length of 512 characters.
 * @param temperature What sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random, while lower values like 0.2 will make it more focused and deterministic. We generally recommend altering this or `top_p` but not both.
 * @param topP An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are considered.  We generally recommend altering this or `temperature` but not both.
 * @param user A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse. [Learn more](/docs/guides/safety-best-practices#end-user-ids).
 * @param previousResponseId The unique ID of the previous response to the model. Use this to create multi-turn conversations. Learn more about  [conversation state](/docs/guides/conversation-state).
 * @param reasoning
 * @param maxOutputTokens An upper bound for the number of tokens that can be generated for a response, including visible output tokens and [reasoning tokens](/docs/guides/reasoning).
 * @param instructions Inserts a system (or developer) message as the first item in the model's context.  When using along with `previous_response_id`, the instructions from a previous response will be not be carried over to the next response. This makes it simple to swap out system (or developer) messages in new responses.
 * @param text
 * @param tools An array of tools the model may call while generating a response. You  can specify which tool to use by setting the `tool_choice` parameter.  The two categories of tools you can provide the model are:  - **Built-in tools**: Tools that are provided by OpenAI that extend the   model's capabilities, like [web search](/docs/guides/tools-web-search)   or [file search](/docs/guides/tools-file-search). Learn more about   [built-in tools](/docs/guides/tools). - **Function calls (custom tools)**: Functions that are defined by you,   enabling the model to call your own code. Learn more about   [function calling](/docs/guides/function-calling).
 * @param toolChoice
 * @param truncation The truncation strategy to use for the model response. - `auto`: If the context of this response and previous ones exceeds   the model's context window size, the model will truncate the    response to fit the context window by dropping input items in the   middle of the conversation.  - `disabled` (default): If a model response will exceed the context window    size for a model, the request will fail with a 400 error.
 * @param include Specify additional output data to include in the model response. Currently supported values are: - `file_search_call.results`: Include the search results of   the file search tool call. - `message.input_image.image_url`: Include image urls from the input message. - `computer_call_output.output.image_url`: Include image urls from the computer call output.
 * @param parallelToolCalls Whether to allow the model to run tool calls in parallel.
 * @param store Whether to store the generated model response for later retrieval via API.
 * @param stream If set to true, the model response data will be streamed to the client as it is generated using [server-sent events](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#Event_stream_format). See the [Streaming section below](/docs/api-reference/responses-streaming) for more information.
 */
@Serializable
public data class CreateResponseRequest(
    @SerialName(value = "model") @Required val model: String,
//    @SerialName(value = "input") @Required val input: CreateResponseAllOfInput,
    // Set of 16 key-value pairs that can be attached to an object. This can be useful for storing additional information about the object in a structured format, and querying for objects via API or the dashboard.   Keys are strings with a maximum length of 64 characters. Values are strings with a maximum length of 512 characters.
    @SerialName(value = "metadata") val metadata: Map<String, String>? = null,
    // What sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random, while lower values like 0.2 will make it more focused and deterministic. We generally recommend altering this or `top_p` but not both.
    @SerialName(value = "temperature") val temperature: Double? = (1).toDouble(),
    // An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are considered.  We generally recommend altering this or `temperature` but not both.
    @SerialName(value = "top_p") val topP: Double? = (1).toDouble(),
    // A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse. [Learn more](/docs/guides/safety-best-practices#end-user-ids).
    @SerialName(value = "user") val user: String? = null,
    // The unique ID of the previous response to the model. Use this to create multi-turn conversations. Learn more about  [conversation state](/docs/guides/conversation-state).
    @SerialName(value = "previous_response_id") val previousResponseId: String? = null,
//    @SerialName(value = "reasoning") val reasoning: Reasoning? = null,
    // An upper bound for the number of tokens that can be generated for a response, including visible output tokens and [reasoning tokens](/docs/guides/reasoning).
    @SerialName(value = "max_output_tokens") val maxOutputTokens: Int? = null,
    // Inserts a system (or developer) message as the first item in the model's context.
    // When using along with `previous_response_id`, the instructions from a previous response will be not be carried over to the next response. This makes it simple to swap out system (or developer) messages in new responses.
    @SerialName(value = "instructions") val instructions: String? = null,
//    @SerialName(value = "text") val text: ResponsePropertiesText? = null,
    // An array of tools the model may call while generating a response. You  can specify which tool to use by setting the `tool_choice` parameter.  The two categories of tools you can provide the model are:  - **Built-in tools**: Tools that are provided by OpenAI that extend the   model's capabilities, like [web search](/docs/guides/tools-web-search)   or [file search](/docs/guides/tools-file-search). Learn more about   [built-in tools](/docs/guides/tools). - **Function calls (custom tools)**: Functions that are defined by you,   enabling the model to call your own code. Learn more about   [function calling](/docs/guides/function-calling).
//    @SerialName(value = "tools") val tools: List<Tool>? = null,
//    @SerialName(value = "tool_choice") val toolChoice: ResponsePropertiesToolChoice? = null,
    // The truncation strategy to use for the model response. - `auto`: If the context of this response and previous ones exceeds   the model's context window size, the model will truncate the    response to fit the context window by dropping input items in the   middle of the conversation.  - `disabled` (default): If a model response will exceed the context window    size for a model, the request will fail with a 400 error.
    @SerialName(
        value = "truncation",
    ) val truncation: Truncation? = Truncation.DISABLED,
    // Specify additional output data to include in the model response. Currently supported values are: - `file_search_call.results`: Include the search results of   the file search tool call. - `message.input_image.image_url`: Include image urls from the input message. - `computer_call_output.output.image_url`: Include image urls from the computer call output.
//    @SerialName(value = "include") val include: List<Includable>? = null,
    // Whether to allow the model to run tool calls in parallel.
    @SerialName(value = "parallel_tool_calls") val parallelToolCalls: Boolean? = true,
    // Whether to store the generated model response for later retrieval via API.
    @SerialName(value = "store") val store: Boolean? = true,
    // If set to true, the model response data will be streamed to the client as it is generated using [server-sent events](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#Event_stream_format). See the [Streaming section below](/docs/api-reference/responses-streaming) for more information.
    @SerialName(value = "stream") val stream: Boolean? = false,
) {
    /**
     * The truncation strategy to use for the model response. - `auto`: If the context of this response and previous ones exceeds   the model's context window size, the model will truncate the    response to fit the context window by dropping input items in the   middle of the conversation.  - `disabled` (default): If a model response will exceed the context window    size for a model, the request will fail with a 400 error.
     *
     * Values: AUTO,DISABLED
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
}

/**
 *
 *
 * @param metadata Set of 16 key-value pairs that can be attached to an object. This can be useful for storing additional information about the object in a structured format, and querying for objects via API or the dashboard.   Keys are strings with a maximum length of 64 characters. Values are strings with a maximum length of 512 characters.
 * @param temperature What sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random, while lower values like 0.2 will make it more focused and deterministic. We generally recommend altering this or `top_p` but not both.
 * @param topP An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are considered.  We generally recommend altering this or `temperature` but not both.
 * @param model
 * @param instructions Inserts a system (or developer) message as the first item in the model's context.  When using along with `previous_response_id`, the instructions from a previous response will be not be carried over to the next response. This makes it simple to swap out system (or developer) messages in new responses.
 * @param tools An array of tools the model may call while generating a response. You  can specify which tool to use by setting the `tool_choice` parameter.  The two categories of tools you can provide the model are:  - **Built-in tools**: Tools that are provided by OpenAI that extend the   model's capabilities, like [web search](/docs/guides/tools-web-search)   or [file search](/docs/guides/tools-file-search). Learn more about   [built-in tools](/docs/guides/tools). - **Function calls (custom tools)**: Functions that are defined by you,   enabling the model to call your own code. Learn more about   [function calling](/docs/guides/function-calling).
 * @param toolChoice
 * @param id Unique identifier for this Response.
 * @param `object` The object type of this resource - always set to `response`.
 * @param createdAt Unix timestamp (in seconds) of when this Response was created.
 * @param error
 * @param incompleteDetails
 * @param output An array of content items generated by the model.  - The length and order of items in the `output` array is dependent   on the model's response. - Rather than accessing the first item in the `output` array and    assuming it's an `assistant` message with the content generated by   the model, you might consider using the `output_text` property where   supported in SDKs.
 * @param parallelToolCalls Whether to allow the model to run tool calls in parallel.
 * @param user A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse. [Learn more](/docs/guides/safety-best-practices#end-user-ids).
 * @param previousResponseId The unique ID of the previous response to the model. Use this to create multi-turn conversations. Learn more about  [conversation state](/docs/guides/conversation-state).
 * @param reasoning
 * @param maxOutputTokens An upper bound for the number of tokens that can be generated for a response, including visible output tokens and [reasoning tokens](/docs/guides/reasoning).
 * @param text
 * @param truncation The truncation strategy to use for the model response. - `auto`: If the context of this response and previous ones exceeds   the model's context window size, the model will truncate the    response to fit the context window by dropping input items in the   middle of the conversation.  - `disabled` (default): If a model response will exceed the context window    size for a model, the request will fail with a 400 error.
 * @param status The status of the response generation. One of `completed`, `failed`,  `in_progress`, or `incomplete`.
 * @param outputText SDK-only convenience property that contains the aggregated text output  from all `output_text` items in the `output` array, if any are present.  Supported in the Python and JavaScript SDKs.
 * @param usage
 */
@Serializable
public data class Response
    @OptIn(ExperimentalSerializationApi::class, ExperimentalSerializationApi::class)
    constructor(
        // Set of 16 key-value pairs that can be attached to an object. This can be useful for storing additional information about the object in a structured format, and querying for objects via API or the dashboard.   Keys are strings with a maximum length of 64 characters. Values are strings with a maximum length of 512 characters.
        @SerialName(value = "metadata") @Required val metadata: Map<String, String>?,
        // What sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random, while lower values like 0.2 will make it more focused and deterministic. We generally recommend altering this or `top_p` but not both.
        @SerialName(value = "temperature") @Required val temperature: Double? = (1).toDouble(),
        // An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are considered.  We generally recommend altering this or `temperature` but not both.
        @SerialName(value = "top_p") @Required val topP: Double? = (1).toDouble(),
        @SerialName(value = "model") @Required val model: String,
        // Inserts a system (or developer) message as the first item in the model's context.  When using along with `previous_response_id`, the instructions from a previous response will be not be carried over to the next response. This makes it simple to swap out system (or developer) messages in new responses.
        @SerialName(value = "instructions") @Required val instructions: String?,
        // An array of tools the model may call while generating a response. You  can specify which tool to use by setting the `tool_choice` parameter.  The two categories of tools you can provide the model are:  - **Built-in tools**: Tools that are provided by OpenAI that extend the   model's capabilities, like [web search](/docs/guides/tools-web-search)   or [file search](/docs/guides/tools-file-search). Learn more about   [built-in tools](/docs/guides/tools). - **Function calls (custom tools)**: Functions that are defined by you,   enabling the model to call your own code. Learn more about   [function calling](/docs/guides/function-calling).
        @SerialName(value = "tools") @Required val tools: List<Tool> = emptyList(),
        @SerialName(value = "tool_choice") @Required val toolChoice: Map<String, String> =
            emptyMap(),
        // Unique identifier for this Response.
        @SerialName(value = "id") @Required val id: String,
        // The object type of this resource - always set to `response`.
        @EncodeDefault(ALWAYS)
        @SerialName("object")
        @Required
        val objectType: String = "response",
        // Unix timestamp (in seconds) of when this Response was created.
        @SerialName(value = "created_at") @Required val createdAt: Long,
//    @SerialName(value = "error") @Required val error: ResponseError?,
//    @SerialName(value = "incomplete_details") @Required val incompleteDetails:
//        ResponseAllOfIncompleteDetails?,
        // An array of content items generated by the model.  - The length and order of items in the `output` array is dependent   on the model's response. - Rather than accessing the first item in the `output` array and    assuming it's an `assistant` message with the content generated by   the model, you might consider using the `output_text` property where   supported in SDKs.
        @SerialName(value = "output") @Required val output: List<OutputMessage>,
        // Whether to allow the model to run tool calls in parallel.
        @SerialName(
            value = "parallel_tool_calls",
        ) @Required val parallelToolCalls: Boolean = true,
        // A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse. [Learn more](/docs/guides/safety-best-practices#end-user-ids).
        @SerialName(value = "user") val user: String? = null,
        // The unique ID of the previous response to the model. Use this to create multi-turn conversations. Learn more about  [conversation state](/docs/guides/conversation-state).
        @SerialName(value = "previous_response_id") val previousResponseId: String? = null,
        @SerialName(value = "reasoning") val reasoning: Reasoning? = null,
        // An upper bound for the number of tokens that can be generated for a response, including visible output tokens and [reasoning tokens](/docs/guides/reasoning).
        @SerialName(value = "max_output_tokens") val maxOutputTokens: Int? = null,
//    @SerialName(value = "text") val text: ResponsePropertiesText? = null,
        // The truncation strategy to use for the model response. - `auto`: If the context of this response and previous ones exceeds   the model's context window size, the model will truncate the    response to fit the context window by dropping input items in the   middle of the conversation.  - `disabled` (default): If a model response will exceed the context window    size for a model, the request will fail with a 400 error.
        @SerialName(value = "truncation") val truncation: Truncation? = Truncation.DISABLED,
        // The status of the response generation. One of `completed`, `failed`,  `in_progress`, or `incomplete`.
        @SerialName(value = "status") val status: Status? = null,
        // SDK-only convenience property that contains the aggregated text output  from all `output_text` items in the `output` array, if any are present.  Supported in the Python and JavaScript SDKs.
        @SerialName(value = "output_text") val outputText: String? = null,
//    @SerialName(value = "usage") val usage: ResponseUsage? = null,
    ) {
        /**
         * The status of the response generation. One of `completed`, `failed`,  `in_progress`, or `incomplete`.
         *
         * Values: COMPLETED,FAILED,IN_PROGRESS,INCOMPLETE
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
 * The truncation strategy to use for the model response. - `auto`: If the context of this response and previous ones exceeds   the model's context window size, the model will truncate the    response to fit the context window by dropping input items in the   middle of the conversation.  - `disabled` (default): If a model response will exceed the context window    size for a model, the request will fail with a 400 error.
 *
 * Values: AUTO,DISABLED
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
