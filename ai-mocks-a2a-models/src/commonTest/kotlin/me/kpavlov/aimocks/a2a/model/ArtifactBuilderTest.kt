package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class ArtifactBuilderTest {
    @Test
    fun `should build Artifact with required parameters`() {
        // when
        val artifact = ArtifactBuilder().apply {
            addPart(textPart {
                text = "Hello, world!"
            })
        }.build()

        // then
        artifact.name shouldBe null
        artifact.description shouldBe null
        artifact.parts.size shouldBe 1
        artifact.parts[0] shouldBe TextPart(text = "Hello, world!")
        artifact.index shouldBe 0
        artifact.append shouldBe null
        artifact.lastChunk shouldBe null
        artifact.metadata shouldBe null
    }

    @Test
    fun `should build Artifact with all parameters`() {
        // when
        val artifact = ArtifactBuilder().apply {
            name = "test-artifact"
            description = "Test artifact description"
            addPart(textPart {
                text = "Hello, world!"
            })
            addPart(filePart {
                file {
                    name = "test.txt"
                    mimeType = "text/plain"
                    bytes = "file content".encodeToByteArray()
                }
            })
            index = 1
            append = true
            lastChunk = false
            metadata = Metadata.empty()
        }.build()

        // then
        artifact.name shouldBe "test-artifact"
        artifact.description shouldBe "Test artifact description"
        artifact.parts.size shouldBe 2
        artifact.parts[0] shouldBe TextPart(text = "Hello, world!")
        (artifact.parts[1] as FilePart).file.name shouldBe "test.txt"
        artifact.index shouldBe 1
        artifact.append shouldBe true
        artifact.lastChunk shouldBe false
        artifact.metadata shouldBe Metadata.empty()
    }

    @Test
    fun `should throw exception when parts is empty`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            ArtifactBuilder().build()
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // when
        val artifact = artifact {
            name = "test-artifact"
            addPart(textPart {
                text = "Hello, world!"
            })
        }

        // then
        artifact.name shouldBe "test-artifact"
        artifact.parts.size shouldBe 1
        artifact.parts[0] shouldBe TextPart(text = "Hello, world!")
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val artifact = Artifact.create {
            name = "test-artifact"
            addPart(textPart {
                text = "Hello, world!"
            })
        }

        // then
        artifact.name shouldBe "test-artifact"
        artifact.parts.size shouldBe 1
        artifact.parts[0] shouldBe TextPart(text = "Hello, world!")
    }
}
