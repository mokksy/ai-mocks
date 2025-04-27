package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [JSONRPCMessage].
 *
 * Example usage:
 * ```
 * val message = jsonRPCMessage {
 *     id = myRequestId
 * }
 * ```
 */
public class JSONRPCMessageBuilder {
    public var id: RequestId? = null

    /**
     * Sets the ID of the message.
     *
     * @param id The ID of the message.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): JSONRPCMessageBuilder =
        apply {
            this.id = id
        }

    /**
     * Builds a [JSONRPCMessage] instance with the configured parameters.
     *
     * @return A new [JSONRPCMessage] instance.
     */
    public fun build(): JSONRPCMessage =
        JSONRPCMessage(
            id = id,
        )
}

/**
 * Top-level DSL function for creating [JSONRPCMessage].
 *
 * @param init The lambda to configure the JSON-RPC message.
 * @return A new [JSONRPCMessage] instance.
 */
public inline fun jsonRPCMessage(init: JSONRPCMessageBuilder.() -> Unit): JSONRPCMessage =
    JSONRPCMessageBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [JSONRPCMessage].
 *
 * @param init The consumer to configure the JSON-RPC message.
 * @return A new [JSONRPCMessage] instance.
 */
public fun jsonRPCMessage(init: Consumer<JSONRPCMessageBuilder>): JSONRPCMessage {
    val builder = JSONRPCMessageBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [JSONRPCMessage.Companion].
 *
 * @param init The lambda to configure the JSON-RPC message.
 * @return A new [JSONRPCMessage] instance.
 */
public fun JSONRPCMessage.Companion.create(
    init: JSONRPCMessageBuilder.() -> Unit,
): JSONRPCMessage = JSONRPCMessageBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [JSONRPCMessage.Companion].
 *
 * @param init The consumer to configure the JSON-RPC message.
 * @return A new [JSONRPCMessage] instance.
 */
public fun JSONRPCMessage.Companion.create(init: Consumer<JSONRPCMessageBuilder>): JSONRPCMessage {
    val builder = JSONRPCMessageBuilder()
    init.accept(builder)
    return builder.build()
}
