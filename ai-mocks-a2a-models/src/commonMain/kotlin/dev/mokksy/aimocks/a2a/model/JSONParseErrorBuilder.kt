package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [JSONParseError].
 *
 * Example usage:
 * ```
 * val error = jsonParseError {
 *     data = myErrorData
 * }
 * ```
 */
public class JSONParseErrorBuilder : JSONRPCErrorBuilder<JSONParseError, JSONParseErrorBuilder>() {
    /**
     * Builds a [JSONParseError] instance with the configured parameters.
     *
     * @return A new [JSONParseError] instance.
     */
    public override fun build(): JSONParseError =
        JSONParseError(
            data = data,
        )
}

/**
 * Top-level DSL function for creating [JSONParseError].
 *
 * @param init The lambda to configure the JSON parse error.
 * @return A new [JSONParseError] instance.
 */
public inline fun jsonParseError(init: JSONParseErrorBuilder.() -> Unit): JSONParseError =
    JSONParseErrorBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [JSONParseError].
 *
 * @param init The consumer to configure the JSON parse error.
 * @return A new [JSONParseError] instance.
 */
public fun jsonParseError(init: Consumer<JSONParseErrorBuilder>): JSONParseError {
    val builder = JSONParseErrorBuilder()
    init.accept(builder)
    return builder.build()
}
