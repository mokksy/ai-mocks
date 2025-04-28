package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class ArtifactBuilderTest {
    @Test
    fun `should build Artifact with required parameters`() {
        // given
        val textPart = TextPart(text = "Sample text content")

        // when
        val artifact = ArtifactBuilder()
            .addPart(textPart)
            .build()

        // then
        artifact.name shouldBe null
        artifact.description shouldBe null
        artifact.parts shouldBe listOf(textPart)
        artifact.index shouldBe 0
        artifact.append shouldBe null
        artifact.lastChunk shouldBe null
        artifact.metadata shouldBe null
    }

    @Test
    fun `should build Artifact with all parameters`() {
        // given
        val textPart = TextPart(text = "Sample text content")
        val metadata = Metadata.of("metaKey" to "metaValue")

        // when
        val artifact = ArtifactBuilder()
            .name("artifact-name")
            .description("artifact description")
            .addPart(textPart)
            .index(1)
            .append(true)
            .lastChunk(false)
            .metadata(metadata)
            .build()

        // then
        artifact.name shouldBe "artifact-name"
        artifact.description shouldBe "artifact description"
        artifact.parts shouldBe listOf(textPart)
        artifact.index shouldBe 1
        artifact.append shouldBe true
        artifact.lastChunk shouldBe false
        artifact.metadata shouldBe metadata
    }

    @Test
    fun `should set parts list`() {
        // given
        val textPart1 = TextPart(text = "First text content")
        val textPart2 = TextPart(text = "Second text content")
        val parts = listOf(textPart1, textPart2)

        // when
        val artifact = ArtifactBuilder()
            .parts(parts)
            .build()

        // then
        artifact.parts shouldBe parts
    }

    @Test
    fun `should create and add text part`() {
        // when
        val artifact = ArtifactBuilder()
            .apply {
                addPart(textPart {
                    text = "Sample text content"
                })
            }
            .build()

        // then
        artifact.parts.size shouldBe 1
        (artifact.parts[0] as TextPart).text shouldBe "Sample text content"
    }

    @Test
    fun `should create and add file part`() {
        // when
        val artifact = ArtifactBuilder()
            .apply {
                addPart(filePart {
                    file {
                        name = "example.txt"
                        mimeType = "text/plain"
                        uri = "https://example.com/file.txt"
                    }
                })
            }
            .build()

        // then
        artifact.parts.size shouldBe 1
        val filePart = artifact.parts[0] as FilePart
        filePart.file.name shouldBe "example.txt"
        filePart.file.mimeType shouldBe "text/plain"
        filePart.file.uri shouldBe "https://example.com/file.txt"
    }

    @Test
    fun `should create and add data part`() {
        // when
        val artifact = ArtifactBuilder()
            .apply {
                addPart(dataPart {
                    put("key1", "value1")
                    put("key2", 42)
                })
            }
            .build()

        // then
        artifact.parts.size shouldBe 1
        val dataPart = artifact.parts[0] as DataPart
        dataPart.data shouldBe mapOf("key1" to "value1", "key2" to 42)
    }

    @Test
    fun `should fail when no parts are provided`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            ArtifactBuilder().build()
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // when
        val artifact = artifact {
            name = "artifact-name"
            description = "artifact description"
            addPart(TextPart(text = "Sample text content"))
        }

        // then
        artifact.name shouldBe "artifact-name"
        artifact.description shouldBe "artifact description"
        artifact.parts.size shouldBe 1
        (artifact.parts[0] as TextPart).text shouldBe "Sample text content"
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val artifact = Artifact.create {
            name = "artifact-name"
            description = "artifact description"
            addPart(TextPart(text = "Sample text content"))
        }

        // then
        artifact.name shouldBe "artifact-name"
        artifact.description shouldBe "artifact description"
        artifact.parts.size shouldBe 1
        (artifact.parts[0] as TextPart).text shouldBe "Sample text content"
    }
}
