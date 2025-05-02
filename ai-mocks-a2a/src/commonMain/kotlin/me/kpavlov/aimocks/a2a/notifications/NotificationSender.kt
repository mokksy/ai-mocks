package me.kpavlov.aimocks.a2a.notifications

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.a2a.model.PushNotificationConfig
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent

internal class NotificationSender {
    private val notificationClient: HttpClient =
        HttpClient {
            val json =
                Json {
                    prettyPrint = true
                    isLenient = true
                }
            install(ContentNegotiation) {
                json(json)
            }
        }

    internal suspend fun sendPushNotification(
        config: PushNotificationConfig,
        taskUpdateEvent: TaskUpdateEvent,
    ) {
        notificationClient.post(config.url) {
            if (config.token != null) {
                header("Authorization", "Bearer ${config.token}")
            }
            contentType(ContentType.Application.Json)
            setBody(taskUpdateEvent)
        }
    }
}
