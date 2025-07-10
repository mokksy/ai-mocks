package me.kpavlov.aimocks.a2a.model

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class AgentProviderBuilderTest {
    @Test
    fun `should build AgentProvider with required properties`() {
        // when
        val provider =
            AgentProviderBuilder()
                .apply {
                    organization = "Test Organization"
                }.build()

        // then
        assertSoftly(provider) {
            organization shouldBe "Test Organization"
            url shouldBe null
        }
    }

    @Test
    fun `should build AgentProvider with all properties`() {
        // when
        val provider =
            AgentProviderBuilder()
                .apply {
                    organization = "Test Organization"
                    url = "https://example.com"
                }.build()

        // then
        assertSoftly(provider) {
            organization shouldBe "Test Organization"
            url shouldBe "https://example.com"
        }
    }

    @Test
    fun `should throw exception when organization is missing`() {
        // when/then
        shouldThrow<IllegalArgumentException> {
            AgentProviderBuilder().build()
        }
    }
}
