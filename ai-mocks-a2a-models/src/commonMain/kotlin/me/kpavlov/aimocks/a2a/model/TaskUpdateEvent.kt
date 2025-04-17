package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.Serializable
import me.kpavlov.aimocks.a2a.model.serializers.TaskUpdateEventSerializer

@Serializable
(with = TaskUpdateEventSerializer::class)
public sealed interface TaskUpdateEvent {
    public fun id(): TaskId
}
