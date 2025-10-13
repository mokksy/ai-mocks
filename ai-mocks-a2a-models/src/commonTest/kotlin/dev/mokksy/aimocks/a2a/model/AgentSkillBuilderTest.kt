package dev.mokksy.aimocks.a2a.model

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class AgentSkillBuilderTest {
    @Test
    fun `should build AgentSkill with required parameters`() {
        // when
        val skill =
            AgentSkill.create {
                id = "skill-123"
                name = "Example Skill"
                description = "Some skill description"
                tags = listOf("Tag1", "Tag2")
            }

        // then
        assertSoftly(skill) {
            id shouldBe "skill-123"
            name shouldBe "Example Skill"
            description shouldBe "Some skill description"
            tags shouldBe listOf("Tag1", "Tag2")
            examples shouldBe null
            inputModes shouldBe null
            outputModes shouldBe null
        }
    }

    @Test
    fun `should build AgentSkill with all parameters`() {
        // when
        val skill =
            AgentSkillBuilder()
                .id("skill-123")
                .name("Example Skill")
                .description("This is an example skill")
                .tags(listOf("example", "demo"))
                .examples(listOf("Example usage 1", "Example usage 2"))
                .inputModes(listOf("text"))
                .outputModes(listOf("text"))
                .build()

        // then
        assertSoftly(skill) {
            id shouldBe "skill-123"
            name shouldBe "Example Skill"
            description shouldBe "This is an example skill"
            tags shouldBe listOf("example", "demo")
            examples shouldBe listOf("Example usage 1", "Example usage 2")
            inputModes shouldBe listOf("text")
            outputModes shouldBe listOf("text")
        }
    }

    @Test
    fun `should fail when id is not provided`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            AgentSkillBuilder()
                .name("Example Skill")
                .build()
        }
    }

    @Test
    fun `should fail when name is not provided`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            AgentSkillBuilder()
                .id("skill-123")
                .build()
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // when
        val skill =
            agentSkill {
                id("skill-123")
                name("Example Skill")
                description("This is an example skill")
                tags(listOf("example", "demo"))
                examples(listOf("Example usage 1", "Example usage 2"))
                inputModes(listOf("text"))
                outputModes(listOf("text"))
            }

        // then
        assertSoftly(skill) {
            id shouldBe "skill-123"
            name shouldBe "Example Skill"
            description shouldBe "This is an example skill"
            tags shouldBe listOf("example", "demo")
            examples shouldBe listOf("Example usage 1", "Example usage 2")
            inputModes shouldBe listOf("text")
            outputModes shouldBe listOf("text")
        }
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val skill =
            AgentSkill.create {
                id("skill-123")
                name("Example Skill")
                description("This is an example skill")
                tags(listOf("example", "demo"))
                examples(listOf("Example usage 1", "Example usage 2"))
                inputModes(listOf("text"))
                outputModes(listOf("text"))
            }

        // then
        assertSoftly(skill) {
            id shouldBe "skill-123"
            name shouldBe "Example Skill"
            description shouldBe "This is an example skill"
            tags shouldBe listOf("example", "demo")
            examples shouldBe listOf("Example usage 1", "Example usage 2")
            inputModes shouldBe listOf("text")
            outputModes shouldBe listOf("text")
        }
    }
}
