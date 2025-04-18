package me.kpavlov.aimocks.a2a.model

/**
 * Builder for creating [FileContent] instances with a fluent DSL.
 *
 * Example usage:
 * ```kotlin
 * val fileContent = FileContentBuilder().apply {
 *     name = "example.txt"
 *     mimeType = "text/plain"
 *     bytes = "Hello World".encodeToByteArray()
 * }.create()
 * ```
 */
public class FileContentBuilder {
    public var name: String? = null
    public var mimeType: String? = null
    public var bytes: ByteArray? = null
    public var uri: String? = null

    /**
     * Builds a [FileContent] instance with the configured properties.
     *
     * @param validate Whether to validate that either bytes or uri is set, but not both
     * @return A new [FileContent] instance
     * @throws IllegalStateException if validation fails
     */
    public fun build(validate: Boolean = false): FileContent {
        if (validate) {
            require(bytes != null || uri != null) {
                "FileContent must have either bytes or uri to be defined"
            }
            require(bytes != null && uri == null) {
                "FileContent must have either bytes or uri, not both"
            }
            require(bytes == null && uri != null) {
                "FileContent must have either bytes or uri, not both"
            }
        }

        return FileContent(
            name = name,
            mimeType = mimeType,
            bytes = bytes,
            uri = uri,
        )
    }
}

/**
 * Creates a new FileContent using the DSL builder.
 *
 * Example:
 * ```kotlin
 * val fileContent = fileContent {
 *     name = "example.txt"
 *     mimeType = "text/plain"
 *     bytes = "Hello World".encodeToByteArray()
 * }
 * ```
 *
 * @param init The lambda to configure the file content
 * @return A new FileContent instance
 */
public inline fun fileContent(init: FileContentBuilder.() -> Unit): FileContent =
    FileContentBuilder().apply(init).build()

public fun FileContent.Companion.create(init: FileContentBuilder.() -> Unit): FileContent =
    FileContentBuilder().apply(init).build()
