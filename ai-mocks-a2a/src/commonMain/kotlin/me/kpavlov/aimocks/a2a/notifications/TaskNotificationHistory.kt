package me.kpavlov.aimocks.a2a.notifications

import me.kpavlov.aimocks.a2a.model.TaskId
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent
import java.util.concurrent.ConcurrentLinkedDeque

public class TaskNotificationHistory(
    public val taskId: TaskId,
) {
    private val events: ConcurrentLinkedDeque<TaskUpdateEvent> =
        ConcurrentLinkedDeque<TaskUpdateEvent>()

    internal fun add(event: TaskUpdateEvent) {
        events.offer(event)
    }

    internal fun clear() {
        events.clear()
    }

    public fun events(): List<TaskUpdateEvent> = events.toList()

    internal fun isEmpty(): Boolean = events.isEmpty()

    internal fun isNotEmpty(): Boolean = !isEmpty()

    internal fun find(predicate: (TaskUpdateEvent) -> Boolean): TaskUpdateEvent? = events.find(predicate)

    internal fun extract(predicate: (TaskUpdateEvent) -> Boolean): List<TaskUpdateEvent> {
        events.filter(predicate).also { found ->
            for (event in found) {
                events.remove(event)
            }
            return found.toList()
        }
    }
}
