@file:Suppress("TooManyFunctions")
@file:JvmName("ErrorCodes")

package dev.mokksy.aimocks.a2a.model

public const val JSON_PARSE_ERROR_CODE: Long = -32700
public const val INVALID_REQUEST_ERROR_CODE: Long = -32600
public const val METHOD_NOT_FOUND_ERROR_CODE: Long = -32601
public const val INVALID_PARAMS_ERROR_CODE: Long = -32602
public const val INTERNAL_ERROR_CODE: Long = -32603
public const val TASK_NOT_FOUND_ERROR_CODE: Long = -32001
public const val TASK_NOT_CANCELABLE_ERROR_CODE: Long = -32002
public const val PUSH_NOTIFICATION_NOT_SUPPORTED_ERROR_CODE: Long = -32003
public const val UNSUPPORTED_OPERATION_ERROR_CODE: Long = -32004
public const val CONTENT_TYPE_NOT_SUPPORTED_CODE: Long = -32005
public const val INVALID_AGENT_ERROR_CODE: Long = -32006
public const val AUTHENTICATED_EXTENDED_CARD_NOT_CONFIGURED_ERROR_CODE: Long = -32007

@JvmOverloads
public fun jsonParseError(
    message: String = "Invalid JSON payload",
    data: Data? = null,
): JSONRPCError =
    JSONRPCError(
        code = JSON_PARSE_ERROR_CODE,
        message = message,
        data = data,
    )

@JvmOverloads
public fun invalidRequestError(
    message: String = "Request payload validation error",
    data: Data? = null,
): JSONRPCError =
    JSONRPCError(
        code = INVALID_REQUEST_ERROR_CODE,
        message = message,
        data = data,
    )

@JvmOverloads
public fun methodNotFoundError(
    message: String = "Method not found",
    data: Data? = null,
): JSONRPCError =
    JSONRPCError(
        code = METHOD_NOT_FOUND_ERROR_CODE,
        message = message,
        data = data,
    )

@JvmOverloads
public fun invalidParamsError(
    message: String = "Invalid parameters",
    data: Data? = null,
): JSONRPCError =
    JSONRPCError(
        code = INVALID_PARAMS_ERROR_CODE,
        message = message,
        data = data,
    )

@JvmOverloads
public fun internalError(
    message: String = "Internal error",
    data: Data? = null,
): JSONRPCError =
    JSONRPCError(
        code = INTERNAL_ERROR_CODE,
        message = message,
        data = data,
    )

@JvmOverloads
public fun taskNotFoundError(
    message: String = "Task not found",
    data: Data? = null,
): JSONRPCError =
    JSONRPCError(
        code = TASK_NOT_FOUND_ERROR_CODE,
        message = message,
        data = data,
    )

@JvmOverloads
public fun taskNotCancelableError(
    message: String = "Task cannot be canceled",
    data: Data? = null,
): JSONRPCError =
    JSONRPCError(
        code = TASK_NOT_CANCELABLE_ERROR_CODE,
        message = message,
        data = data,
    )

@JvmOverloads
public fun pushNotificationNotSupportedError(
    message: String = "Push Notification is not supported",
    data: Data? = null,
): JSONRPCError =
    JSONRPCError(
        code = PUSH_NOTIFICATION_NOT_SUPPORTED_ERROR_CODE,
        message = message,
        data = data,
    )

@JvmOverloads
public fun unsupportedOperationError(
    message: String = "This operation is not supported",
    data: Data? = null,
): JSONRPCError =
    JSONRPCError(
        code = UNSUPPORTED_OPERATION_ERROR_CODE,
        message = message,
        data = data,
    )

@JvmOverloads
public fun contentTypeNotSupportedError(
    message: String = "Incompatible content types",
    data: Data? = null,
): JSONRPCError =
    JSONRPCError(
        code = CONTENT_TYPE_NOT_SUPPORTED_CODE,
        message = message,
        data = data,
    )

@JvmOverloads
public fun invalidAgentResponseError(
    message: String = "Invalid agent response type",
    data: Data? = null,
): JSONRPCError =
    JSONRPCError(
        code = INVALID_AGENT_ERROR_CODE,
        message = message,
        data = data,
    )

@JvmOverloads
public fun authenticatedExtendedCardNotConfiguredError(
    message: String = "Authenticated Extended Card is not configured",
    data: Data? = null,
): JSONRPCError =
    JSONRPCError(
        code = AUTHENTICATED_EXTENDED_CARD_NOT_CONFIGURED_ERROR_CODE,
        message = message,
        data = data,
    )
