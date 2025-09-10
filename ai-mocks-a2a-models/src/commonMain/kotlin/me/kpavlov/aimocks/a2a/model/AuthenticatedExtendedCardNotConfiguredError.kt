package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.Serializable

/**
 * An A2A-specific error indicating that the agent does not have an Authenticated Extended Card configured.
 *
 * This error is returned when a client attempts to retrieve an authenticated extended agent card
 * but the agent does not have one configured.
 *
 * @param code The error code for when an authenticated extended card is not configured. Always -32007.
 * @param message The error message.
 * @param data A primitive or structured value containing additional information about the error.
 *             This may be omitted.
 */
@Serializable
public class AuthenticatedExtendedCardNotConfiguredError : JSONRPCError {
    @JvmOverloads
    public constructor(data: Data? = null) : super(
        code = -32007,
        message = "Authenticated Extended Card is not configured",
        data = data,
    )

    public fun copy(data: Data? = this.data): AuthenticatedExtendedCardNotConfiguredError =
        AuthenticatedExtendedCardNotConfiguredError(data = data)
}
