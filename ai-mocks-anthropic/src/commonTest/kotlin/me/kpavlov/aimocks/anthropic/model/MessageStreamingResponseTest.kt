package me.kpavlov.aimocks.anthropic.model

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

internal class MessageStreamingResponseTest {
    val jsonParser =
        Json {
            ignoreUnknownKeys = true
        }

    @Test
    fun `Should deserialize text request`() {
        // language=json lines
        val sseData =
            """
            event: message_start
            data: {"type": "message_start", "message": {"id": "msg_1nZdL29xx5MUA1yADyHTEsnR8uuvGzszyY", "type": "message", "role": "assistant", "content": [], "model": "claude-3-7-sonnet-20250219", "stop_reason": null, "stop_sequence": null, "usage": {"input_tokens": 25, "output_tokens": 1}}}

            event: content_block_start
            data: {"type": "content_block_start", "index": 0, "content_block": {"type": "text", "text": ""}}

            event: ping
            data: {"type": "ping"}

            event: content_block_delta
            data: {"type": "content_block_delta", "index": 0, "delta": {"type": "text_delta", "text": "Hello"}}

            event: content_block_delta
            data: {"type": "content_block_delta", "index": 0, "delta": {"type": "text_delta", "text": "!"}}

            event: content_block_stop
            data: {"type": "content_block_stop", "index": 0}

            event: message_delta
            data: {"type": "message_delta", "delta": {"stop_reason": "end_turn", "stop_sequence":null}, "usage": {"output_tokens": 15}}

            event: message_stop
            data: {"type": "message_stop"}
            """.trimIndent()

        val result =
            sseData
                .split("\n\n")
                .filter { it.isNotBlank() }
                .mapNotNull {
                    val lines = it.split('\n')
                    if (lines.size == 2) {
                        val jsonData = lines[1].removePrefix("data: ")
                        jsonParser.decodeFromString<AnthropicSseData>(jsonData)
                    } else {
                        null
                    }
                }.toList()

        result.size shouldBe 8

        result[0] as AnthropicSseData.MessageStartData shouldNotBeNull {
            message.content.size shouldBe 0
            message.stopReason shouldBe null
            message.stopSequence shouldBe null
            message.usage.shouldNotBeNull {
                inputTokens shouldBe 25
                outputTokens shouldBe 1
            }
        }

        result[1] as AnthropicSseData.ContentBlockStartData shouldNotBeNull {
            index shouldBe 0
            contentBlock as AnthropicSseData.ContentBlock.Text shouldNotBeNull {
                this.text shouldBe ""
            }
        }

        result[2] as AnthropicSseData.PingData shouldNotBeNull {
        }

        result[3] as AnthropicSseData.ContentBlockDeltaData shouldNotBeNull {
            this.delta as AnthropicSseData.ContentDelta.TextDelta shouldNotBeNull {
                this.text shouldBe "Hello"
            }
        }

        result[4] as AnthropicSseData.ContentBlockDeltaData shouldNotBeNull {
            this.delta as AnthropicSseData.ContentDelta.TextDelta shouldNotBeNull {
                this.text shouldBe "!"
            }
        }
        result[5] as AnthropicSseData.ContentBlockStopData shouldNotBeNull {
            index shouldBe 0
        }
        result[6] as AnthropicSseData.MessageDeltaData shouldNotBeNull {
            delta as AnthropicSseData.MessageDelta shouldNotBeNull {
                stopReason shouldBe "end_turn"
                stopSequence shouldBe null
            }
            usage.shouldNotBeNull {
                outputTokens shouldBe 15
            }
        }
        result[7] as AnthropicSseData.MessageStopData shouldNotBeNull {
        }
    }
}
