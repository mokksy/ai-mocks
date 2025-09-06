/*
 * MessageSendParams.kt
 *
 * Parameters for the message/send method according to the A2A protocol.
 * See: https://a2a-protocol.org/latest/specification/
 */
package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Parameters for sending a message to an agent using the A2A protocol's message/send method.
 *
 * According to the A2A specification, this represents the parameters for the core message sending
 * functionality that enables agent-to-agent communication.
 *
 * @see [A2A Protocol - Send a Message](https://a2a-protocol.org/latest/specification/)
 */
@Serializable
public data class MessageSendParams
    @JvmOverloads
    constructor(
        /**
         * The message to send to the agent.
         * Contains the actual content including text, files, or structured data parts.
         */
        @SerialName("message")
        val message: Message,
        /**
         * Optional configuration for the send request.
         * Controls aspects like blocking behavior, history length, and output modes.
         */
        @SerialName("configuration")
        val configuration: MessageSendConfiguration? = null,
        /**
         * Optional metadata for extensions.
         * Provides a way to include additional data that may be used by extensions or specific implementations.
         */
        @SerialName("metadata")
        val metadata: Metadata? = null,
    ) {
        public companion object {
            /**
             * Creates a new MessageSendParams using the DSL builder.
             *
             * @param init The lambda to configure the message send params.
             * @return A new MessageSendParams instance.
             */
            public fun build(init: MessageSendParamsBuilder.() -> Unit): MessageSendParams =
                MessageSendParamsBuilder().apply(init).build()

            /**
             * Creates a new instance of MessageSendParams using the provided configuration block.
             *
             * @param block A configuration block for building a MessageSendParams instance using the MessageSendParamsBuilder.
             * @return A newly created MessageSendParams instance.
             */
            public fun create(block: MessageSendParamsBuilder.() -> Unit): MessageSendParams =
                MessageSendParamsBuilder().apply(block).build()
        }
    }
