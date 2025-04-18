package me.kpavlov.aimocks.a2a.model

public class TaskBuilder {
    public var id: String? = null
    public var sessionId: String? = null
    public var status: TaskStatus? = null
    public var artifacts: MutableList<Artifact> = mutableListOf()
    public var metadata: Metadata? = null

    /**
     * Sets the ID of the task.
     *
     * @param id The unique identifier for the task.
     * @return This builder instance for method chaining.
     */
    public fun id(id: String): TaskBuilder {
        this.id = id
        return this
    }

    /**
     * Sets the session ID of the task.
     *
     * @param sessionId The session identifier for the task.
     * @return This builder instance for method chaining.
     */
    public fun sessionId(sessionId: String): TaskBuilder {
        this.sessionId = sessionId
        return this
    }

    /**
     * Sets the status of the task.
     *
     * @param status The status of the task.
     * @return This builder instance for method chaining.
     */
    public fun status(status: TaskStatus): TaskBuilder {
        this.status = status
        return this
    }

    /**
     * Sets the artifacts of the task.
     *
     * @param artifacts The list of artifacts associated with the task.
     * @return This builder instance for method chaining.
     */
    public fun artifacts(artifacts: List<Artifact>): TaskBuilder {
        this.artifacts = artifacts.toMutableList()
        return this
    }

    /**
     * Adds an artifact to the task.
     *
     * @param artifact The artifact to add to the task.
     * @return This builder instance for method chaining.
     */
    public fun addArtifact(artifact: Artifact): TaskBuilder {
        this.artifacts.add(artifact)
        return this
    }

    /**
     * Sets the metadata of the task.
     *
     * @param metadata The metadata associated with the task.
     * @return This builder instance for method chaining.
     */
    public fun metadata(metadata: Metadata): TaskBuilder {
        this.metadata = metadata
        return this
    }

    public fun build(validate: Boolean = false): Task {
        if (validate) {
            requireNotNull(id) { "Task ID is required" }
            requireNotNull(status) { "Task status is required" }
        }
        return Task(
            id = id!!,
            sessionId = sessionId,
            status = status!!,
            artifacts = artifacts,
            metadata = metadata,
        )
    }

    public fun status(block: TaskStatusBuilder.() -> Unit) {
        status = TaskStatusBuilder().apply(block).build()
    }

    public fun artifact(block: ArtifactBuilder.() -> Unit): Artifact =
        ArtifactBuilder().apply(block).build()
}

/**
 * Creates a new instance of a Task using the provided configuration block.
 *
 * @param block A configuration block for building a Task instance using the TaskBuilder.
 * @return A newly created Task instance.
 */
public fun Task.Companion.create(block: TaskBuilder.() -> Unit): Task =
    TaskBuilder().apply(block).build()
