package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class PushNotificationNotSupportedError(
    /** Error code */
    @Contextual
    @SerialName("code")
    val code: Int = -32003,
    /** A short description of the error */
    @Contextual
    @SerialName("message")
    val message: String = "Push Notification is not supported",
    @Contextual
    @SerialName("data")
    val data: Any? = null,
) {
    init {
        require(code == -32003) { "code not constant value -32003 - $code" }
        require(message == cg_str0) { "message not constant value $cg_str0 - $message" }
    }

    private companion object {
        @Suppress("ktlint:standard:property-naming")
        private const val cg_str0 = "Push Notification is not supported"
    }
}
