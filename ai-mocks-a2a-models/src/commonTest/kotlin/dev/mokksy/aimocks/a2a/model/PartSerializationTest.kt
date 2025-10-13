package dev.mokksy.aimocks.a2a.model

import dev.mokksy.aimocks.a2a.model.serializers.PartSerializer
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test

/**
 * Comprehensive tests for Part polymorphic serialization using the custom PartSerializer.
 *
 * These tests verify that the PartSerializer correctly handles:
 * - Individual Part implementations (TextPart, FilePart, DataPart)
 * - Lists of Parts with mixed types
 * - Proper "kind" discriminator usage
 */
internal class PartSerializationTest {
    private val partSerializer = PartSerializer()

    @Test
    fun `should serialize and deserialize TextPart with PartSerializer`() {
        val textPart = TextPart(text = "Hello world")

        val json = Json.encodeToString(partSerializer, textPart)
        val deserializedPart = Json.decodeFromString(partSerializer, json)

        deserializedPart.shouldBeInstanceOf<TextPart>()
        deserializedPart.text shouldBe "Hello world"
    }

    @Test
    fun `should serialize and deserialize FilePart with PartSerializer`() {
        val filePart =
            FilePart(
                file =
                    FileContent(
                        uri = "https://example.com/file.txt",
                        mimeType = "text/plain",
                    ),
            )

        val json = Json.encodeToString(partSerializer, filePart)
        val deserializedPart = Json.decodeFromString(partSerializer, json)

        deserializedPart.shouldBeInstanceOf<FilePart>()
        deserializedPart.file.uri shouldBe "https://example.com/file.txt"
        deserializedPart.file.mimeType shouldBe "text/plain"
    }

    @Test
    fun `should serialize and deserialize DataPart with PartSerializer`() {
        val dataPart =
            DataPart(
                data = Data.of("key" to "value", "number" to 42),
            )

        val json = Json.encodeToString(partSerializer, dataPart)
        val deserializedPart = Json.decodeFromString(partSerializer, json)

        deserializedPart.shouldBeInstanceOf<DataPart>()
        deserializedPart.data["key"] shouldBe "value"
        deserializedPart.data["number"] shouldBe 42
    }

    @Test
    fun `should serialize and deserialize list of Parts with PartSerializer`() {
        val parts =
            listOf(
                TextPart(text = "Hello"),
                FilePart(
                    file =
                        FileContent(
                            uri = "https://example.com/file.txt",
                            mimeType = "text/plain",
                        ),
                ),
                DataPart(data = Data.of("key" to "value")),
            )

        val listSerializer = ListSerializer(partSerializer)
        val json = Json.encodeToString(listSerializer, parts)
        val deserializedParts = Json.decodeFromString(listSerializer, json)

        deserializedParts.size shouldBe 3
        deserializedParts[0].shouldBeInstanceOf<TextPart>()
        deserializedParts[1].shouldBeInstanceOf<FilePart>()
        deserializedParts[2].shouldBeInstanceOf<DataPart>()

        (deserializedParts[0] as TextPart).text shouldBe "Hello"
        (deserializedParts[1] as FilePart).file.uri shouldBe "https://example.com/file.txt"
        (deserializedParts[2] as DataPart).data["key"] shouldBe "value"
    }

    @Test
    fun `should handle JSON with kind discriminator correctly`() {
        val json =
            """{"kind":"text","text":"write a long paper describing the attached pictures"}"""

        val deserializedPart = Json.decodeFromString(partSerializer, json)

        deserializedPart.shouldBeInstanceOf<TextPart>()
        deserializedPart.text shouldBe
            "write a long paper describing the attached pictures"
    }
}
