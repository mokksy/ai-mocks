package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder for creating [TextPart] instances with a fluent DSL.
 *
 * Example usage:
 * ```kotlin
 * val textPart = TextPartBuilder().apply {
 *     text = "Hello, world!"
 *     metadata {
 *         // configure metadata
 *     }
 * }.create()
 * ```
 */
public class TextPartBuilder {
    public var text: String? = null
    public var metadata: Metadata? = null

    /**
     * Sets the text content.
     *
     * @param text The text content.
     * @return This builder for chaining.
     */
    public fun text(text: String): TextPartBuilder =
        apply {
            this.text = text
        }

    /**
     * Sets the metadata.
     *
     * @param metadata The metadata.
     * @return This builder for chaining.
     */
    public fun metadata(metadata: Metadata): TextPartBuilder =
        apply {
            this.metadata = metadata
        }

    /**
     * Builds a [TextPart] instance with the configured properties.
     *
     * @param validate Whether to validate required properties
     * @return A new [TextPart] instance
     * @throws IllegalStateException if validation fails
     */
    public fun build(validate: Boolean = false): TextPart {
        if (validate) {
            requireNotNull(text) { "Text is required for TextPart" }
        }

        return TextPart(
            text = text ?: "",
            metadata = metadata,
        )
    }
}

/**
 * Creates a new TextPart using the DSL builder.
 *
 * Example:
 * ```kotlin
 * val textPart = textPart {
 *     text = "Hello, world!"
 * }
 * ```
 *
 * @param init The lambda to configure the text part
 * @return A new TextPart instance
 */
public inline fun textPart(init: TextPartBuilder.() -> Unit): TextPart =
    TextPartBuilder().apply(init).build()

/**
 * Creates a new TextPart using the Java-friendly Consumer.
 *
 * @param init The consumer to configure the text part
 * @return A new TextPart instance
 */
public fun textPart(init: Consumer<TextPartBuilder>): TextPart {
    val builder = TextPartBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new TextPart using the DSL builder.
 *
 * @param init The lambda to configure the text part
 * @return A new TextPart instance
 */
public fun TextPart.Companion.create(init: TextPartBuilder.() -> Unit): TextPart =
    TextPartBuilder().apply(init).build()

/**
 * Creates a new TextPart using the Java-friendly Consumer.
 *
 * @param init The consumer to configure the text part
 * @return A new TextPart instance
 */
public fun TextPart.Companion.create(init: Consumer<TextPartBuilder>): TextPart {
    val builder = TextPartBuilder()
    init.accept(builder)
    return builder.build()
}
