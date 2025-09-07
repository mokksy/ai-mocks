/*
 * SendMessageRequestBuilder.kt
 *
 * Builder for SendMessageRequest according to the A2A protocol.
 * See: https://a2a-protocol.org/latest/specification/
 */
package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder class for creating [SendMessageRequest] instances.
 *
 * This builder provides a fluent API for creating SendMessageRequest objects,
 * making it easier to configure message send requests according to the A2A protocol.
 *
 * @see [A2A Protocol - Send a Message](https://a2a-protocol.org/latest/specification/)
 *
 * Example usage:
 * ```
 * val request = sendMessageRequest {
 *     id = "request-123"
 *     params {
 *         message {
 *             role = Message.Role.user
 *             parts = listOf(
 *                 textPart(text = "Hello, how can I help you?")
 *             )
 *         }
 *         streamResponse = true
 *     }
 * }
 * ```
 */
public class SendMessageRequestBuilder {
    public var id: String? = null
    public var params: MessageSendParams? = null

    /**
     * Sets the ID of the request.
     *
     * @param id The ID of the request.
     * @return This builder instance for method chaining.
     */
    public fun id(id: String): SendMessageRequestBuilder =
        apply {
            this.id = id
        }

    /**
     * Configures the message send params using a lambda with receiver.
     *
     * @param init The lambda to configure the message send params.
     * @return This builder instance for method chaining.
     */
    public fun params(init: MessageSendParamsBuilder.() -> Unit): SendMessageRequestBuilder =
        apply {
            this.params = MessageSendParams.build(init)
        }

    /**
     * Configures the message send params using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the message send params.
     * @return This builder instance for method chaining.
     */
    public fun params(init: Consumer<MessageSendParamsBuilder>): SendMessageRequestBuilder =
        apply {
            val builder = MessageSendParamsBuilder()
            init.accept(builder)
            this.params = builder.build()
        }

    /**
     * Builds a [SendMessageRequest] instance with the configured parameters.
     *
     * @return A new [SendMessageRequest] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): SendMessageRequest {
        requireNotNull(params) { "Params are required" }

        return SendMessageRequest(
            id = id,
            params = params!!,
        )
    }
}

/**
 * Top-level DSL function for creating [SendMessageRequest].
 *
 * @param init The lambda to configure the send message request.
 * @return A new [SendMessageRequest] instance.
 */
public inline fun sendMessageRequest(
    init: SendMessageRequestBuilder.() -> Unit,
): SendMessageRequest = SendMessageRequestBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [SendMessageRequest].
 *
 * @param init The consumer to configure the send message request.
 * @return A new [SendMessageRequest] instance.
 */
public fun sendMessageRequest(init: Consumer<SendMessageRequestBuilder>): SendMessageRequest {
    val builder = SendMessageRequestBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new instance of a SendMessageRequest using the provided configuration block.
 *
 * @param block A configuration block for building a SendMessageRequest instance using the SendMessageRequestBuilder.
 * @return A newly created SendMessageRequest instance.
 */
public fun SendMessageRequest.Companion.create(
    block: SendMessageRequestBuilder.() -> Unit,
): SendMessageRequest = SendMessageRequestBuilder().apply(block).build()

/**
 * Creates a new instance of a SendMessageRequest using the provided Java-friendly Consumer.
 *
 * @param block A consumer for building a SendMessageRequest instance using the SendMessageRequestBuilder.
 * @return A newly created SendMessageRequest instance.
 */
public fun SendMessageRequest.Companion.create(
    block: Consumer<SendMessageRequestBuilder>,
): SendMessageRequest {
    val builder = SendMessageRequestBuilder()
    block.accept(builder)
    return builder.build()
}
