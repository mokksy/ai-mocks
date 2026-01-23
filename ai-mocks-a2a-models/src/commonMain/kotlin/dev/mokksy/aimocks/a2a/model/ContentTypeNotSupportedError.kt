package dev.mokksy.aimocks.a2a.model

import kotlinx.serialization.Serializable

/**
 * An A2A-specific error indicating an incompatibility between the requested
 * content types and the agent's capabilities.
 *
 * This error is returned when a Media Type provided in the request's `message.parts`
 * (or implied for an artifact) is not supported by the agent or the specific skill being invoked.
 *
 * @param code The error code for an unsupported content type. Always -32005.
 * @param message The error message.
 * @param data A primitive or structured value containing additional information about the error.
 *             This may be omitted.
 */
@Serializable
public class ContentTypeNotSupportedError : JSONRPCError {
    @JvmOverloads
    public constructor(data: Data? = null) : super(
        code = CONTENT_TYPE_NOT_SUPPORTED_CODE,
        message = "Incompatible content types",
        data = data,
    )

    public fun copy(data: Data? = this.data): ContentTypeNotSupportedError =
        ContentTypeNotSupportedError(data = data)
}
