package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("file")
public data class FilePart
    @JvmOverloads
    constructor(
        @SerialName("file")
        val file: FileContent,
        @SerialName("metadata")
        val metadata: Metadata? = null,
    ) : Part {
        public companion object
    }
