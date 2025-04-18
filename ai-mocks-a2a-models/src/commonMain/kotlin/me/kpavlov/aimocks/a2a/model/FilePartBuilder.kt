package me.kpavlov.aimocks.a2a.model

/**
 * Builder for creating [FilePart] instances with a fluent DSL.
 *
 * Example usage:
 * ```kotlin
 * val filePart = FilePartBuilder().apply {
 *     file {
 *         name = "example.txt"
 *         mimeType = "text/plain"
 *         bytes = "Hello World".encodeToByteArray()
 *     }
 * }.create()
 * ```
 */
public class FilePartBuilder {
    private var fileContent: FileContent? = null
    public var metadata: Metadata? = null

    /**
     * Configures the file content using a DSL block.
     *
     * @param block Lambda with receiver to configure file content
     */
    public fun file(block: FileContentBuilder.() -> Unit) {
        fileContent = FileContentBuilder().apply(block).build()
    }

    /**
     * Sets the file content directly.
     *
     * @param fileContent The file content to set
     * @return This builder for chaining
     */
    public fun file(fileContent: FileContent): FilePartBuilder {
        this.fileContent = fileContent
        return this
    }

    /**
     * Builds a [FilePart] instance with the configured properties.
     *
     * @param validate Whether to validate required properties
     * @return A new [FilePart] instance
     * @throws IllegalStateException if validation fails
     */
    public fun build(validate: Boolean = false): FilePart {
        if (validate) {
            requireNotNull(fileContent) { "FileContent is required for FilePart" }
        }

        return FilePart(
            file = fileContent ?: FileContent(),
            metadata = metadata,
        )
    }
}

/**
 * Creates a new FilePart using the DSL builder.
 *
 * Example:
 * ```kotlin
 * val filePart = filePart {
 *     file {
 *         name = "example.txt"
 *         uri = "https://example.com/file.txt"
 *     }
 * }
 * ```
 *
 * @param init The lambda to configure the file part
 * @return A new FilePart instance
 */
public inline fun filePart(init: FilePartBuilder.() -> Unit): FilePart =
    FilePartBuilder().apply(init).build()

public fun FilePart.Companion.create(init: FilePartBuilder.() -> Unit): FilePart =
    FilePartBuilder().apply(init).build()
