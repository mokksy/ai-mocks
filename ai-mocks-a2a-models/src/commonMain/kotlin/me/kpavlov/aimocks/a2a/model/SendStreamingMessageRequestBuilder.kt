/*
 * SendStreamingMessageRequestBuilder.kt
 *
 * Builder for SendStreamingMessageRequest according to the A2A protocol.
 * See: https://a2a-protocol.org/latest/specification/
 */
package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder class for creating [SendStreamingMessageRequest] instances.
 *
 * This builder provides a fluent API for creating SendStreamingMessageRequest objects,
 * making it easier to configure streaming message requests according to the A2A protocol.
 *
 * Example usage:
 * ```
 * val request = sendStreamingMessageRequest {
 *     id = "request-789"
 *     params {
 *         message {
 *             role = Message.Role.user
 *             parts = listOf(
 *                 TextPart(text = "Generate a detailed report")
 *             )
 *         }
 *         streamResponse = true
 *     }
 * }
 * ```
 *
 * @see [A2A Protocol - Streaming Messages](https://a2a-protocol.org/latest/specification/)
 */
public class SendStreamingMessageRequestBuilder {
    public var id: String? = null
    public var params: MessageSendParams? = null

    /**
     * Sets the ID of the request.
     *
     * @param id The ID of the request.
     * @return This builder instance for method chaining.
     */
    public fun id(id: String): SendStreamingMessageRequestBuilder =
        apply {
            this.id = id
        }

    /**
     * Configures the message send params using a lambda with receiver.
     *
     * @param init The lambda to configure the message send params.
     * @return This builder instance for method chaining.
     */
    public fun params(
        init: MessageSendParamsBuilder.() -> Unit,
    ): SendStreamingMessageRequestBuilder =
        apply {
            this.params = MessageSendParams.build(init)
        }

    /**
     * Configures the message send params using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the message send params.
     * @return This builder instance for method chaining.
     */
    public fun params(
        init: Consumer<MessageSendParamsBuilder>,
    ): SendStreamingMessageRequestBuilder =
        apply {
            val builder = MessageSendParamsBuilder()
            init.accept(builder)
            this.params = builder.build()
        }

    /**
     * Builds a [SendStreamingMessageRequest] instance with the configured parameters.
     *
     * @return A new [SendStreamingMessageRequest] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): SendStreamingMessageRequest {
        requireNotNull(params) { "Params are required" }

        return SendStreamingMessageRequest(
            id = id,
            params = params!!,
        )
    }
}

/**
 * Top-level DSL function for creating [SendStreamingMessageRequest].
 *
 * @param init The lambda to configure the send streaming message request.
 * @return A new [SendStreamingMessageRequest] instance.
 */
public inline fun sendStreamingMessageRequest(
    init: SendStreamingMessageRequestBuilder.() -> Unit,
): SendStreamingMessageRequest = SendStreamingMessageRequestBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [SendStreamingMessageRequest].
 *
 * @param init The consumer to configure the send streaming message request.
 * @return A new [SendStreamingMessageRequest] instance.
 */
public fun sendStreamingMessageRequest(
    init: Consumer<SendStreamingMessageRequestBuilder>,
): SendStreamingMessageRequest {
    val builder = SendStreamingMessageRequestBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new instance of a SendStreamingMessageRequest using the provided configuration block.
 *
 * @param block A configuration block for building a SendStreamingMessageRequest instance using the SendStreamingMessageRequestBuilder.
 * @return A newly created SendStreamingMessageRequest instance.
 */
public fun SendStreamingMessageRequest.Companion.create(
    block: SendStreamingMessageRequestBuilder.() -> Unit,
): SendStreamingMessageRequest = SendStreamingMessageRequestBuilder().apply(block).build()

/**
 * Creates a new instance of a SendStreamingMessageRequest using the provided Java-friendly Consumer.
 *
 * @param block A consumer for building a SendStreamingMessageRequest instance using the SendStreamingMessageRequestBuilder.
 * @return A newly created SendStreamingMessageRequest instance.
 */
public fun SendStreamingMessageRequest.Companion.create(
    block: Consumer<SendStreamingMessageRequestBuilder>,
): SendStreamingMessageRequest {
    val builder = SendStreamingMessageRequestBuilder()
    block.accept(builder)
    return builder.build()
}
