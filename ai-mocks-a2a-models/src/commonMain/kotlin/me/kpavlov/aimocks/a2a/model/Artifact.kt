package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Artifact
    @JvmOverloads
    constructor(
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
        /**
         * Java-friendly constructor.
         */
        @JvmOverloads
        public constructor(
            name: String? = null,
            description: String? = null,
            part: Part,
            index: Long = 0,
            append: Boolean? = null,
            lastChunk: Boolean? = null,
            metadata: Metadata? = null,
        ) : this(
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
            name: String,
            part: Part,
            append: Boolean,
            lastChunk: Boolean,
        ) : this(
            name = name,
            description = null,
            parts = listOf(part),
            index = 0,
            append = append,
            lastChunk = lastChunk,
            metadata = null,
        )

        public companion object
    }
