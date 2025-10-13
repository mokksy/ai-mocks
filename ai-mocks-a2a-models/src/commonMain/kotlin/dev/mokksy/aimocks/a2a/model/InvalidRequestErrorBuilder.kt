package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [InvalidRequestError].
 *
 * Example usage:
 * ```
 * val error = invalidRequestError {
 *     data = myErrorData
 * }
 * ```
 */
public class InvalidRequestErrorBuilder :
    JSONRPCErrorBuilder<InvalidRequestError, InvalidRequestErrorBuilder>() {
    /**
     * Builds an [InvalidRequestError] instance with the configured parameters.
     *
     * @return A new [InvalidRequestError] instance.
     */
    public override fun build(): InvalidRequestError =
        InvalidRequestError(
            data = data,
        )
}

/**
 * Top-level DSL function for creating [InvalidRequestError].
 *
 * @param init The lambda to configure the invalid request error.
 * @return A new [InvalidRequestError] instance.
 */
public inline fun invalidRequestError(
    init: InvalidRequestErrorBuilder.() -> Unit,
): InvalidRequestError = InvalidRequestErrorBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [InvalidRequestError].
 *
 * @param init The consumer to configure the invalid request error.
 * @return A new [InvalidRequestError] instance.
 */
public fun invalidRequestError(init: Consumer<InvalidRequestErrorBuilder>): InvalidRequestError {
    val builder = InvalidRequestErrorBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [InvalidRequestError.Companion].
 *
 * @param init The lambda to configure the invalid request error.
 * @return A new [InvalidRequestError] instance.
 */
public fun InvalidRequestError.Companion.create(
    init: InvalidRequestErrorBuilder.() -> Unit,
): InvalidRequestError = InvalidRequestErrorBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [InvalidRequestError.Companion].
 *
 * @param init The consumer to configure the invalid request error.
 * @return A new [InvalidRequestError] instance.
 */
public fun InvalidRequestError.Companion.create(
    init: Consumer<InvalidRequestErrorBuilder>,
): InvalidRequestError {
    val builder = InvalidRequestErrorBuilder()
    init.accept(builder)
    return builder.build()
}
