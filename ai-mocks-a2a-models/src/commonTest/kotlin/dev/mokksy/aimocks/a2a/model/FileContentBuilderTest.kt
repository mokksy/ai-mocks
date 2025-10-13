package dev.mokksy.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class FileContentBuilderTest {
    @Test
    fun `should build FileContent with bytes`() {
        // given
        val bytes = "Hello World".encodeToByteArray()

        // when
        val fileContent =
            FileContentBuilder()
                .name("example.txt")
                .mimeType("text/plain")
                .bytes(bytes)
                .build()

        // then
        fileContent.name shouldBe "example.txt"
        fileContent.mimeType shouldBe "text/plain"
        fileContent.bytes shouldBe bytes
        fileContent.uri shouldBe null
    }

    @Test
    fun `should build FileContent with uri`() {
        // when
        val fileContent =
            FileContentBuilder()
                .name("example.txt")
                .mimeType("text/plain")
                .uri("https://example.com/file.txt")
                .build()

        // then
        fileContent.name shouldBe "example.txt"
        fileContent.mimeType shouldBe "text/plain"
        fileContent.bytes shouldBe null
        fileContent.uri shouldBe "https://example.com/file.txt"
    }

    @Test
    fun `should build FileContent with minimal properties`() {
        // given
        val bytes = "Hello World".encodeToByteArray()

        // when
        val fileContent =
            FileContentBuilder()
                .bytes(bytes)
                .build()

        // then
        fileContent.name shouldBe null
        fileContent.mimeType shouldBe null
        fileContent.bytes shouldBe bytes
        fileContent.uri shouldBe null
    }

    @Test
    fun `should fail validation when neither bytes nor uri is provided`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            FileContentBuilder().build(validate = true)
        }
    }

    @Test
    fun `should fail validation when both bytes and uri are provided`() {
        // given
        val bytes = "Hello World".encodeToByteArray()

        // when/then
        assertFailsWith<IllegalArgumentException> {
            FileContentBuilder()
                .bytes(bytes)
                .uri("https://example.com/file.txt")
                .build(validate = true)
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // given
        val bytes = "Hello World".encodeToByteArray()

        // when
        val fileContent =
            fileContent {
                name("example.txt")
                mimeType("text/plain")
                bytes(bytes)
            }

        // then
        fileContent.name shouldBe "example.txt"
        fileContent.mimeType shouldBe "text/plain"
        fileContent.bytes shouldBe bytes
        fileContent.uri shouldBe null
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val fileContent =
            FileContent.create {
                name("example.txt")
                mimeType("text/plain")
                uri("https://example.com/file.txt")
            }

        // then
        fileContent.name shouldBe "example.txt"
        fileContent.mimeType shouldBe "text/plain"
        fileContent.bytes shouldBe null
        fileContent.uri shouldBe "https://example.com/file.txt"
    }
}
