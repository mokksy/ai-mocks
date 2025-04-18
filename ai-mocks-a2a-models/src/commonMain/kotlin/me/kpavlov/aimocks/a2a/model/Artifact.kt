package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Artifact(
    @SerialName("name")
    val name: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("parts")
    val parts: List<Part>,
    @SerialName("index")
    @EncodeDefault
    val index: Long = 0,
    @SerialName("append")
    val append: Boolean? = null,
    @SerialName("lastChunk")
    val lastChunk: Boolean? = null,
    @SerialName("metadata")
    val metadata: Metadata? = null,
) {
    public companion object
}
