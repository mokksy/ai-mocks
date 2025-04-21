package me.kpavlov.aimocks.a2a.notifications

import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.request.receive
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import me.kpavlov.aimocks.a2a.model.TaskId
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent
import java.util.concurrent.ConcurrentHashMap

internal class NotificationListener(internal val notificationsUri: String) {

    private val taskNotificationsMap = ConcurrentHashMap<TaskId, TaskNotificationHistory>()

    internal fun add(event: TaskUpdateEvent) {
        getByTaskId(taskId = event.id()).add(event)
    }

    internal fun getByTaskId(taskId: TaskId): TaskNotificationHistory =
        taskNotificationsMap.computeIfAbsent(taskId) {
            TaskNotificationHistory(taskId = taskId)
        }
}

internal fun Application.configureNotificationListener(
    notificationsUri: String,
    listener: NotificationListener,
    verbose: Boolean
) {
    require(notificationsUri.isNotBlank()) { "notificationsUri must not be blank" }
    require(notificationsUri.startsWith("/")) { "notificationsUri must start with '/'" }
    require(notificationsUri.length > 1) { "notificationsUri must not be root url" }

    routing {
        post(listener.notificationsUri) {
            val event = call.receive<TaskUpdateEvent>()
            if (verbose) {
                log.info("Received task notification: {}", event)
            } else {
                log.trace("Received task notification: {}", event)
            }
            listener.add(event)
        }
    }

}
