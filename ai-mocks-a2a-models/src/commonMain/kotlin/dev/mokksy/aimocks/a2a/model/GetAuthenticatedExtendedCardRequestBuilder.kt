package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

/**
 * DSL builder for [GetAuthenticatedExtendedCardRequest].
 *
 * Example usage:
 * ```
 * val request = getAuthenticatedExtendedCardRequest {
 *     id = "1"
 * }
 * ```
 */
public class GetAuthenticatedExtendedCardRequestBuilder {
    public var id: RequestId? = null

    /**
     * Sets the ID of the request.
     *
     * @param id The ID of the request.
     * @return This builder instance for method chaining.
     */
    public fun id(id: RequestId): GetAuthenticatedExtendedCardRequestBuilder =
        apply {
            this.id = id
        }

    /**
     * Builds a [GetAuthenticatedExtendedCardRequest] instance with the configured parameters.
     *
     * @return A new [GetAuthenticatedExtendedCardRequest] instance.
     */
    public fun build(): GetAuthenticatedExtendedCardRequest =
        GetAuthenticatedExtendedCardRequest(
            id = id,
        )
}

/**
 * Top-level DSL function for creating [GetAuthenticatedExtendedCardRequest].
 *
 * @param init The lambda to configure the get authenticated extended card request.
 * @return A new [GetAuthenticatedExtendedCardRequest] instance.
 */
public inline fun getAuthenticatedExtendedCardRequest(
    init: GetAuthenticatedExtendedCardRequestBuilder.() -> Unit,
): GetAuthenticatedExtendedCardRequest =
    GetAuthenticatedExtendedCardRequestBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [GetAuthenticatedExtendedCardRequest].
 *
 * @param init The consumer to configure the get authenticated extended card request.
 * @return A new [GetAuthenticatedExtendedCardRequest] instance.
 */
public fun getAuthenticatedExtendedCardRequest(
    init: Consumer<GetAuthenticatedExtendedCardRequestBuilder>,
): GetAuthenticatedExtendedCardRequest {
    val builder = GetAuthenticatedExtendedCardRequestBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [GetAuthenticatedExtendedCardRequest].
 *
 * @param init The lambda to configure the get authenticated extended card request.
 * @return A new [GetAuthenticatedExtendedCardRequest] instance.
 */
public fun GetAuthenticatedExtendedCardRequest.Companion.create(
    init: GetAuthenticatedExtendedCardRequestBuilder.() -> Unit,
): GetAuthenticatedExtendedCardRequest =
    GetAuthenticatedExtendedCardRequestBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [GetAuthenticatedExtendedCardRequest].
 *
 * @param init The consumer to configure the get authenticated extended card request.
 * @return A new [GetAuthenticatedExtendedCardRequest] instance.
 */
public fun GetAuthenticatedExtendedCardRequest.Companion.create(
    init: Consumer<GetAuthenticatedExtendedCardRequestBuilder>,
): GetAuthenticatedExtendedCardRequest {
    val builder = GetAuthenticatedExtendedCardRequestBuilder()
    init.accept(builder)
    return builder.build()
}
