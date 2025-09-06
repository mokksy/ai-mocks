package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [JSONRPCResponse].
 *
 * Example usage:
 * ```
 * val response = jsonRPCResponse {
 *     id = myRequestId
 *     result = myResult
 * }
 * ```
 */
public class JSONRPCResponseBuilder {
    public var id: RequestId? = null
    public var result: Any? = null
    public var error: JSONRPCError? = null

    /**
     * Sets the ID of the response.
     *
     * @param id The ID of the response.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): JSONRPCResponseBuilder =
        apply {
            this.id = id
        }

    /**
     * Sets the result.
     *
     * @param result The result.
     * @return This builder instance for method chaining.
     */
    public fun result(result: Any?): JSONRPCResponseBuilder =
        apply {
            this.result = result
        }

    /**
     * Sets the error.
     *
     * @param error The error.
     * @return This builder instance for method chaining.
     */
    public fun error(error: JSONRPCError): JSONRPCResponseBuilder =
        apply {
            this.error = error
        }

    /**
     * Builds a [JSONRPCResponse] instance with the configured parameters.
     *
     * @return A new [JSONRPCResponse] instance.
     */
    public fun build(): JSONRPCResponse =
        JSONRPCResponse(
            id = id,
            result = result,
            error = error,
        )
}

/**
 * Top-level DSL function for creating [JSONRPCResponse].
 *
 * @param init The lambda to configure the JSON-RPC response.
 * @return A new [JSONRPCResponse] instance.
 */
public inline fun jsonRPCResponse(init: JSONRPCResponseBuilder.() -> Unit): JSONRPCResponse =
    JSONRPCResponseBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [JSONRPCResponse].
 *
 * @param init The consumer to configure the JSON-RPC response.
 * @return A new [JSONRPCResponse] instance.
 */
public fun jsonRPCResponse(init: Consumer<JSONRPCResponseBuilder>): JSONRPCResponse {
    val builder = JSONRPCResponseBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [JSONRPCResponse.Companion].
 *
 * @param init The lambda to configure the JSON-RPC response.
 * @return A new [JSONRPCResponse] instance.
 */
public fun JSONRPCResponse.Companion.create(init: JSONRPCResponseBuilder.() -> Unit): JSONRPCResponse =
    JSONRPCResponseBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [JSONRPCResponse.Companion].
 *
 * @param init The consumer to configure the JSON-RPC response.
 * @return A new [JSONRPCResponse] instance.
 */
public fun JSONRPCResponse.Companion.create(init: Consumer<JSONRPCResponseBuilder>): JSONRPCResponse {
    val builder = JSONRPCResponseBuilder()
    init.accept(builder)
    return builder.build()
}
