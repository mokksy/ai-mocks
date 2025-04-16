package me.kpavlov.aimocks.a2a.model

/**
 * Builder class for creating [Message] instances.
 *
 * This builder provides a fluent API for creating Message objects,
 * making it easier to construct complex messages with many parameters.
 *
 * Example usage:
 * ```kotlin
 * val message = MessageBuilder()
 *     .role(Message.Role.user)
 *     .addPart(textPartBuilder.build())
 *     .build()
 * ```
 */
public class MessageBuilder {
    private var role: Message.Role? = null
    private var parts: MutableList<Part> = mutableListOf()
    private var metadata: Metadata? = null

    /**
     * Sets the role of the message.
     *
     * @param role The role of the message sender (user or agent).
     * @return This builder instance for method chaining.
     */
    public fun role(role: Message.Role): MessageBuilder {
        this.role = role
        return this
    }

    /**
     * Sets the parts of the message.
     *
     * @param parts The list of parts that make up the message.
     * @return This builder instance for method chaining.
     */
    public fun parts(parts: List<Part>): MessageBuilder {
        this.parts = parts.toMutableList()
        return this
    }

    /**
     * Adds a part to the message.
     *
     * @param part The part to add to the message.
     * @return This builder instance for method chaining.
     */
    public fun addPart(part: Part): MessageBuilder {
        this.parts.add(part)
        return this
    }

    /**
     * Sets the metadata of the message.
     *
     * @param metadata The metadata associated with the message.
     * @return This builder instance for method chaining.
     */
    public fun metadata(metadata: Metadata): MessageBuilder {
        this.metadata = metadata
        return this
    }

    /**
     * Builds a [Message] instance with the configured parameters.
     *
     * @return A new [Message] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): Message {
        requireNotNull(role) { "Role is required" }
        require(parts.isNotEmpty()) { "At least one part is required" }

        return Message(
            role = role!!,
            parts = parts.toList(),
            metadata = metadata,
        )
    }
}
