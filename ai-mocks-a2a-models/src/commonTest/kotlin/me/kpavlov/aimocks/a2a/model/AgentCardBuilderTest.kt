package me.kpavlov.aimocks.a2a.model

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
        card.name shouldBe "Test Agent"
        card.url shouldBe "https://example.com/agent"
        card.version shouldBe "1.0.0"
        card.capabilities shouldNotBe null
        card.description shouldBe null
        card.provider shouldBe null
        card.documentationUrl shouldBe null
        card.authentication shouldBe null
        card.defaultInputModes shouldBe listOf("text")
        card.defaultOutputModes shouldBe listOf("text")
        card.skills shouldBe emptyList()
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
                    authentication {
                        schemes = listOf("oauth2")
                        credentials = "some-credentials"
                    }
                    defaultInputModes = listOf("text", "image")
                    defaultOutputModes = listOf("text", "audio")
                    skills +=
                        skill {
                            id = "skill-123"
                            name = "Test Skill"
                            description = "A test skill"
                        }
                }.build()

        // then
        card.name shouldBe "Test Agent"
        card.description shouldBe "A test agent"
        card.url shouldBe "https://example.com/agent"
        card.provider shouldNotBe null
        card.provider?.organization shouldBe "Test Provider"
        card.provider?.url shouldBe "https://example.com"
        card.version shouldBe "1.0.0"
        card.documentationUrl shouldBe "https://example.com/docs"
        card.capabilities.streaming shouldBe true
        card.capabilities.pushNotifications shouldBe true
        card.capabilities.stateTransitionHistory shouldBe true
        card.authentication shouldNotBe null
        card.authentication?.schemes shouldBe listOf("oauth2")
        card.authentication?.credentials shouldBe "some-credentials"
        card.defaultInputModes shouldBe listOf("text", "image")
        card.defaultOutputModes shouldBe listOf("text", "audio")
        card.skills.size shouldBe 1
        card.skills[0].id shouldBe "skill-123"
        card.skills[0].name shouldBe "Test Skill"
        card.skills[0].description shouldBe "A test skill"
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
