package dev.mokksy.aimocks.a2a.model

import dev.mokksy.aimocks.a2a.model.serializers.TaskUpdateEventSerializer
import kotlinx.serialization.Serializable

@Serializable
(with = TaskUpdateEventSerializer::class)
public sealed interface TaskUpdateEvent {
    public fun id(): TaskId
}
