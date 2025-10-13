/*
 * MessageSendParamsBuilder.kt
 *
 * Builder for MessageSendParams according to the A2A protocol.
 * See: https://a2a-protocol.org/latest/specification/
 */
package dev.mokksy.aimocks.a2a.model

/**
 * Builder for creating MessageSendParams instances using a DSL approach.
 *
 * Example usage:
 * ```
 * val params = MessageSendParams.build {
 *     message {
 *         role = Message.Role.user
 *         parts = listOf(
 *             TextPart(text = "Hello, how can I help you?")
 *         )
 *     }
 *     streamResponse = true
 * }
 * ```
 *
 * @see [A2A Protocol - Send a Message](https://a2a-protocol.org/latest/specification/)
 */
public class MessageSendParamsBuilder {
    /**
     * The message to send to the agent.
     */
    public var message: Message? = null

    /**
     * Optional configuration for the send request.
     */
    public var configuration: MessageSendConfiguration? = null

    /**
     * Optional metadata for extensions.
     */
    public var metadata: Metadata? = null

    /**
     * Configure the message using a DSL builder.
     *
     * @param init The lambda to configure the message.
     */
    public fun message(init: MessageBuilder.() -> Unit) {
        this.message = MessageBuilder().apply(init).build()
    }

    /**
     * Configure the configuration using a DSL builder.
     *
     * @param init The lambda to configure the configuration.
     */
    public fun configuration(init: MessageSendConfigurationBuilder.() -> Unit) {
        this.configuration = MessageSendConfigurationBuilder().apply(init).build()
    }

    /**
     * Builds the MessageSendParams instance.
     *
     * @return A new MessageSendParams instance.
     * @throws IllegalArgumentException if required fields are not set.
     */
    public fun build(): MessageSendParams {
        val message = this.message ?: throw IllegalArgumentException("message is required")

        return MessageSendParams(
            message = message,
            configuration = configuration,
            metadata = metadata,
        )
    }
}
