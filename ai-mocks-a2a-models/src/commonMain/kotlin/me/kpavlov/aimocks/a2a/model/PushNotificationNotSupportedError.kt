package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class PushNotificationNotSupportedError(
    /** Error code */
    @SerialName("code")
    val code: Int = -32003,
    /** A short description of the error */
    @SerialName("message")
    val message: String = "Push Notification is not supported",
    @Contextual
    @SerialName("data")
    val data: Any? = null,
) {
    init {
        require(code == -32003) { "code not constant value -32003 - $code" }
        require(message == "Push Notification is not supported") {
            "message not constant value Push Notification is not supported - $message"
        }
    }
}
