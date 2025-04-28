package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder class for creating [TaskArtifactUpdateEvent] instances.
 *
 * This builder provides a fluent API for creating TaskArtifactUpdateEvent objects,
 * making it easier to configure task artifact update events.
 */
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
    public fun id(id: TaskId): TaskArtifactUpdateEventBuilder =
        apply {
            this.id = id
        }

    /**
     * Sets the artifact for the event.
     *
     * @param artifact The artifact data.
     * @return This builder instance for method chaining.
     */
    public fun artifact(artifact: Artifact): TaskArtifactUpdateEventBuilder =
        apply {
            this.artifact = artifact
        }

    /**
     * Configures the artifact using a lambda with receiver.
     *
     * @param block The lambda to configure the artifact.
     * @return This builder instance for method chaining.
     */
    public fun artifact(block: ArtifactBuilder.() -> Unit): TaskArtifactUpdateEventBuilder =
        apply {
            artifact = ArtifactBuilder().apply(block).build()
        }

    /**
     * Configures the artifact using a Java-friendly Consumer.
     *
     * @param block The consumer to configure the artifact.
     * @return This builder instance for method chaining.
     */
    public fun artifact(block: Consumer<ArtifactBuilder>): TaskArtifactUpdateEventBuilder =
        apply {
            val builder = ArtifactBuilder()
            block.accept(builder)
            artifact = builder.build()
        }

    /**
     * Sets the optional metadata for the event.
     *
     * @param metadata The metadata associated with the task artifact update.
     * @return This builder instance for method chaining.
     */
    public fun metadata(metadata: Metadata): TaskArtifactUpdateEventBuilder =
        apply {
            this.metadata = metadata
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

/**
 * Creates a new TaskArtifactUpdateEvent using the Java-friendly Consumer.
 *
 * @param init The consumer to configure the TaskArtifactUpdateEvent.
 * @return A new TaskArtifactUpdateEvent instance.
 */
public fun TaskArtifactUpdateEvent.Companion.create(
    init: Consumer<TaskArtifactUpdateEventBuilder>,
): TaskArtifactUpdateEvent {
    val builder = TaskArtifactUpdateEventBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Top-level DSL function for creating [TaskArtifactUpdateEvent].
 *
 * @param init The lambda to configure the TaskArtifactUpdateEvent.
 * @return A new TaskArtifactUpdateEvent instance.
 */
public inline fun taskArtifactUpdateEvent(
    init: TaskArtifactUpdateEventBuilder.() -> Unit,
): TaskArtifactUpdateEvent = TaskArtifactUpdateEventBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [TaskArtifactUpdateEvent].
 *
 * @param init The consumer to configure the TaskArtifactUpdateEvent.
 * @return A new TaskArtifactUpdateEvent instance.
 */
public fun taskArtifactUpdateEvent(
    init: Consumer<TaskArtifactUpdateEventBuilder>,
): TaskArtifactUpdateEvent {
    val builder = TaskArtifactUpdateEventBuilder()
    init.accept(builder)
    return builder.build()
}
