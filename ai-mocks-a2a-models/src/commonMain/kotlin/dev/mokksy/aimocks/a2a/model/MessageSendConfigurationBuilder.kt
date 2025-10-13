/*
 * MessageSendConfigurationBuilder.kt
 *
 * Builder for MessageSendConfiguration according to the A2A protocol.
 * See: https://a2a-protocol.org/latest/specification/
 */
package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder for creating MessageSendConfiguration instances using a DSL approach.
 *
 * Example usage:
 * ```
 * val configuration = MessageSendConfiguration.build {
 *     blocking = true
 *     historyLength = 10
 *     acceptedOutputModes = listOf("text/plain", "application/json")
 * }
 * ```
 *
 * @see [A2A Protocol - Send a Message](https://a2a-protocol.org/latest/specification/)
 */
public class MessageSendConfigurationBuilder {
    /**
     * A list of output MIME types the client is prepared to accept in the response.
     */
    public var acceptedOutputModes: List<String>? = null

    /**
     * The number of most recent messages from the task's history to retrieve in the response.
     */
    public var historyLength: Int? = null

    /**
     * Configuration for the agent to send push notifications for updates after the initial response.
     */
    public var pushNotificationConfig: PushNotificationConfig? = null

    /**
     * If true, the client will wait for the task to complete. The server may reject this if the task is long-running.
     */
    public var blocking: Boolean? = null

    /**
     * Configure the push notification config using a DSL builder.
     *
     * @param init The lambda to configure the push notification config.
     */
    public fun pushNotificationConfig(init: PushNotificationConfigBuilder.() -> Unit) {
        this.pushNotificationConfig = PushNotificationConfigBuilder().apply(init).build()
    }

    /**
     * Configure the push notification config using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the push notification config.
     */
    public fun pushNotificationConfig(init: Consumer<PushNotificationConfigBuilder>) {
        val builder = PushNotificationConfigBuilder()
        init.accept(builder)
        this.pushNotificationConfig = builder.build()
    }

    /**
     * Builds the MessageSendConfiguration instance.
     *
     * @return A new MessageSendConfiguration instance.
     */
    public fun build(): MessageSendConfiguration =
        MessageSendConfiguration(
            acceptedOutputModes = acceptedOutputModes,
            historyLength = historyLength,
            pushNotificationConfig = pushNotificationConfig,
            blocking = blocking,
        )
}

/**
 * Creates a new instance of MessageSendConfiguration using the provided configuration block.
 *
 * @param block A configuration block for building a MessageSendConfiguration instance using the MessageSendConfigurationBuilder.
 * @return A newly created MessageSendConfiguration instance.
 */
public fun MessageSendConfiguration.Companion.create(
    block: MessageSendConfigurationBuilder.() -> Unit,
): MessageSendConfiguration = MessageSendConfigurationBuilder().apply(block).build()

/**
 * Creates a new instance of MessageSendConfiguration using the provided Java-friendly Consumer.
 *
 * @param block A consumer for building a MessageSendConfiguration instance using the MessageSendConfigurationBuilder.
 * @return A newly created MessageSendConfiguration instance.
 */
public fun MessageSendConfiguration.Companion.create(
    block: Consumer<MessageSendConfigurationBuilder>,
): MessageSendConfiguration {
    val builder = MessageSendConfigurationBuilder()
    block.accept(builder)
    return builder.build()
}
