package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.Serializable

@Serializable
public class PushNotificationNotSupportedError : JSONRPCError {
    @JvmOverloads
    public constructor(data: Data? = null) : super(
        code = -32003,
        message = "Push Notification is not supported",
        data = null,
    )

    public fun copy(data: Data? = this.data): InternalError = InternalError(data = data)
}
