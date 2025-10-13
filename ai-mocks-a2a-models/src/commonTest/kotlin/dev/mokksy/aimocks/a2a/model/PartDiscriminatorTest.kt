package dev.mokksy.aimocks.a2a.model

import dev.mokksy.test.utils.deserializeAndSerialize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

/**
 * Test to verify that the Part polymorphic serialization works correctly with "kind" discriminator.
 * This addresses the issue: "Class discriminator was missing and no default serializers were registered in the polymorphic scope of 'Part'."
 */
internal class PartDiscriminatorTest {
    @Test
    fun `should deserialize TextPart with kind discriminator`() {
        // This is the exact JSON from the error message that was failing
        val json =
            """{"kind":"text","text":"write a long paper describing the attached pictures"}"""

        val part = deserializeAndSerialize<Part>(json)

        part.shouldBeInstanceOf<TextPart>()
        (part as TextPart).text shouldBe "write a long paper describing the attached pictures"
    }

    @Test
    fun `should deserialize DataPart with kind discriminator`() {
        val json = """{"kind":"data","data":{"key":"value"}}"""

        val part = deserializeAndSerialize<Part>(json)

        part.shouldBeInstanceOf<DataPart>()
    }

    @Test
    fun `should deserialize FilePart with kind discriminator`() {
        val json =
            """{"kind":"file","file":{"mimeType":"text/plain","uri":"https://example.com/file.txt"}}"""

        val part = deserializeAndSerialize<Part>(json)

        part.shouldBeInstanceOf<FilePart>()
    }
}
