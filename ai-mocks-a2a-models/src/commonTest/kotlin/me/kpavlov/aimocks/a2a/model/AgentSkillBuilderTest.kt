package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class AgentSkillBuilderTest {
    @Test
    fun `should build AgentSkill with required parameters`() {
        // when
        val skill = AgentSkill.create {
            id = "skill-123"
            name = "Example Skill"
        }

        // then
        skill.id shouldBe "skill-123"
        skill.name shouldBe "Example Skill"
        skill.description shouldBe null
        skill.tags shouldBe null
        skill.examples shouldBe null
        skill.inputModes shouldBe null
        skill.outputModes shouldBe null
    }

    @Test
    fun `should build AgentSkill with all parameters`() {
        // when
        val skill = AgentSkillBuilder()
            .id("skill-123")
            .name("Example Skill")
            .description("This is an example skill")
            .tags(listOf("example", "demo"))
            .examples(listOf("Example usage 1", "Example usage 2"))
            .inputModes(listOf("text"))
            .outputModes(listOf("text"))
            .build()

        // then
        skill.id shouldBe "skill-123"
        skill.name shouldBe "Example Skill"
        skill.description shouldBe "This is an example skill"
        skill.tags shouldBe listOf("example", "demo")
        skill.examples shouldBe listOf("Example usage 1", "Example usage 2")
        skill.inputModes shouldBe listOf("text")
        skill.outputModes shouldBe listOf("text")
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
        val skill = agentSkill {
            id("skill-123")
            name("Example Skill")
            description("This is an example skill")
            tags(listOf("example", "demo"))
            examples(listOf("Example usage 1", "Example usage 2"))
            inputModes(listOf("text"))
            outputModes(listOf("text"))
        }

        // then
        skill.id shouldBe "skill-123"
        skill.name shouldBe "Example Skill"
        skill.description shouldBe "This is an example skill"
        skill.tags shouldBe listOf("example", "demo")
        skill.examples shouldBe listOf("Example usage 1", "Example usage 2")
        skill.inputModes shouldBe listOf("text")
        skill.outputModes shouldBe listOf("text")
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val skill = AgentSkill.create {
            id("skill-123")
            name("Example Skill")
            description("This is an example skill")
            tags(listOf("example", "demo"))
            examples(listOf("Example usage 1", "Example usage 2"))
            inputModes(listOf("text"))
            outputModes(listOf("text"))
        }

        // then
        skill.id shouldBe "skill-123"
        skill.name shouldBe "Example Skill"
        skill.description shouldBe "This is an example skill"
        skill.tags shouldBe listOf("example", "demo")
        skill.examples shouldBe listOf("Example usage 1", "Example usage 2")
        skill.inputModes shouldBe listOf("text")
        skill.outputModes shouldBe listOf("text")
    }
}
