package me.kpavlov.aimocks.openai.model.responses

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

internal class InputModelsTest {
    private val jsonParser =
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

    @Test
    fun `Should deserialize InputText`() {
        val json =
            """
            {
              "type": "input_text",
              "text": "What is the capital of France?"
            }
            """.trimIndent()

        val inputText = jsonParser.decodeFromString<InputContent>(json)

        inputText.shouldBeInstanceOf<InputText>()
        inputText.type shouldBe "input_text"
        inputText.text shouldBe "What is the capital of France?"
    }

    @Test
    fun `Should deserialize InputImage with URL`() {
        val json =
            """
            {
              "type": "input_image",
              "detail": "high",
              "image_url": "https://example.com/image.jpg"
            }
            """.trimIndent()

        val inputImage = jsonParser.decodeFromString<InputContent>(json)

        inputImage.shouldBeInstanceOf<InputImage>()
        inputImage.type shouldBe "input_image"
        inputImage.detail shouldBe InputImage.Detail.HIGH
        inputImage.imageUrl shouldBe "https://example.com/image.jpg"
    }

    @Test
    fun `Should deserialize InputImage with file ID`() {
        val json =
            """
            {
              "type": "input_image",
              "detail": "low",
              "file_id": "file-123"
            }
            """.trimIndent()

        val inputImage = jsonParser.decodeFromString<InputContent>(json)

        inputImage.shouldBeInstanceOf<InputImage>()
        inputImage.type shouldBe "input_image"
        inputImage.detail shouldBe InputImage.Detail.LOW
        inputImage.fileId shouldBe "file-123"
    }

    @Test
    fun `Should deserialize InputFile with file ID`() {
        val json =
            """
            {
              "type": "input_file",
              "file_id": "file-123"
            }
            """.trimIndent()

        val inputFile = jsonParser.decodeFromString<InputContent>(json)

        inputFile.shouldBeInstanceOf<InputFile>()
        inputFile.type shouldBe "input_file"
        inputFile.fileId shouldBe "file-123"
    }

    @Test
    fun `Should deserialize InputFile with file data`() {
        val json =
            """
            {
              "type": "input_file",
              "filename": "document.txt",
              "file_data": "SGVsbG8gV29ybGQh"
            }
            """.trimIndent()

        val inputFile = jsonParser.decodeFromString<InputContent>(json)

        inputFile.shouldBeInstanceOf<InputFile>()
        inputFile.type shouldBe "input_file"
        inputFile.filename shouldBe "document.txt"
        inputFile.fileData shouldBe "SGVsbG8gV29ybGQh"
    }

    @Test
    fun `Should deserialize InputAudio`() {
        val json =
            """
            {
              "type": "input_audio",
              "data": "SGVsbG8gV29ybGQh",
              "format": "mp3"
            }
            """.trimIndent()

        val inputAudio = jsonParser.decodeFromString<InputContent>(json)

        inputAudio.shouldBeInstanceOf<InputAudio>()
        inputAudio.type shouldBe "input_audio"
        inputAudio.data shouldBe "SGVsbG8gV29ybGQh"
        inputAudio.format shouldBe InputAudio.Format.MP3
    }

    @Test
    fun `Should deserialize InputMessageResource`() {
        val json =
            """
            {
              "role": "user",
              "content": [
                {
                  "type": "input_text",
                  "text": "What is the capital of France?"
                }
              ],
              "id": "msg_123",
              "type": "message",
              "status": "completed"
            }
            """.trimIndent()

        val message = jsonParser.decodeFromString<InputMessageResource>(json)

        message.role shouldBe InputMessageResource.Role.USER
        message.content.size shouldBe 1
        message.content[0].shouldBeInstanceOf<InputText>()
        (message.content[0] as InputText).text shouldBe "What is the capital of France?"
        message.id shouldBe "msg_123"
        message.type shouldBe InputMessageResource.Type.MESSAGE
        message.status shouldBe InputMessageResource.Status.COMPLETED
    }

    @Test
    fun `Should create InputText using factory method`() {
        val inputText = InputText.of("Hello World")

        inputText.type shouldBe "input_text"
        inputText.text shouldBe "Hello World"
    }

    @Test
    fun `Should create InputImage using factory methods`() {
        val inputImageUrl =
            InputImage.ofUrl(
                "https://example.com/image.jpg",
                InputImage.Detail.HIGH,
            )

        inputImageUrl.type shouldBe "input_image"
        inputImageUrl.detail shouldBe InputImage.Detail.HIGH
        inputImageUrl.imageUrl shouldBe "https://example.com/image.jpg"

        val inputImageFileId = InputImage.ofFileId("file-123", InputImage.Detail.LOW)

        inputImageFileId.type shouldBe "input_image"
        inputImageFileId.detail shouldBe InputImage.Detail.LOW
        inputImageFileId.fileId shouldBe "file-123"
    }

    @Test
    fun `Should create InputFile using factory methods`() {
        val inputFileId = InputFile.ofFileId("file-123")

        inputFileId.type shouldBe "input_file"
        inputFileId.fileId shouldBe "file-123"

        val inputFileData = InputFile.ofFileData("document.txt", "SGVsbG8gV29ybGQh")

        inputFileData.type shouldBe "input_file"
        inputFileData.filename shouldBe "document.txt"
        inputFileData.fileData shouldBe "SGVsbG8gV29ybGQh"
    }

    @Test
    fun `Should create InputAudio using factory methods`() {
        val inputAudioMp3 = InputAudio.ofMp3("SGVsbG8gV29ybGQh")

        inputAudioMp3.type shouldBe "input_audio"
        inputAudioMp3.data shouldBe "SGVsbG8gV29ybGQh"
        inputAudioMp3.format shouldBe InputAudio.Format.MP3

        val inputAudioWav = InputAudio.ofWav("SGVsbG8gV29ybGQh")

        inputAudioWav.type shouldBe "input_audio"
        inputAudioWav.data shouldBe "SGVsbG8gV29ybGQh"
        inputAudioWav.format shouldBe InputAudio.Format.WAV
    }
}
