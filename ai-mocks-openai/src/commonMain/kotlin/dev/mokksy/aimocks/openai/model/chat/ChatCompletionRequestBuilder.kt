package dev.mokksy.aimocks.openai.model.chat

import dev.mokksy.aimocks.openai.ChatCompletionRequest
import dev.mokksy.aimocks.openai.Message
import dev.mokksy.aimocks.openai.ResponseFormat
import dev.mokksy.aimocks.openai.Tool
import dev.mokksy.aimocks.openai.model.ChatCompletionRole
import dev.mokksy.aimocks.openai.model.ChatCompletionStreamOptions

/**
 * Builder class for creating [ChatCompletionRequest] instances.
 *
 * This builder provides a fluent API for creating chat completion requests,
 * making it easier to construct complex requests with many parameters.
 *
 * Example usage:
 * ```kotlin
 * val request = ChatCompletionRequestBuilder()
 *     .model("gpt-4")
 *     .addSystemMessage("You are a helpful assistant.")
 *     .addUserMessage("Tell me about Kotlin.")
 *     .temperature(0.7)
 *     .build()
 * ```
 * @author Konstantin Pavlov
 */
@Suppress("TooManyFunctions")
public class ChatCompletionRequestBuilder {
    private val messages = mutableListOf<Message>()
    private var model: String? = null
    private var store: Boolean = false
    private var reasoningEffort: String = "medium"
    private var metadata: Map<String, String>? = null
    private var maxCompletionTokens: Int? = null
    private var frequencyPenalty: Double? = 0.0
    private var responseFormat: ResponseFormat? = null
    private var temperature: Double = 1.0
    private var seed: Int? = null
    private var stream: Boolean = false
    private var streamOptions: ChatCompletionStreamOptions? = null
    private var tools: List<Tool>? = null

    /**
     * Sets the model to use for the completion.
     *
     * @param model The identifier of the language model to be used.
     * @return This builder instance for method chaining.
     */
    public fun model(model: String): ChatCompletionRequestBuilder {
        this.model = model
        return this
    }

    /**
     * Adds a message to the conversation.
     *
     * @param role The role of the message sender.
     * @param content The content of the message.
     * @return This builder instance for method chaining.
     */
    public fun addMessage(
        role: ChatCompletionRole,
        content: String,
    ): ChatCompletionRequestBuilder {
        messages.add(Message(role, MessageContent.Text(content)))
        return this
    }

    /**
     * Adds a message with structured content parts to the conversation.
     *
     * @param role The role of the message sender.
     * @param content The structured message content.
     * @return This builder instance for method chaining.
     */
    public fun addMessage(
        role: ChatCompletionRole,
        content: MessageContent,
    ): ChatCompletionRequestBuilder {
        messages.add(Message(role, content))
        return this
    }

    /**
     * Adds a system message to the conversation.
     *
     * @param content The content of the system message.
     * @return This builder instance for method chaining.
     */
    public fun addSystemMessage(content: String): ChatCompletionRequestBuilder =
        addMessage(ChatCompletionRole.SYSTEM, content)

    /**
     * Adds a user message to the conversation.
     *
     * @param content The content of the user message.
     * @return This builder instance for method chaining.
     */
    public fun addUserMessage(content: String): ChatCompletionRequestBuilder =
        addMessage(ChatCompletionRole.USER, content)

    /**
     * Adds an assistant message to the conversation.
     *
     * @param content The content of the assistant message.
     * @return This builder instance for method chaining.
     */
    public fun addAssistantMessage(content: String): ChatCompletionRequestBuilder =
        addMessage(ChatCompletionRole.ASSISTANT, content)

    /**
     * Sets whether to store the conversation context.
     *
     * @param store Whether to store the conversation context.
     * @return This builder instance for method chaining.
     */
    public fun store(store: Boolean): ChatCompletionRequestBuilder {
        this.store = store
        return this
    }

    /**
     * Sets the reasoning effort to apply during reasoning.
     *
     * @param reasoningEffort The level of computational effort to apply ("low", "medium", "high").
     * @return This builder instance for method chaining.
     */
    public fun reasoningEffort(reasoningEffort: String): ChatCompletionRequestBuilder {
        this.reasoningEffort = reasoningEffort
        return this
    }

    /**
     * Sets the metadata for the request.
     *
     * @param metadata The metadata to associate with the request.
     * @return This builder instance for method chaining.
     */
    public fun metadata(metadata: Map<String, String>): ChatCompletionRequestBuilder {
        this.metadata = metadata
        return this
    }

    /**
     * Sets the maximum number of tokens allowed in the generated completions.
     *
     * @param maxCompletionTokens The maximum number of tokens.
     * @return This builder instance for method chaining.
     */
    public fun maxCompletionTokens(maxCompletionTokens: Int): ChatCompletionRequestBuilder {
        this.maxCompletionTokens = maxCompletionTokens
        return this
    }

    /**
     * Sets the penalty value for repetitive token usage.
     *
     * @param frequencyPenalty The penalty value.
     * @return This builder instance for method chaining.
     */
    public fun frequencyPenalty(frequencyPenalty: Double): ChatCompletionRequestBuilder {
        this.frequencyPenalty = frequencyPenalty
        return this
    }

    /**
     * Sets the response format.
     *
     * @param responseFormat The response format.
     * @return This builder instance for method chaining.
     */
    public fun responseFormat(responseFormat: ResponseFormat): ChatCompletionRequestBuilder {
        this.responseFormat = responseFormat
        return this
    }

    /**
     * Sets the temperature for controlling randomness.
     *
     * @param temperature A value between 0.0 and 1.0.
     * @return This builder instance for method chaining.
     */
    public fun temperature(temperature: Double): ChatCompletionRequestBuilder {
        this.temperature = temperature
        return this
    }

    /**
     * Sets the seed for deterministic responses.
     *
     * @param seed The seed value.
     * @return This builder instance for method chaining.
     */
    public fun seed(seed: Int): ChatCompletionRequestBuilder {
        this.seed = seed
        return this
    }

    /**
     * Sets whether to stream the response.
     *
     * @param stream Whether to stream the response.
     * @return This builder instance for method chaining.
     */
    public fun stream(stream: Boolean): ChatCompletionRequestBuilder {
        this.stream = stream
        return this
    }

    /**
     * Sets the stream options.
     *
     * @param streamOptions The stream options.
     * @return This builder instance for method chaining.
     */
    public fun streamOptions(streamOptions: ChatCompletionStreamOptions): ChatCompletionRequestBuilder {
        this.streamOptions = streamOptions
        return this
    }

    /**
     * Sets the tools available to the model.
     *
     * @param tools The list of tools.
     * @return This builder instance for method chaining.
     */
    public fun tools(tools: List<Tool>): ChatCompletionRequestBuilder {
        this.tools = tools
        return this
    }

    /**
     * Builds a [ChatCompletionRequest] instance with the configured parameters.
     *
     * @return A new [ChatCompletionRequest] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): ChatCompletionRequest {
        requireNotNull(model) { "Model is required" }
        require(messages.isNotEmpty()) { "At least one message is required" }

        return ChatCompletionRequest(
            messages = messages.toList(),
            model = model!!,
            store = store,
            reasoningEffort = reasoningEffort,
            metadata =
                metadata?.let {
                    dev.mokksy.aimocks.openai
                        .Metadata(it)
                },
            maxCompletionTokens = maxCompletionTokens,
            frequencyPenalty = frequencyPenalty,
            responseFormat = responseFormat,
            temperature = temperature,
            seed = seed,
            stream = stream,
            streamOptions = streamOptions,
            tools = tools,
        )
    }
}
