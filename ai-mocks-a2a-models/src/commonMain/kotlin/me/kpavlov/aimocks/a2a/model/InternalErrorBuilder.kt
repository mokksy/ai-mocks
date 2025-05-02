package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [InternalError].
 *
 * Example usage:
 * ```
 * val error = internalError {
 *     data = myErrorData
 * }
 * ```
 */
public class InternalErrorBuilder : JSONRPCErrorBuilder<InternalError, InternalErrorBuilder>() {
    /**
     * Builds an [InternalError] instance with the configured parameters.
     *
     * @return A new [InternalError] instance.
     */
    public override fun build(): InternalError =
        InternalError(
            data = data,
        )
}

/**
 * Top-level DSL function for creating [InternalError].
 *
 * @param init The lambda to configure the internal error.
 * @return A new [InternalError] instance.
 */
public inline fun internalError(init: InternalErrorBuilder.() -> Unit): InternalError =
    InternalErrorBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [InternalError].
 *
 * @param init The consumer to configure the internal error.
 * @return A new [InternalError] instance.
 */
public fun internalError(init: Consumer<InternalErrorBuilder>): InternalError {
    val builder = InternalErrorBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [InternalError.Companion].
 *
 * @param init The lambda to configure the internal error.
 * @return A new [InternalError] instance.
 */
public fun InternalError.Companion.create(init: InternalErrorBuilder.() -> Unit): InternalError =
    InternalErrorBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [InternalError.Companion].
 *
 * @param init The consumer to configure the internal error.
 * @return A new [InternalError] instance.
 */
public fun InternalError.Companion.create(init: Consumer<InternalErrorBuilder>): InternalError {
    val builder = InternalErrorBuilder()
    init.accept(builder)
    return builder.build()
}
