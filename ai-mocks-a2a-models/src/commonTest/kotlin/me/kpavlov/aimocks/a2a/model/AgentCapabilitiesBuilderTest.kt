package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class AgentCapabilitiesBuilderTest {
    @Test
    fun `should build AgentCapabilities with default values`() {
        // when
        val capabilities = AgentCapabilitiesBuilder().build()

        // then
        capabilities.streaming shouldBe false
        capabilities.pushNotifications shouldBe false
        capabilities.stateTransitionHistory shouldBe false
    }

    @Test
    fun `should build AgentCapabilities with custom values`() {
        // when
        val capabilities = AgentCapabilitiesBuilder().apply {
            streaming = true
            pushNotifications = true
            stateTransitionHistory = true
        }.build()

        // then
        capabilities.streaming shouldBe true
        capabilities.pushNotifications shouldBe true
        capabilities.stateTransitionHistory shouldBe true
    }

    @Test
    fun `should build AgentCapabilities with mixed values`() {
        // when
        val capabilities = AgentCapabilitiesBuilder().apply {
            streaming = true
            pushNotifications = false
            stateTransitionHistory = true
        }.build()

        // then
        capabilities.streaming shouldBe true
        capabilities.pushNotifications shouldBe false
        capabilities.stateTransitionHistory shouldBe true
    }
}
