package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

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
     * Sets the name of the file.
     *
     * @param name The name of the file
     * @return This builder for chaining
     */
    public fun name(name: String): FileContentBuilder =
        apply {
            this.name = name
        }

    /**
     * Sets the MIME type of the file.
     *
     * @param mimeType The MIME type of the file
     * @return This builder for chaining
     */
    public fun mimeType(mimeType: String): FileContentBuilder =
        apply {
            this.mimeType = mimeType
        }

    /**
     * Sets the bytes of the file.
     *
     * @param bytes The bytes of the file
     * @return This builder for chaining
     */
    public fun bytes(bytes: ByteArray): FileContentBuilder =
        apply {
            this.bytes = bytes
        }

    /**
     * Sets the URI of the file.
     *
     * @param uri The URI of the file
     * @return This builder for chaining
     */
    public fun uri(uri: String): FileContentBuilder =
        apply {
            this.uri = uri
        }

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

/**
 * Creates a new FileContent using the Java-friendly Consumer.
 *
 * @param init The consumer to configure the file content
 * @return A new FileContent instance
 */
public fun fileContent(init: Consumer<FileContentBuilder>): FileContent {
    val builder = FileContentBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new FileContent using the DSL builder.
 *
 * @param init The lambda to configure the file content
 * @return A new FileContent instance
 */
public fun FileContent.Companion.create(init: FileContentBuilder.() -> Unit): FileContent =
    FileContentBuilder().apply(init).build()

/**
 * Creates a new FileContent using the Java-friendly Consumer.
 *
 * @param init The consumer to configure the file content
 * @return A new FileContent instance
 */
public fun FileContent.Companion.create(init: Consumer<FileContentBuilder>): FileContent {
    val builder = FileContentBuilder()
    init.accept(builder)
    return builder.build()
}
