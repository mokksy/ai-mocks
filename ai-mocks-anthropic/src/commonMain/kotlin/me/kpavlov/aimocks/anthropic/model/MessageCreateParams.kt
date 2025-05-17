@file:OptIn(ExperimentalSerializationApi::class)

package me.kpavlov.aimocks.anthropic.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Parameters for creating a message with Anthropic's Claude models.
 * Based on the structure of com.anthropic.models.messages.MessageCreateParams
 */
@Serializable
public data class MessageCreateParams(
    /**
     * The model that will complete your prompt.
     * See the [models page](https://docs.anthropic.com/en/api/models) for a list of available models.
     */
    public val model: String,
    /**
     * Input messages.
     */
    public val messages: List<Message>,
    /**
     * System prompt.
     * A system prompt is a way of providing context and instructions to Claude,
     * such as specifying a particular goal or role.
     */
    @Serializable(with = SystemPromptsSerializer::class)
    public val system: List<SystemPrompt>? = null,
    /**
     * The maximum number of tokens to generate before stopping.
     */
    @SerialName("max_tokens")
    public val maxTokens: Int? = null,
    /**
     * Amount of randomness injected into the response.
     * Defaults to 1.0. Ranges from 0.0 to 1.0.
     * Use temperature closer to 0.0 for analytical / multiple choice,
     * and closer to 1.0 for creative and generative tasks.
     */
    public val temperature: Double? = null,
    /**
     * Use nucleus sampling.
     * In nucleus sampling, we compute the cumulative distribution over all the options
     * for each subsequent token in decreasing probability order
     * and cut it off once it reaches a particular probability specified by top_p.
     * Defaults to -1.0, which disables it.
     */
    @SerialName("top_p")
    public val topP: Double? = null,
    /**
     * Only sample from the top K options for each subsequent token.
     * Used to remove "long tail" low probability responses.
     * Defaults to -1, which disables it.
     */
    @SerialName("top_k")
    public val topK: Int? = null,
    /**
     * Whether to incrementally stream the response using server-sent events.
     */
    public val stream: Boolean? = null,
    /**
     * An object describing metadata about the request.
     */
    public val metadata: Metadata? = null,

    public val tools: List<Map<String, String>>? = null, //todo: use proper models
) {

    /**
     * System prompt for Claude.
     * A system prompt is a way of providing context and instructions to Claude,
     * such as specifying a particular goal or role.
     */
    @Serializable
    public data class SystemPrompt(
        /**
         * The text content of the system prompt.
         */
        val text: String,

        /**
         * The type of the system prompt. Always "text".
         */
        @EncodeDefault
        val type: String = "text",

        /**
         * Create a cache control breakpoint at this content block.
         */
        @SerialName("cache_control")
        val cacheControl: CacheControl? = null,

        /**
         * Citations for the system prompt.
         */
        val citations: List<Citation>? = null
    ) {
        /**
         * Cache control configuration.
         */
        @Serializable
        public data class CacheControl(
            /**
             * The type of cache control. Always "ephemeral".
             */
            @EncodeDefault
            val type: String = "ephemeral"
        )

        /**
         * Citation for referenced content in the system prompt.
         */
        @Serializable
        public data class Citation(
            /**
             * The text being cited.
             */
            @SerialName("cited_text")
            val citedText: String,

            /**
             * Index of the document being cited.
             */
            @SerialName("document_index")
            val documentIndex: Int,

            /**
             * Optional title of the document being cited.
             */
            @SerialName("document_title")
            val documentTitle: String? = null,

            /**
             * End character index in the original text.
             */
            @SerialName("end_char_index")
            val endCharIndex: Int,

            /**
             * Start character index in the original text.
             */
            @SerialName("start_char_index")
            val startCharIndex: Int,

            /**
             * Type of citation. Always "char_location".
             */
            val type: String = "char_location"
        )
    }


    /**
     * Message object for Claude API.
     */
    @Serializable
    public data class Message(
        /**
         * The role of the message's author. Currently, only "user" and "assistant" are supported.
         */
        public val role: String,
        /**
         * The content of the message.
         * Can be a string for simple messages or a list of content blocks for multi-modal messages.
         */
        @Serializable(with = ContentSerializer::class)
        public val content: Content,
    )

    /**
     * Content of a message. Can be a simple string or a list of content blocks.
     */
    @Serializable
    public sealed interface Content

    /**
     * Text content for a message.
     */
    @Serializable
    @SerialName("text")
    public data class TextContent(
        public val text: String?,
    ) : Content

    /**
     * List of content blocks for multi-modal messages.
     */
    @Serializable
    public data class ContentList(
        public val blocks: List<ContentBlock>,
    ) : Content

    /**
     * A content block in a multi-modal message.
     */
    @Serializable
    public sealed interface ContentBlock {
        public val type: String
    }

    /**
     * Text content block.
     */
    @Serializable
    @SerialName("text")
    public data class TextBlock(
        public override val type: String = "text",
        public val text: String,
    ) : ContentBlock

    /**
     * Image content block.
     */
    @Serializable
    @SerialName("image")
    public data class ImageBlock(
        public override val type: String = "image",
        public val source: ImageSource,
    ) : ContentBlock

    /**
     * Source information for an image.
     */
    @Serializable
    public data class ImageSource(
        public val type: String,
        @SerialName("media_type")
        public val mediaType: String,
        public val data: String,
    )

    /**
     * Metadata about the request.
     */
    @Serializable
    public data class Metadata(
        /**
         * An external identifier for the user who is associated with the request.
         */
        @SerialName("user_id")
        public val userId: String? = null,
    )
}

