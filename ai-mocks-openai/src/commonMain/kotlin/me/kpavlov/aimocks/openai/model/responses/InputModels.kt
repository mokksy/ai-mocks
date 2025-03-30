package me.kpavlov.aimocks.openai.model.responses

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Sealed interface representing input content elements in the OpenAI responses API.
 * This is the base interface for all input content types.
 */
@Serializable(with = InputContentSerializer::class)
public sealed interface InputContent {
    /**
     * The type of the input content.
     */
    public val type: String
}

/**
 * Represents a text input to the model.
 *
 * @property type The type of the input item. Always "input_text".
 * @property text The text input to the model.
 */
@Serializable
public data class InputText(
    @SerialName("type") @Required public override val type: String = "input_text",
    @SerialName("text") @Required public val text: String,
) : InputContent {
    public companion object {
        /**
         * Creates a new text input.
         *
         * @param text The text input to the model.
         * @return A new [InputText] instance.
         */
        public fun of(text: String): InputText = InputText(text = text)
    }
}

/**
 * Represents an image input to the model.
 *
 * @property type The type of the input item. Always "input_image".
 * @property detail The detail level of the image. One of "high", "low", or "auto".
 * @property imageUrl The URL of the image or base64 encoded image data.
 * @property fileId The ID of the file to be sent to the model.
 */
@Serializable
public data class InputImage(
    @SerialName("type") @Required public override val type: String = "input_image",
    @SerialName("detail") @Required public val detail: Detail = Detail.AUTO,
    @SerialName("image_url") public val imageUrl: String? = null,
    @SerialName("file_id") public val fileId: String? = null,
) : InputContent {
    /**
     * The detail level of the image.
     */
    @Serializable
    public enum class Detail(
        public val value: String,
    ) {
        @SerialName("high")
        HIGH("high"),

        @SerialName("low")
        LOW("low"),

        @SerialName("auto")
        AUTO("auto"),
    }

    public companion object {
        /**
         * Creates a new image input with a URL.
         *
         * @param imageUrl The URL of the image or base64 encoded image data.
         * @param detail The detail level of the image.
         * @return A new [InputImage] instance.
         */
        public fun ofUrl(
            imageUrl: String,
            detail: Detail = Detail.AUTO,
        ): InputImage = InputImage(detail = detail, imageUrl = imageUrl)

        /**
         * Creates a new image input with a file ID.
         *
         * @param fileId The ID of the file to be sent to the model.
         * @param detail The detail level of the image.
         * @return A new [InputImage] instance.
         */
        public fun ofFileId(
            fileId: String,
            detail: Detail = Detail.AUTO,
        ): InputImage = InputImage(detail = detail, fileId = fileId)
    }
}

/**
 * Represents a file input to the model.
 *
 * @property type The type of the input item. Always "input_file".
 * @property fileId The ID of the file to be sent to the model.
 * @property filename The name of the file to be sent to the model.
 * @property fileData The content of the file to be sent to the model.
 */
@Serializable
public data class InputFile(
    @SerialName("type") @Required public override val type: String = "input_file",
    @SerialName("file_id") public val fileId: String? = null,
    @SerialName("filename") public val filename: String? = null,
    @SerialName("file_data") public val fileData: String? = null,
) : InputContent {
    public companion object {
        /**
         * Creates a new file input with a file ID.
         *
         * @param fileId The ID of the file to be sent to the model.
         * @return A new [InputFile] instance.
         */
        public fun ofFileId(fileId: String): InputFile = InputFile(fileId = fileId)

        /**
         * Creates a new file input with file data.
         *
         * @param filename The name of the file to be sent to the model.
         * @param fileData The content of the file to be sent to the model.
         * @return A new [InputFile] instance.
         */
        public fun ofFileData(
            filename: String,
            fileData: String,
        ): InputFile = InputFile(filename = filename, fileData = fileData)
    }
}

/**
 * Represents an audio input to the model.
 *
 * @property type The type of the input item. Always "input_audio".
 * @property data Base64-encoded audio data.
 * @property format The format of the audio data. Currently supported formats are "mp3" and "wav".
 */
@Serializable
public data class InputAudio(
    @SerialName("type") @Required public override val type: String = "input_audio",
    @SerialName("data") @Required public val `data`: String,
    @SerialName("format") @Required public val format: Format,
) : InputContent {
    /**
     * The format of the audio data.
     */
    @Serializable
    public enum class Format(
        public val value: String,
    ) {
        @SerialName("mp3")
        MP3("mp3"),

        @SerialName("wav")
        WAV("wav"),
    }

    public companion object {
        /**
         * Creates a new audio input.
         *
         * @param data Base64-encoded audio data.
         * @param format The format of the audio data.
         * @return A new [InputAudio] instance.
         */
        public fun of(
            `data`: String,
            format: Format,
        ): InputAudio = InputAudio(`data` = `data`, format = format)

        /**
         * Creates a new MP3 audio input.
         *
         * @param data Base64-encoded audio data.
         * @return A new [InputAudio] instance with MP3 format.
         */
        public fun ofMp3(`data`: String): InputAudio = of(`data` = `data`, format = Format.MP3)

        /**
         * Creates a new WAV audio input.
         *
         * @param data Base64-encoded audio data.
         * @return A new [InputAudio] instance with WAV format.
         */
        public fun ofWav(`data`: String): InputAudio = of(`data` = `data`, format = Format.WAV)
    }
}

/**
 * Serializer for [InputContent] that handles polymorphic serialization based on the "type" field.
 */
public object InputContentSerializer :
    JsonContentPolymorphicSerializer<InputContent>(InputContent::class) {
    public override fun selectDeserializer(element: JsonElement): KSerializer<out InputContent> =
        when (element.jsonObject["type"]?.jsonPrimitive?.content) {
            "input_text" -> InputText.serializer()
            "input_image" -> InputImage.serializer()
            "input_file" -> InputFile.serializer()
            "input_audio" -> InputAudio.serializer()
            else -> throw IllegalArgumentException("Unknown input content type")
        }
}
