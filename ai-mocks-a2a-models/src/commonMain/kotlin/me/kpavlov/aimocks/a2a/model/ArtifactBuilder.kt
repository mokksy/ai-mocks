package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder class for creating [Artifact] instances.
 *
 * This builder provides a fluent API for creating Artifact objects,
 * making it easier to construct complex artifacts with many parameters.
 *
 * Example usage:
 * ```kotlin
 * val artifact = ArtifactBuilder()
 *     .apply {
 *         name = "example-artifact"
 *         description = "An example artifact"
 *         parts.add(textPartBuilder.create())
 *         index = 1
 *         append = true
 *         lastChunk = false
 *     }
 *     .create()
 * ```
 */
@Suppress("TooManyFunctions")
public class ArtifactBuilder {
    public var name: String? = null
    public var description: String? = null
    public var parts: MutableList<Part> = mutableListOf()
    public var index: Long = 0
    public var append: Boolean? = null
    public var lastChunk: Boolean? = null
    public var metadata: Metadata? = null

    /**
     * Sets the name of the artifact.
     *
     * @param name The name of the artifact.
     * @return This builder instance for method chaining.
     */
    public fun name(name: String): ArtifactBuilder =
        apply {
            this.name = name
        }

    /**
     * Sets the description of the artifact.
     *
     * @param description The description of the artifact.
     * @return This builder instance for method chaining.
     */
    public fun description(description: String): ArtifactBuilder =
        apply {
            this.description = description
        }

    /**
     * Sets the parts of the artifact.
     *
     * @param parts The list of parts that make up the artifact.
     * @return This builder instance for method chaining.
     */
    public fun parts(parts: List<Part>): ArtifactBuilder =
        apply {
            this.parts = parts.toMutableList()
        }

    /**
     * Adds a part to the artifact.
     *
     * @param part The part to add to the artifact.
     * @return This builder instance for method chaining.
     */
    public fun addPart(part: Part): ArtifactBuilder =
        apply {
            this.parts.add(part)
        }

    /**
     * Creates a text part using the provided configuration block.
     *
     * @param block The lambda to configure the text part.
     * @return The created text part.
     */
    public fun textPart(block: TextPartBuilder.() -> Unit): TextPart =
        TextPartBuilder().apply(block).build()

    /**
     * Creates a text part using the provided Java-friendly Consumer.
     *
     * @param block The consumer to configure the text part.
     * @return The created text part.
     */
    public fun textPart(block: Consumer<TextPartBuilder>): TextPart {
        val builder = TextPartBuilder()
        block.accept(builder)
        return builder.build()
    }

    public fun text(block: () -> String): TextPart = TextPartBuilder().text(block.invoke()).build()

    public fun file(block: FileContentBuilder.() -> Unit): FilePart =
        filePart {
            this.file(block)
        }

    public fun data(block: () -> Map<String, Any>): DataPart =
        dataPart {
            data = block.invoke().toMutableMap()
        }

    /**
     * Creates a file part using the provided configuration block.
     *
     * @param block The lambda to configure the file part.
     * @return The created file part.
     */
    public fun filePart(block: FilePartBuilder.() -> Unit): FilePart =
        FilePartBuilder().apply(block).build()

    /**
     * Creates a file part using the provided Java-friendly Consumer.
     *
     * @param block The consumer to configure the file part.
     * @return The created file part.
     */
    public fun filePart(block: Consumer<FilePartBuilder>): FilePart {
        val builder = FilePartBuilder()
        block.accept(builder)
        return builder.build()
    }

    /**
     * Creates a data part using the provided configuration block.
     *
     * @param block The lambda to configure the data part.
     * @return The created data part.
     */
    public fun dataPart(block: DataPartBuilder.() -> Unit): DataPart =
        DataPartBuilder().apply(block).build()

    /**
     * Creates a data part using the provided Java-friendly Consumer.
     *
     * @param block The consumer to configure the data part.
     * @return The created data part.
     */
    public fun dataPart(block: Consumer<DataPartBuilder>): DataPart {
        val builder = DataPartBuilder()
        block.accept(builder)
        return builder.build()
    }

    /**
     * Sets the index of the artifact.
     *
     * @param index The index of the artifact.
     * @return This builder instance for method chaining.
     */
    public fun index(index: Long): ArtifactBuilder =
        apply {
            this.index = index
        }

    /**
     * Sets whether to append the artifact.
     *
     * @param append Whether to append the artifact.
     * @return This builder instance for method chaining.
     */
    public fun append(append: Boolean): ArtifactBuilder =
        apply {
            this.append = append
        }

    /**
     * Sets whether this is the last chunk of the artifact.
     *
     * @param lastChunk Whether this is the last chunk of the artifact.
     * @return This builder instance for method chaining.
     */
    public fun lastChunk(lastChunk: Boolean): ArtifactBuilder =
        apply {
            this.lastChunk = lastChunk
        }

    /**
     * Sets the metadata of the artifact.
     *
     * @param metadata The metadata associated with the artifact.
     * @return This builder instance for method chaining.
     */
    public fun metadata(metadata: Metadata): ArtifactBuilder =
        apply {
            this.metadata = metadata
        }

    /**
     * Builds an [Artifact] instance with the configured parameters.
     *
     * @return A new [Artifact] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): Artifact {
        require(parts.isNotEmpty()) { "At least one part is required" }

        return Artifact(
            name = name,
            description = description,
            parts = parts.toList(),
            index = index,
            append = append,
            lastChunk = lastChunk,
            metadata = metadata,
        )
    }
}

/**
 * Top-level DSL function for creating [Artifact].
 *
 * @param init The lambda to configure the artifact.
 * @return A new [Artifact] instance.
 */
public inline fun artifact(init: ArtifactBuilder.() -> Unit): Artifact =
    ArtifactBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [Artifact].
 *
 * @param init The consumer to configure the artifact.
 * @return A new [Artifact] instance.
 */
public fun artifact(init: Consumer<ArtifactBuilder>): Artifact {
    val builder = ArtifactBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new Artifact using the DSL builder.
 *
 * @param init The lambda to configure the artifact.
 * @return A new Artifact instance.
 */
public fun Artifact.Companion.create(init: ArtifactBuilder.() -> Unit): Artifact =
    ArtifactBuilder().apply(init).build()

/**
 * Creates a new Artifact using the provided Java-friendly Consumer.
 *
 * @param init A consumer for building an Artifact instance using the ArtifactBuilder.
 * @return A newly created Artifact instance.
 */
public fun Artifact.Companion.create(init: Consumer<ArtifactBuilder>): Artifact {
    val builder = ArtifactBuilder()
    init.accept(builder)
    return builder.build()
}
