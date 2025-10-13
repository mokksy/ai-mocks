package dev.mokksy.aimocks.a2a.model

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

internal class AgentCardBuilderTest {
    @Test
    fun `should build AgentCard with required properties`() {
        // when
        val card =
            AgentCardBuilder()
                .apply {
                    name = "Test Agent"
                    url = "https://example.com/agent"
                    version = "1.0.0"
                    capabilities = AgentCapabilitiesBuilder().build()
                }.build()

        // then
        assertSoftly(card) {
            name shouldBe "Test Agent"
            url shouldBe "https://example.com/agent"
            version shouldBe "1.0.0"
            capabilities shouldNotBe null
            description shouldBe null
            provider shouldBe null
            documentationUrl shouldBe null
            defaultInputModes shouldBe listOf("text")
            defaultOutputModes shouldBe listOf("text")
            skills shouldBe emptyList()
        }
    }

    @Test
    fun `should build AgentCard with all properties`() {
        // when
        val card =
            AgentCardBuilder()
                .apply {
                    name = "Test Agent"
                    description = "A test agent"
                    url = "https://example.com/agent"
                    provider {
                        organization = "Test Provider"
                        url = "https://example.com"
                    }
                    version = "1.0.0"
                    documentationUrl = "https://example.com/docs"
                    capabilities {
                        streaming = true
                        pushNotifications = true
                        stateTransitionHistory = true
                    }
                    defaultInputModes = listOf("text", "image")
                    defaultOutputModes = listOf("text", "audio")
                    skills +=
                        skill {
                            id = "skill-123"
                            name = "Test Skill"
                            description = "A test skill"
                            tags = listOf("test-tag")
                        }
                }.build()

        // then
        assertSoftly(card) {
            name shouldBe "Test Agent"
            description shouldBe "A test agent"
            url shouldBe "https://example.com/agent"
            provider shouldNotBe null
            provider?.organization shouldBe "Test Provider"
            provider?.url shouldBe "https://example.com"
            version shouldBe "1.0.0"
            documentationUrl shouldBe "https://example.com/docs"
            capabilities.streaming shouldBe true
            capabilities.pushNotifications shouldBe true
            capabilities.stateTransitionHistory shouldBe true
            defaultInputModes shouldBe listOf("text", "image")
            defaultOutputModes shouldBe listOf("text", "audio")
            skills.size shouldBe 1
            skills[0].id shouldBe "skill-123"
            skills[0].name shouldBe "Test Skill"
            skills[0].description shouldBe "A test skill"
        }
    }

    @Test
    fun `should throw exception when required properties are missing and validation is enabled`() {
        // when/then
        shouldThrow<IllegalArgumentException> {
            AgentCardBuilder().build(validate = true)
        }

        shouldThrow<IllegalArgumentException> {
            AgentCardBuilder()
                .apply {
                    name = "Test Agent"
                }.build(validate = true)
        }

        shouldThrow<IllegalArgumentException> {
            AgentCardBuilder()
                .apply {
                    name = "Test Agent"
                    url = "https://example.com/agent"
                }.build(validate = true)
        }

        shouldThrow<IllegalArgumentException> {
            AgentCardBuilder()
                .apply {
                    name = "Test Agent"
                    url = "https://example.com/agent"
                    version = "1.0.0"
                }.build(validate = true)
        }
    }
}
