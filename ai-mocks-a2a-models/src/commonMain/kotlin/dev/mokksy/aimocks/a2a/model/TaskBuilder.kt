package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder class for creating [Task] instances.
 *
 * This builder provides a fluent API for creating Task objects,
 * making it easier to configure tasks.
 *
 * Example usage:
 * ```
 * val task = Task.create {
 *     id("task-456")
 *     contextId("ctx-789")
 *     status {
 *         state = TaskState.working
 *         timestamp = System.currentTimeMillis()
 *     }
 *     artifact {
 *         name = "response"
 *         textPart("I can help you with that!")
 *     }
 * }
 * ```
 */
@Suppress("TooManyFunctions")
public class TaskBuilder {
    public var id: String? = null
    public var contextId: String? = null
    public var status: TaskStatus? = null
    public val artifacts: MutableList<Artifact> = mutableListOf()
    public var metadata: Metadata? = null
    public val history: MutableList<Message> = mutableListOf()

    /**
     * Sets the ID of the task.
     *
     * @param id The unique identifier for the task.
     * @return This builder instance for method chaining.
     */
    public fun id(id: String): TaskBuilder =
        apply {
            this.id = id
        }

    public fun contextId(contextId: String): TaskBuilder =
        apply {
            this.contextId = contextId
        }

    /**
     * Sets the status of the task.
     *
     * @param status The status of the task.
     * @return This builder instance for method chaining.
     */
    public fun status(status: TaskStatus): TaskBuilder =
        apply {
            this.status = status
        }

    /**
     * Configures the status using a lambda with receiver.
     *
     * @param block The lambda to configure the status.
     * @return This builder instance for method chaining.
     */
    public fun status(block: TaskStatusBuilder.() -> Unit): TaskBuilder =
        apply {
            status = TaskStatusBuilder().apply(block).build()
        }

    /**
     * Configures the status using a Java-friendly Consumer.
     *
     * @param block The consumer to configure the status.
     * @return This builder instance for method chaining.
     */
    public fun status(block: Consumer<TaskStatusBuilder>): TaskBuilder =
        apply {
            val builder = TaskStatusBuilder()
            block.accept(builder)
            status = builder.build()
        }

    /**
     * Sets the artifacts of the task.
     *
     * @param artifacts The list of artifacts associated with the task.
     * @return This builder instance for method chaining.
     */
    public fun artifacts(artifacts: List<Artifact>): TaskBuilder =
        apply {
            this.artifacts.clear()
            this.artifacts.addAll(artifacts)
        }

    /**
     * Adds an artifact to the task.
     *
     * @param artifact The artifact to add to the task.
     * @return This builder instance for method chaining.
     */
    public fun addArtifact(artifact: Artifact): TaskBuilder =
        apply {
            this.artifacts.add(artifact)
        }

    public fun addToHistory(message: Message): TaskBuilder =
        apply {
            this.history.add(message)
        }

    /**
     * Creates an artifact using the provided configuration block and adds it to the task.
     *
     * @param block The lambda to configure the artifact.
     * @return The created artifact.
     */
    public fun artifact(block: ArtifactBuilder.() -> Unit): Artifact =
        ArtifactBuilder().apply(block).build()

    /**
     * Creates an artifact using the provided Java-friendly Consumer and adds it to the task.
     *
     * @param block The consumer to configure the artifact.
     * @return The created artifact.
     */
    public fun artifact(block: Consumer<ArtifactBuilder>): Artifact {
        val builder = ArtifactBuilder()
        block.accept(builder)
        return builder.build()
    }

    /**
     * Sets the metadata of the task.
     *
     * @param metadata The metadata associated with the task.
     * @return This builder instance for method chaining.
     */
    public fun metadata(metadata: Metadata): TaskBuilder =
        apply {
            this.metadata = metadata
        }

    /**
     * Builds a [Task] instance with the configured parameters.
     *
     * @param validate Whether to validate required parameters.
     * @return A new [Task] instance.
     * @throws IllegalArgumentException If validate is true and required parameters are missing.
     */
    public fun build(validate: Boolean = true): Task {
        if (validate) {
            requireNotNull(id) { "Task ID is required" }
            requireNotNull(contextId) { "Context ID status is required" }
            requireNotNull(status) { "Task status is required" }
        }
        return Task(
            id = requireNotNull(id),
            contextId = requireNotNull(contextId),
            status = requireNotNull(status),
            artifacts = artifacts.ifEmpty { null },
            metadata = metadata,
            history = history.ifEmpty { null },
        )
    }
}

/**
 * Top-level DSL function for creating [Task].
 *
 * @param init The lambda to configure the task.
 * @return A new [Task] instance.
 */
public inline fun task(init: TaskBuilder.() -> Unit): Task = TaskBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [Task].
 *
 * @param init The consumer to configure the task.
 * @return A new [Task] instance.
 */
public fun task(init: Consumer<TaskBuilder>): Task {
    val builder = TaskBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new instance of a Task using the provided configuration block.
 *
 * @param block A configuration block for building a Task instance using the TaskBuilder.
 * @return A newly created Task instance.
 */
public fun Task.Companion.create(block: TaskBuilder.() -> Unit): Task =
    TaskBuilder().apply(block).build()

/**
 * Creates a new instance of a Task using the provided Java-friendly Consumer.
 *
 * @param block A consumer for building a Task instance using the TaskBuilder.
 * @return A newly created Task instance.
 */
public fun Task.Companion.create(block: Consumer<TaskBuilder>): Task {
    val builder = TaskBuilder()
    block.accept(builder)
    return builder.build()
}
