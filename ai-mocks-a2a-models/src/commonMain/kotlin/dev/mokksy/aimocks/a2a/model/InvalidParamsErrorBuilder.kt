package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [InvalidParamsError].
 *
 * Example usage:
 * ```
 * val error = invalidParamsError {
 *     data = myErrorData
 * }
 * ```
 */
public class InvalidParamsErrorBuilder :
    JSONRPCErrorBuilder<InvalidParamsError, InvalidParamsErrorBuilder>() {
    /**
     * Builds an [InvalidParamsError] instance with the configured parameters.
     *
     * @return A new [InvalidParamsError] instance.
     */
    public override fun build(): InvalidParamsError =
        InvalidParamsError(
            data = data,
        )
}

/**
 * Top-level DSL function for creating [InvalidParamsError].
 *
 * @param init The lambda to configure the invalid params error.
 * @return A new [InvalidParamsError] instance.
 */
public inline fun invalidParamsError(
    init: InvalidParamsErrorBuilder.() -> Unit,
): InvalidParamsError = InvalidParamsErrorBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [InvalidParamsError].
 *
 * @param init The consumer to configure the invalid params error.
 * @return A new [InvalidParamsError] instance.
 */
public fun invalidParamsError(init: Consumer<InvalidParamsErrorBuilder>): InvalidParamsError {
    val builder = InvalidParamsErrorBuilder()
    init.accept(builder)
    return builder.build()
}
