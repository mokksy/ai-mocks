package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Artifact
    @JvmOverloads
    constructor(
        @SerialName("artifactId")
        val artifactId: String? = null,
        @SerialName("name")
        val name: String? = null,
        @SerialName("description")
        val description: String? = null,
        @SerialName("parts")
        val parts: List<Part>,
        @SerialName("index")
        val index: Long? = null,
        @SerialName("append")
        val append: Boolean? = null,
        @SerialName("lastChunk")
        val lastChunk: Boolean? = null,
        @SerialName("metadata")
        val metadata: Metadata? = null,
    ) {
        /**
         * Java-friendly constructor.
         */
        @JvmOverloads
        public constructor(
            artifactId: String? = null,
            name: String? = null,
            description: String? = null,
            part: Part,
            index: Long? = null,
            append: Boolean? = null,
            lastChunk: Boolean? = null,
            metadata: Metadata? = null,
        ) : this(
            artifactId = artifactId,
            name = name,
            description = description,
            parts = listOf(part),
            index = index,
            append = append,
            lastChunk = lastChunk,
            metadata = metadata,
        )

        /**
         * Java-friendly constructor.
         */
        public constructor(
            artifactId: String?,
            name: String,
            part: Part,
            append: Boolean,
            lastChunk: Boolean,
        ) : this(
            artifactId = artifactId,
            name = name,
            description = null,
            parts = listOf(part),
            index = null,
            append = append,
            lastChunk = lastChunk,
            metadata = null,
        )

        public companion object
    }
