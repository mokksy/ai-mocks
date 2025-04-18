package me.kpavlov.aimocks.a2a.model

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
 * }.build()
 * ```
 */
public class TextPartBuilder {
    public var text: String? = null
    public var metadata: Metadata? = null

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
