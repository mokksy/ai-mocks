package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.Serializable

@Serializable
public class PushNotificationNotSupportedError : JSONRPCError {
    @JvmOverloads
    public constructor(data: Data? = null) :  super(-32003, "Push Notification is not supported", null)


    public fun copy(data: Data? = this.data): InternalError = InternalError(data = data)
}
