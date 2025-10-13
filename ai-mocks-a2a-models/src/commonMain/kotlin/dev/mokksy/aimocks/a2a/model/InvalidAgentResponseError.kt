package dev.mokksy.aimocks.a2a.model

import kotlinx.serialization.Serializable

/**
 * An A2A-specific error indicating that the agent returned a response that
 * does not conform to the specification for the current method.
 *
 * This error is returned when an agent generates an invalid response for the requested method.
 *
 * @param code The error code for an invalid agent response. Always -32006.
 * @param message The error message.
 * @param data A primitive or structured value containing additional information about the error.
 *             This may be omitted.
 */
@Serializable
public class InvalidAgentResponseError : JSONRPCError {
    @JvmOverloads
    public constructor(data: Data? = null) : super(
        code = -32006,
        message = "Invalid agent response",
        data = data,
    )

    public fun copy(data: Data? = this.data): InvalidAgentResponseError =
        InvalidAgentResponseError(data = data)
}
