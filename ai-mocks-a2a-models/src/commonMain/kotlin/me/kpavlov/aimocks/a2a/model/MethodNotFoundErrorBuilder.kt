package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [MethodNotFoundError].
 *
 * Example usage:
 * ```
 * val error = methodNotFoundError {
 *     data = myErrorData
 * }
 * ```
 */
public class MethodNotFoundErrorBuilder :
    JSONRPCErrorBuilder<MethodNotFoundError, MethodNotFoundErrorBuilder>() {
    /**
     * Builds a [MethodNotFoundError] instance with the configured parameters.
     *
     * @return A new [MethodNotFoundError] instance.
     */
    public override fun build(): MethodNotFoundError =
        MethodNotFoundError(
            data = data,
        )
}

/**
 * Top-level DSL function for creating [MethodNotFoundError].
 *
 * @param init The lambda to configure the method not found error.
 * @return A new [MethodNotFoundError] instance.
 */
public inline fun methodNotFoundError(
    init: MethodNotFoundErrorBuilder.() -> Unit,
): MethodNotFoundError = MethodNotFoundErrorBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [MethodNotFoundError].
 *
 * @param init The consumer to configure the method not found error.
 * @return A new [MethodNotFoundError] instance.
 */
public fun methodNotFoundError(init: Consumer<MethodNotFoundErrorBuilder>): MethodNotFoundError {
    val builder = MethodNotFoundErrorBuilder()
    init.accept(builder)
    return builder.build()
}
