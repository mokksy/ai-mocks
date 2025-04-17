package me.kpavlov.aimocks.a2a.model

public class TaskArtifactUpdateEventBuilder {
    public var id: TaskId? = null
    public var artifact: Artifact? = null
    public var metadata: Metadata? = null

    /**
     * Sets the id of the task artifact.
     *
     * @param id The unique identifier for the task.
     * @return This builder instance for method chaining.
     */
    public fun id(id: TaskId): TaskArtifactUpdateEventBuilder {
        this.id = id
        return this
    }

    /**
     * Sets the artifact for the event.
     *
     * @param artifact The artifact data.
     * @return This builder instance for method chaining.
     */
    public fun artifact(artifact: Artifact): TaskArtifactUpdateEventBuilder {
        this.artifact = artifact
        return this
    }

    /**
     * Sets the optional metadata for the event.
     *
     * @param metadata The metadata associated with the task artifact update.
     * @return This builder instance for method chaining.
     */
    public fun metadata(metadata: Metadata): TaskArtifactUpdateEventBuilder {
        this.metadata = metadata
        return this
    }

    /**
     * Builds a `TaskArtifactUpdateEvent` instance with the configured parameters.
     *
     * @return A newly created `TaskArtifactUpdateEvent`.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): TaskArtifactUpdateEvent {
        requireNotNull(id) { "Task ID is required" }
        requireNotNull(artifact) { "Artifact is required" }
        return TaskArtifactUpdateEvent(
            id = id!!,
            artifact = artifact!!,
            metadata = metadata,
        )
    }
}

/**
 * Creates a new TaskArtifactUpdateEvent using the DSL builder.
 *
 * @param init The lambda to configure the TaskArtifactUpdateEvent.
 * @return A new TaskArtifactUpdateEvent instance.
 */
public fun TaskArtifactUpdateEvent.Companion.create(
    init: TaskArtifactUpdateEventBuilder.() -> Unit,
): TaskArtifactUpdateEvent = TaskArtifactUpdateEventBuilder().apply(init).build()
