package me.kpavlov.aimocks.gemini.content

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map

/**
 * Specification for configuring a streaming Gemini content generation response.
 *
 * This class provides a fluent API for configuring the streaming response that will be returned
 * by the mock Gemini API when a content generation request is made with streaming enabled.
 *
 * @property chunks The chunks of content to include in the streaming response.
 * @property finishReason The reason why the model stopped generating tokens.
 */
public class GeminiStreamingContentResponseSpecification {
    public var chunks: List<String> =
        listOf("This is ", "a mock ", "streaming ", "response ", "from ", "Gemini API.")
    public var finishReason: String = "STOP"
    public var role: String = "model"
    public var delayBetweenChunksMs: Long = 100

    /**
     * Sets the chunks of content for the streaming response.
     *
     * @param chunks The chunks of content to include in the streaming response.
     * @return This specification instance for method chaining.
     */
    public fun chunks(chunks: List<String>): GeminiStreamingContentResponseSpecification =
        apply {
            this.chunks = chunks
        }

    /**
     * Sets the chunks of content for the streaming response.
     *
     * @param vararg chunks The chunks of content to include in the streaming response.
     * @return This specification instance for method chaining.
     */
    public fun chunks(vararg chunks: String): GeminiStreamingContentResponseSpecification =
        apply {
            this.chunks = chunks.toList()
        }

    /**
     * Sets the finish reason for the streaming response.
     *
     * @param finishReason The reason why the model stopped generating tokens.
     * @return This specification instance for method chaining.
     */
    public fun finishReason(finishReason: String): GeminiStreamingContentResponseSpecification =
        apply {
            this.finishReason = finishReason
        }

    /**
     * Sets the role for the streaming response content.
     *
     * @param role The role of the content (e.g., "model").
     * @return This specification instance for method chaining.
     */
    public fun role(role: String): GeminiStreamingContentResponseSpecification =
        apply {
            this.role = role
        }

    /**
     * Sets the delay between chunks in milliseconds.
     *
     * @param delayMs The delay between chunks in milliseconds.
     * @return This specification instance for method chaining.
     */
    public fun delayBetweenChunksMs(delayMs: Long): GeminiStreamingContentResponseSpecification =
        apply {
            this.delayBetweenChunksMs = delayMs
        }

    /**
     * Creates a flow of chunks for the streaming response.
     *
     * @return A flow of chunks for the streaming response.
     */
    public fun chunksFlow(): Flow<String> = chunks.asFlow().map { it }
}
