package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [PushNotificationNotSupportedError].
 *
 * Example usage:
 * ```
 * val error = pushNotificationNotSupportedError {
 *     data = myErrorData
 * }
 * ```
 */
public class PushNotificationNotSupportedErrorBuilder :
    JSONRPCErrorBuilder<
        PushNotificationNotSupportedError,
        PushNotificationNotSupportedErrorBuilder,
    >() {
    /**
     * Builds a [PushNotificationNotSupportedError] instance with the configured parameters.
     *
     * @return A new [PushNotificationNotSupportedError] instance.
     */
    public override fun build(): PushNotificationNotSupportedError =
        PushNotificationNotSupportedError(
            data = data,
        )
}

/**
 * Top-level DSL function for creating [PushNotificationNotSupportedError].
 *
 * @param init The lambda to configure the push notification not supported error.
 * @return A new [PushNotificationNotSupportedError] instance.
 */
public inline fun pushNotificationNotSupportedError(
    init: PushNotificationNotSupportedErrorBuilder.() -> Unit,
): PushNotificationNotSupportedError = PushNotificationNotSupportedErrorBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [PushNotificationNotSupportedError].
 *
 * @param init The consumer to configure the push notification not supported error.
 * @return A new [PushNotificationNotSupportedError] instance.
 */
public fun pushNotificationNotSupportedError(
    init: Consumer<PushNotificationNotSupportedErrorBuilder>,
): PushNotificationNotSupportedError {
    val builder = PushNotificationNotSupportedErrorBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [PushNotificationNotSupportedError.Companion].
 *
 * @param init The lambda to configure the push notification not supported error.
 * @return A new [PushNotificationNotSupportedError] instance.
 */
public fun PushNotificationNotSupportedError.Companion.create(
    init: PushNotificationNotSupportedErrorBuilder.() -> Unit,
): PushNotificationNotSupportedError = PushNotificationNotSupportedErrorBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [PushNotificationNotSupportedError.Companion].
 *
 * @param init The consumer to configure the push notification not supported error.
 * @return A new [PushNotificationNotSupportedError] instance.
 */
public fun PushNotificationNotSupportedError.Companion.create(
    init: Consumer<PushNotificationNotSupportedErrorBuilder>,
): PushNotificationNotSupportedError {
    val builder = PushNotificationNotSupportedErrorBuilder()
    init.accept(builder)
    return builder.build()
}
