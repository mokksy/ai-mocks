package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class FilePartBuilderTest {
    @Test
    fun `should build FilePart with required parameters`() {
        // given
        val fileContent = FileContent(
            name = "example.txt",
            mimeType = "text/plain",
            bytes = "Hello World".encodeToByteArray()
        )

        // when
        val filePart = FilePartBuilder()
            .file(fileContent)
            .build()

        // then
        filePart.file shouldBe fileContent
        filePart.metadata shouldBe null
    }

    @Test
    fun `should build FilePart with all parameters`() {
        // given
        val fileContent = FileContent(
            name = "example.txt",
            mimeType = "text/plain",
            bytes = "Hello World".encodeToByteArray()
        )
        val metadata = Metadata.of("metaKey" to "metaValue")

        // when
        val filePart = FilePartBuilder()
            .file(fileContent)
            .metadata(metadata)
            .build()

        // then
        filePart.file shouldBe fileContent
        filePart.metadata shouldBe metadata
    }

    @Test
    fun `should build FilePart using file DSL block`() {
        // given
        val bytes = "Hello World".encodeToByteArray()

        // when
        val filePart = FilePartBuilder()
            .apply {
                file {
                    name("example.txt")
                    mimeType("text/plain")
                    bytes(bytes)
                }
            }
            .build()

        // then
        filePart.file.name shouldBe "example.txt"
        filePart.file.mimeType shouldBe "text/plain"
        filePart.file.bytes shouldBe bytes
        filePart.file.uri shouldBe null
        filePart.metadata shouldBe null
    }

    @Test
    fun `should fail validation when file is not provided`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            FilePartBuilder().build(validate = true)
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // given
        val bytes = "Hello World".encodeToByteArray()

        // when
        val filePart = filePart {
            file {
                name("example.txt")
                mimeType("text/plain")
                bytes(bytes)
            }
        }

        // then
        filePart.file.name shouldBe "example.txt"
        filePart.file.mimeType shouldBe "text/plain"
        filePart.file.bytes shouldBe bytes
        filePart.file.uri shouldBe null
        filePart.metadata shouldBe null
    }

    @Test
    fun `should build using companion object create function`() {
        // given
        val bytes = "Hello World".encodeToByteArray()

        // when
        val filePart = FilePart.create {
            file {
                name("example.txt")
                mimeType("text/plain")
                bytes(bytes)
            }
            metadata(Metadata.of("metaKey" to "metaValue"))
        }

        // then
        filePart.file.name shouldBe "example.txt"
        filePart.file.mimeType shouldBe "text/plain"
        filePart.file.bytes shouldBe bytes
        filePart.file.uri shouldBe null
        filePart.metadata shouldBe Metadata.of("metaKey" to "metaValue")
    }
}
