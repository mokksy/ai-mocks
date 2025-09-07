package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder class for creating [Message] instances.
 *
 * This builder provides a fluent API for creating Message objects,
 * making it easier to construct complex messages with many parameters.
 *
 * Example usage:
 * ```
 * val message = Message.create {
 *     role = Message.Role.user
 *     textPart("Hello, how can I help you?")
 *     filePart {
 *         name = "document.pdf"
 *         mimeType = "application/pdf"
 *         data = fileBytes
 *     }
 * }
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

    public fun textPart(block: Consumer<TextPartBuilder>): TextPart {
        val builder = TextPartBuilder()
        block.accept(builder)
        return builder.build()
    }

    public fun text(block: () -> String): TextPart = TextPartBuilder().text(block.invoke()).build()

    public fun filePart(block: FilePartBuilder.() -> Unit): FilePart =
        FilePartBuilder().apply(block).build()

    public fun filePart(block: Consumer<FilePartBuilder>): FilePart {
        val builder = FilePartBuilder()
        block.accept(builder)
        return builder.build()
    }

    public fun file(block: FileContentBuilder.() -> Unit): FilePart =
        filePart {
            this.file(block)
        }

    public fun dataPart(block: DataPartBuilder.() -> Unit): DataPart =
        DataPartBuilder().apply(block).build()

    public fun dataPart(block: Consumer<DataPartBuilder>): DataPart {
        val builder = DataPartBuilder()
        block.accept(builder)
        return builder.build()
    }

    public fun data(block: () -> Map<String, Any>): DataPart =
        dataPart {
            data = block.invoke().toMutableMap()
        }

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

/**
 * Top-level DSL function for creating [Message].
 *
 * @param init The lambda to configure the message.
 * @return A new [Message] instance.
 */
public inline fun message(init: MessageBuilder.() -> Unit): Message =
    MessageBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [Message].
 *
 * @param init The consumer to configure the message.
 * @return A new [Message] instance.
 */
public fun message(init: Consumer<MessageBuilder>): Message {
    val builder = MessageBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * DSL extension for [Message.Companion].
 *
 * @param init The lambda to configure the message.
 * @return A new [Message] instance.
 */
public fun Message.Companion.create(init: MessageBuilder.() -> Unit): Message =
    MessageBuilder().apply(init).build()

/**
 * Java-friendly DSL extension for [Message.Companion].
 *
 * @param init The consumer to configure the message.
 * @return A new [Message] instance.
 */
public fun Message.Companion.create(init: Consumer<MessageBuilder>): Message {
    val builder = MessageBuilder()
    init.accept(builder)
    return builder.build()
}
