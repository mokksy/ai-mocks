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
 *     .apply {
 *         role = Message.Role.user
 *         parts.add(textPartBuilder.create())
 *     }
 *     .create()
 * ```
 */
public class MessageBuilder {
    public var role: Message.Role? = null
    public var parts: MutableList<Part> = mutableListOf()
    public var metadata: Metadata? = null

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
     * Adds a part to the message.
     *
     * @param part The part to add to the message.
     * @return This builder instance for method chaining.
     */
    public fun addPart(part: Part): MessageBuilder {
        this.parts.add(part)
        return this
    }

    public fun textPart(block: TextPartBuilder.() -> Unit): TextPart =
        TextPartBuilder().apply(block).build()

    public fun filePart(block: FilePartBuilder.() -> Unit): FilePart =
        FilePartBuilder().apply(block).build()

    public fun dataPart(block: DataPartBuilder.() -> Unit): DataPart =
        DataPartBuilder().apply(block).build()

    /**
     * Builds a [Message] instance with the configured parameters.
     *
     * @return A new [Message] instance.
     * @throws IllegalArgumentException If required, parameters are missing.
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
