package me.kpavlov.aimocks.anthropic.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * Base interface for all SSE data payloads
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
public sealed interface AnthropicSseData {
    public companion object {
        public val serializersModule: SerializersModule =
            SerializersModule {

                polymorphic(AnthropicSseData::class) {
                    subclass(MessageStartData::class)
                    subclass(ContentBlockStartData::class)
                    subclass(PingData::class)
                    subclass(ContentBlockDeltaData::class)
                    subclass(ContentBlockStopData::class)
                    subclass(MessageDeltaData::class)
                    subclass(MessageStopData::class)
                }
                polymorphic(ContentBlock::class) {
                    subclass(ContentBlock.Text::class)
                    subclass(ContentBlock.Image::class)
                }
                polymorphic(ContentDelta::class) {
                    subclass(ContentDelta.TextDelta::class)
                }
            }
    }

    /**
     * Data for message_start event
     */
    @Serializable
    @SerialName("message_start")
    public data class MessageStartData(
        val message: Message,
    ) : AnthropicSseData

    /**
     * Data for content_block_start event
     */
    @Serializable
    @SerialName("content_block_start")
    public data class ContentBlockStartData(
        val index: Int,
        @SerialName("content_block") val contentBlock: ContentBlock,
    ) : AnthropicSseData

    /**
     * Data for ping event
     */
    @Serializable
    @SerialName("ping")
    public object PingData :
        AnthropicSseData

    /**
     * Data for content_block_delta event
     */
    @Serializable
    @SerialName("content_block_delta")
    public data class ContentBlockDeltaData(
        val index: Int,
        val delta: ContentDelta,
    ) : AnthropicSseData

    /**
     * Data for content_block_stop event
     */
    @Serializable
    @SerialName("content_block_stop")
    public data class ContentBlockStopData(
        val index: Int,
    ) : AnthropicSseData

    /**
     * Data for message_delta event
     */
    @Serializable
    @SerialName("message_delta")
    public data class MessageDeltaData(
        val delta: MessageDelta,
        val usage: Usage,
    ) : AnthropicSseData

    /**
     * Data for message_stop event
     */
    @Serializable
    @SerialName("message_stop")
    public object MessageStopData : AnthropicSseData

    /**
     * Message object contained in message_start event
     */
    @Serializable
    public data class Message(
        val id: String,
        val type: String,
        val role: String,
        @EncodeDefault
        val content: List<ContentBlock> = emptyList(),
        val model: String,
        @EncodeDefault
        @SerialName("stop_reason") val stopReason: String? = null,
        @EncodeDefault
        @SerialName("stop_sequence") val stopSequence: String? = null,
        val usage: Usage,
    )

    /**
     * Usage statistics for token consumption
     */
    @Serializable
    public data class Usage(
        @SerialName("input_tokens") val inputTokens: Int? = null,
        @SerialName("output_tokens") val outputTokens: Int? = null,
    )

    /**
     * Base class for content blocks with polymorphic serialization
     */
    @Serializable
    @JsonClassDiscriminator("type")
    public sealed class ContentBlock {
        @Serializable
        @SerialName("text")
        public data class Text(
            val text: String,
        ) : ContentBlock()

        @Serializable
        @SerialName("image")
        public data class Image(
            val type: String = "image",
            val source: ImageSource,
        ) : ContentBlock()
    }

    /**
     * Image source for image content blocks
     */
    @Serializable
    public data class ImageSource(
        val type: String,
        @SerialName("media_type") val mediaType: String,
        val data: String,
    )

    /**
     * Delta information for content block updates
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonClassDiscriminator("type")
    public sealed class ContentDelta {
        @Serializable
        @SerialName("text_delta")
        public data class TextDelta(
            val text: String,
        ) : ContentDelta()
    }

    /**
     * Delta information for message updates
     */
    @Serializable
    public data class MessageDelta(
        @SerialName("stop_reason") val stopReason: String? = null,
        @EncodeDefault
        @SerialName("stop_sequence") val stopSequence: String? = null,
    )

    /**
     * System prompt for messages
     */
    @Serializable
    public data class SystemPrompt(
        val text: String,
        val type: String = "text",
    )
}
