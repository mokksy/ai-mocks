@file:OptIn(ExperimentalSerializationApi::class)

package dev.mokksy.aimocks.anthropic.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.json.JsonNames

@Serializable
@JvmRecord
public data class Message(
    val id: String,
    val content: List<ContentBlock>,
    val model: String,
    @EncodeDefault
    val role: String = "assistant",
    @SerialName("stop_reason")
    val stopReason: StopReason? = null,
    @SerialName("stop_sequence")
    val stopSequence: String? = null,
    @EncodeDefault
    val type: String = "message",
    val usage: Usage,
)

@Serializable
@JsonClassDiscriminator("type")
public sealed class ContentBlock

@Serializable
@SerialName("text")
public data class TextBlock(
    val text: String,
    val citations: List<Citation>? = null,
) : ContentBlock()

@Serializable
@SerialName("tool_use")
public data class ToolUseBlock(
    val id: String,
    val name: String,
    val input: Map<String, String>,
) : ContentBlock()

@Serializable
@SerialName("thinking")
public data class ThinkingBlock(
    val thinking: String,
) : ContentBlock()

@Serializable
@SerialName("redacted_thinking")
public data class RedactedThinkingBlock(
    val data: String,
) : ContentBlock()

@Serializable
@JvmRecord
public data class Citation(
    val text: String,
    val type: String,
    @SerialName("start_index")
    val startIndex: Int,
    @SerialName("end_index")
    val endIndex: Int,
    @SerialName("source_type")
    val sourceType: String? = null,
    val metadata: Map<String, String>? = null,
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
public enum class StopReason {
    @JsonNames("end_turn")
    @SerialName("end_turn")
    END_TURN,

    @JsonNames("max_tokens")
    @SerialName("max_tokens")
    MAX_TOKENS,

    @JsonNames("stop_sequence")
    @SerialName("stop_sequence")
    STOP_SEQUENCE,

    @JsonNames("tool_use")
    @SerialName("tool_use")
    TOOL_USE,

    ;

    override fun toString(): String =
        when (this) {
            END_TURN -> "end_turn"
            MAX_TOKENS -> "max_tokens"
            STOP_SEQUENCE -> "stop_sequence"
            TOOL_USE -> "tool_use"
        }
}

@Serializable
@JvmRecord
public data class Usage(
    @SerialName("input_tokens")
    val inputTokens: Long,
    @SerialName("output_tokens")
    val outputTokens: Long,
    @SerialName("cache_creation_input_tokens")
    val cacheCreationInputTokens: Long = 0,
    @SerialName("cache_read_input_tokens")
    val cacheReadInputTokens: Long = 0,
    @SerialName("server_tool_use")
    val serverToolUse: ServerToolUseUsage? = null,
)

@Serializable
@JvmRecord
public data class ServerToolUseUsage(
    @SerialName("input_tokens")
    val inputTokens: Long,
    @SerialName("output_tokens")
    val outputTokens: Long,
)
