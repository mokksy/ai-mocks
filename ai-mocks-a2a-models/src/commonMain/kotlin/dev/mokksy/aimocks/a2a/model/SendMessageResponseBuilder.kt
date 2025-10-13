/*
 * SendMessageResponseBuilder.kt
 *
 * Builder for SendMessageResponse according to the A2A protocol.
 * See: https://a2a-protocol.org/latest/specification/
 */
package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder class for creating [SendMessageResponse] instances.
 *
 * This builder provides a fluent API for creating SendMessageResponse objects,
 * making it easier to configure message send responses according to the A2A protocol.
 *
 * Example usage:
 * ```
 * val response = sendMessageResponse {
 *     id = "request-123"
 *     result {
 *         id = "task-456"
 *         status {
 *             state = TaskState.working
 *             timestamp = System.currentTimeMillis()
 *         }
 *         artifacts = listOf(
 *             Artifact {
 *                 name = "response"
 *                 parts = listOf(
 *                     TextPart(text = "I can help you with that!")
 *                 )
 *             }
 *         )
 *     }
 * }
 * ```
 *
 * @see [A2A Protocol - Send a Message](https://a2a-protocol.org/latest/specification/)
 */
public class SendMessageResponseBuilder {
    public var id: RequestId? = null
    public var result: Task? = null
    public var error: JSONRPCError? = null

    /**
     * Sets the ID of the response.
     *
     * @param id The ID of the response.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): SendMessageResponseBuilder =
        apply {
            this.id = id
        }

    /**
     * Sets the result task.
     *
     * @param result The task result.
     * @return This builder instance for method chaining.
     */
    public fun result(result: Task): SendMessageResponseBuilder =
        apply {
            this.result = result
        }

    /**
     * Configures the result task using a lambda with receiver.
     *
     * @param init The lambda to configure the task.
     * @return This builder instance for method chaining.
     */
    public fun result(init: TaskBuilder.() -> Unit): SendMessageResponseBuilder =
        apply {
            this.result = TaskBuilder().apply(init).build()
        }

    /**
     * Configures the result task using a Java-friendly Consumer.
     *
     * @param init The consumer to configure the task.
     * @return This builder instance for method chaining.
     */
    public fun result(init: Consumer<TaskBuilder>): SendMessageResponseBuilder =
        apply {
            val builder = TaskBuilder()
            init.accept(builder)
            this.result = builder.build()
        }

    /**
     * Sets the error.
     *
     * @param error The JSON-RPC error.
     * @return This builder instance for method chaining.
     */
    public fun error(error: JSONRPCError): SendMessageResponseBuilder =
        apply {
            this.error = error
        }

    /**
     * Builds a [SendMessageResponse] instance with the configured parameters.
     *
     * @return A new [SendMessageResponse] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): SendMessageResponse {
        require(result != null || error != null) { "Either result or error must be provided" }
        require(result == null || error == null) { "Cannot provide both result and error" }

        return SendMessageResponse(
            id = id,
            result = result,
            error = error,
        )
    }
}

/**
 * Top-level DSL function for creating [SendMessageResponse].
 *
 * @param init The lambda to configure the send message response.
 * @return A new [SendMessageResponse] instance.
 */
public inline fun sendMessageResponse(
    init: SendMessageResponseBuilder.() -> Unit,
): SendMessageResponse = SendMessageResponseBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [SendMessageResponse].
 *
 * @param init The consumer to configure the send message response.
 * @return A new [SendMessageResponse] instance.
 */
public fun sendMessageResponse(init: Consumer<SendMessageResponseBuilder>): SendMessageResponse {
    val builder = SendMessageResponseBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new instance of a SendMessageResponse using the provided configuration block.
 *
 * @param block A configuration block for building a SendMessageResponse instance using the SendMessageResponseBuilder.
 * @return A newly created SendMessageResponse instance.
 */
public fun SendMessageResponse.Companion.create(
    block: SendMessageResponseBuilder.() -> Unit,
): SendMessageResponse = SendMessageResponseBuilder().apply(block).build()

/**
 * Creates a new instance of a SendMessageResponse using the provided Java-friendly Consumer.
 *
 * @param block A consumer for building a SendMessageResponse instance using the SendMessageResponseBuilder.
 * @return A newly created SendMessageResponse instance.
 */
public fun SendMessageResponse.Companion.create(
    block: Consumer<SendMessageResponseBuilder>,
): SendMessageResponse {
    val builder = SendMessageResponseBuilder()
    block.accept(builder)
    return builder.build()
}
