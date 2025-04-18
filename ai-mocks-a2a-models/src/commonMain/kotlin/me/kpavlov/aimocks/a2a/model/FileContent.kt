package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.kpavlov.aimocks.a2a.model.serializers.ByteArrayAsBase64Serializer

/**
 * Represents the content of a file, either as base64 encoded bytes or a URI.
 *
 * Either 'bytes' or 'uri' should be provided, but not both.
 */
@Serializable
public data class FileContent(
    @SerialName("name")
    val name: String? = null,
    @SerialName("mimeType")
    val mimeType: String? = null,
    @Serializable(with = ByteArrayAsBase64Serializer::class)
    @SerialName("bytes")
    val bytes: ByteArray? = null,
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
