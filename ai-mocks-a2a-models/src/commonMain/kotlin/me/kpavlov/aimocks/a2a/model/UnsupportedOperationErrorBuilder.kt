package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [UnsupportedOperationError].
 *
 * Example usage:
 * ```
 * val error = unsupportedOperationError {
 *     data = myErrorData
 * }
 * ```
 */
public class UnsupportedOperationErrorBuilder :
    JSONRPCErrorBuilder<UnsupportedOperationError, UnsupportedOperationErrorBuilder>() {
    /**
     * Builds an [UnsupportedOperationError] instance with the configured parameters.
     *
     * @return A new [UnsupportedOperationError] instance.
     */
    public override fun build(): UnsupportedOperationError =
        UnsupportedOperationError(
            data = data,
        )
}

/**
 * Top-level DSL function for creating [UnsupportedOperationError].
 *
 * @param init The lambda to configure the unsupported operation error.
 * @return A new [UnsupportedOperationError] instance.
 */
public inline fun unsupportedOperationError(
    init: UnsupportedOperationErrorBuilder.() -> Unit,
): UnsupportedOperationError = UnsupportedOperationErrorBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [UnsupportedOperationError].
 *
 * @param init The consumer to configure the unsupported operation error.
 * @return A new [UnsupportedOperationError] instance.
 */
public fun unsupportedOperationError(
    init: Consumer<UnsupportedOperationErrorBuilder>,
): UnsupportedOperationError {
    val builder = UnsupportedOperationErrorBuilder()
    init.accept(builder)
    return builder.build()
}
