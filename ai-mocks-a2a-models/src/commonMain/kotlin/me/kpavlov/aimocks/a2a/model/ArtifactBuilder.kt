package me.kpavlov.aimocks.a2a.model

/**
 * Builder class for creating [Artifact] instances.
 *
 * This builder provides a fluent API for creating Artifact objects,
 * making it easier to construct complex artifacts with many parameters.
 *
 * Example usage:
 * ```kotlin
 * val artifact = ArtifactBuilder()
 *     .name("example-artifact")
 *     .description("An example artifact")
 *     .addPart(textPartBuilder.build())
 *     .index(1)
 *     .append(true)
 *     .lastChunk(false)
 *     .build()
 * ```
 */
public class ArtifactBuilder {
    private var name: String? = null
    private var description: String? = null
    private var parts: MutableList<Part> = mutableListOf()
    private var index: Long = 0
    private var append: Boolean? = null
    private var lastChunk: Boolean? = null
    private var metadata: Metadata? = null

    /**
     * Sets the name of the artifact.
     *
     * @param name The name of the artifact.
     * @return This builder instance for method chaining.
     */
    public fun name(name: String): ArtifactBuilder {
        this.name = name
        return this
    }

    /**
     * Sets the description of the artifact.
     *
     * @param description The description of the artifact.
     * @return This builder instance for method chaining.
     */
    public fun description(description: String): ArtifactBuilder {
        this.description = description
        return this
    }

    /**
     * Sets the parts of the artifact.
     *
     * @param parts The list of parts that make up the artifact.
     * @return This builder instance for method chaining.
     */
    public fun parts(parts: List<Part>): ArtifactBuilder {
        this.parts = parts.toMutableList()
        return this
    }

    /**
     * Adds a part to the artifact.
     *
     * @param part The part to add to the artifact.
     * @return This builder instance for method chaining.
     */
    public fun addPart(part: Part): ArtifactBuilder {
        this.parts.add(part)
        return this
    }

    /**
     * Sets the index of the artifact.
     *
     * @param index The index of the artifact.
     * @return This builder instance for method chaining.
     */
    public fun index(index: Long): ArtifactBuilder {
        this.index = index
        return this
    }

    /**
     * Sets whether to append the artifact.
     *
     * @param append Whether to append the artifact.
     * @return This builder instance for method chaining.
     */
    public fun append(append: Boolean): ArtifactBuilder {
        this.append = append
        return this
    }

    /**
     * Sets whether this is the last chunk of the artifact.
     *
     * @param lastChunk Whether this is the last chunk of the artifact.
     * @return This builder instance for method chaining.
     */
    public fun lastChunk(lastChunk: Boolean): ArtifactBuilder {
        this.lastChunk = lastChunk
        return this
    }

    /**
     * Sets the metadata of the artifact.
     *
     * @param metadata The metadata associated with the artifact.
     * @return This builder instance for method chaining.
     */
    public fun metadata(metadata: Metadata): ArtifactBuilder {
        this.metadata = metadata
        return this
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
