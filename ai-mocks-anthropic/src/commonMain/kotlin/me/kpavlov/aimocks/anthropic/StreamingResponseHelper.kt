package me.kpavlov.aimocks.anthropic

import io.ktor.sse.TypedServerSentEvent
import me.kpavlov.aimocks.anthropic.model.AnthropicSseData
import me.kpavlov.aimocks.anthropic.model.AnthropicSseData.ContentBlock
import me.kpavlov.aimocks.anthropic.model.AnthropicSseData.ContentDelta
import me.kpavlov.aimocks.anthropic.model.AnthropicSseData.Message
import me.kpavlov.aimocks.anthropic.model.AnthropicSseData.MessageDelta
import me.kpavlov.aimocks.anthropic.model.AnthropicSseData.Usage

@Suppress("MagicNumber")
internal object StreamingResponseHelper {
    internal fun createMessageStartChunk(
        id: String,
        model: String,
    ): TypedServerSentEvent<AnthropicSseData> {
        val data = AnthropicSseData.MessageStartData(
            message = Message(
                id = id,
                type = "message",
                role = "assistant",
                model = model,
                content = emptyList(),
                usage = Usage(
                    inputTokens = 25,
                    outputTokens = 1
                )
            )
        )
        return TypedServerSentEvent(
            event = "message_start",
            data = data,
        )
    }

    internal fun createContentBlockStartChunk(
        index: Long = 0,
    ): TypedServerSentEvent<AnthropicSseData> {
        val data = AnthropicSseData.ContentBlockStartData(
            index = index.toInt(),
            contentBlock = ContentBlock.Text(
                text = ""
            )
        )
        return TypedServerSentEvent(
            event = "content_block_start",
            data = data,
        )
    }

    internal fun createTextDeltaChunk(
        index: Long = 0,
        content: String,
    ): TypedServerSentEvent<AnthropicSseData> {
        val data = AnthropicSseData.ContentBlockDeltaData(
            index = index.toInt(),
            delta = ContentDelta.TextDelta(
                text = content
            )
        )
        return TypedServerSentEvent(
            event = "content_block_delta",
            data = data,
        )
    }

    internal fun createMessageDeltaChunk(
        stopReason: String,
        outputTokens: Long,
    ): TypedServerSentEvent<AnthropicSseData> {
        val data = AnthropicSseData.MessageDeltaData(
            delta = MessageDelta(
                stopReason = stopReason,
                stopSequence = null
            ),
            usage = Usage(
                outputTokens = outputTokens.toInt()
            )
        )
        return TypedServerSentEvent(
            event = "message_delta",
            data = data,
        )
    }

    internal fun createContentBlockStopChunk(
        index: Long = 0,
    ): TypedServerSentEvent<AnthropicSseData> {
        val data = AnthropicSseData.ContentBlockStopData(
            index = index.toInt()
        )
        return TypedServerSentEvent(
            event = "content_block_stop",
            data = data,
        )
    }

    internal fun createMessageStopChunk(): TypedServerSentEvent<AnthropicSseData> {
        return TypedServerSentEvent(
            event = "message_stop",
            data = AnthropicSseData.MessageStopData,
        )
    }

    internal fun createPingEvent(): TypedServerSentEvent<AnthropicSseData> =
        TypedServerSentEvent(
            event = "ping",
            data = AnthropicSseData.PingData,
        )

    internal fun randomIdString(
        prefix: String = "",
        length: Int = 24,
    ): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return prefix +
            (1..length)
                .map { chars.random() }
                .joinToString("")
    }
}
