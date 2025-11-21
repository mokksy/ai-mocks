package dev.mokksy.aimocks.a2a.model

import dev.mokksy.aimocks.a2a.model.serializers.ByteArrayAsBase64Serializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the content of a file, either as base64 encoded bytes or a URI.
 *
 * This is a union type that can represent either:
 * - File content provided directly as base64-encoded bytes (via [bytes] property)
 * - File content located at a specific URI (via [uri] property)
 *
 * Either [bytes] or [uri] should be provided, but not both.
 */
@Serializable
public data class FileContent(
    /**
     * An optional name for the file (e.g., "document.pdf").
     */
    @SerialName("name")
    val name: String? = null,

    /**
     * The MIME type of the file (e.g., "application/pdf").
     */
    @SerialName("mimeType")
    val mimeType: String? = null,

    /**
     * The base64-encoded content of the file.
     * Present when representing file content directly.
     */
    @Serializable(with = ByteArrayAsBase64Serializer::class)
    @SerialName("bytes")
    val bytes: ByteArray? = null,

    /**
     * A URL pointing to the file's content.
     * Present when representing file content via URI.
     */
    @SerialName("uri")
    val uri: String? = null,
) {
    public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileContent

        if (name != other.name) return false
        if (mimeType != other.mimeType) return false
        if (uri != other.uri) return false

        return true
    }

    public override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (mimeType?.hashCode() ?: 0)
        result = 31 * result + (uri?.hashCode() ?: 0)
        return result
    }

    public override fun toString(): String =
        "FileContent(" +
            "name=$name, " +
            "uri=$uri, " +
            "mimeType=$mimeType, " +
            "bytes=${if (bytes != null) "${bytes.size} bytes" else "null"}" +
            ")"

    public companion object
}
