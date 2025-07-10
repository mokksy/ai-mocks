package me.kpavlov.aimocks.a2a.model

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class AgentCapabilitiesBuilderTest {
    @Test
    fun `should build AgentCapabilities with default values`() {
        // when
        val capabilities = AgentCapabilitiesBuilder().build()

        // then
        assertSoftly(capabilities) {
            streaming shouldBe false
            pushNotifications shouldBe false
            stateTransitionHistory shouldBe false
        }
    }

    @Test
    fun `should build AgentCapabilities with custom values`() {
        // when
        val capabilities =
            AgentCapabilitiesBuilder()
                .apply {
                    streaming = true
                    pushNotifications = true
                    stateTransitionHistory = true
                }.build()

        // then
        assertSoftly(capabilities) {
            streaming shouldBe true
            pushNotifications shouldBe true
            stateTransitionHistory shouldBe true
        }
    }

    @Test
    fun `should build AgentCapabilities with mixed values`() {
        // when
        val capabilities =
            AgentCapabilitiesBuilder()
                .apply {
                    streaming = true
                    pushNotifications = false
                    stateTransitionHistory = true
                }.build()

        // then
        assertSoftly(capabilities) {
            streaming shouldBe true
            pushNotifications shouldBe false
            stateTransitionHistory shouldBe true
        }
    }
}
