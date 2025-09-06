/*
 * MessageSendConfiguration.kt
 *
 * Configuration options for the message/send method according to the A2A protocol.
 * See: https://a2a-protocol.org/latest/specification/
 */
package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines configuration options for a `message/send` or `message/stream` request.
 *
 * According to the A2A specification, this provides optional configuration parameters
 * that control how the agent handles the message sending request.
 *
 * @see [A2A Protocol - Send a Message](https://a2a-protocol.org/latest/specification/)
 */
@Serializable
public data class MessageSendConfiguration
    @JvmOverloads
    constructor(
        /**
         * A list of output MIME types the client is prepared to accept in the response.
         */
        @SerialName("acceptedOutputModes")
        val acceptedOutputModes: List<String>? = null,
        /**
         * The number of most recent messages from the task's history to retrieve in the response.
         */
        @SerialName("historyLength")
        val historyLength: Int? = null,
        /**
         * Configuration for the agent to send push notifications for updates after the initial response.
         */
        @SerialName("pushNotificationConfig")
        val pushNotificationConfig: PushNotificationConfig? = null,
        /**
         * If true, the client will wait for the task to complete. The server may reject this if the task is long-running.
         */
        @SerialName("blocking")
        val blocking: Boolean? = null,
    ) {
        public companion object
    }
