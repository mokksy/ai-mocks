package me.kpavlov.aimocks.a2a.model

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class AgentAuthenticationBuilderTest {
    @Test
    fun `should build AgentAuthentication with schemes`() {
        // when
        val auth =
            AgentAuthenticationBuilder()
                .apply {
                    schemes = listOf("oauth2")
                }.build()

        // then
        assertSoftly(auth) {
            schemes shouldBe listOf("oauth2")
            credentials shouldBe null
        }
    }

    @Test
    fun `should build AgentAuthentication with schemes and credentials`() {
        // when
        val auth =
            AgentAuthenticationBuilder()
                .apply {
                    schemes = listOf("oauth2", "api_key")
                    credentials = "some-credentials-info"
                }.build()

        // then
        assertSoftly(auth) {
            schemes shouldBe listOf("oauth2", "api_key")
            credentials shouldBe "some-credentials-info"
        }
    }

    @Test
    fun `should throw exception when schemes is empty`() {
        // when/then
        shouldThrow<IllegalArgumentException> {
            AgentAuthenticationBuilder().build()
        }
    }

    @Test
    fun `should build using builder directly`() {
        // when
        val auth =
            AgentAuthenticationBuilder()
                .apply {
                    schemes = listOf("oauth2")
                    credentials = "some-credentials"
                }.build()

        // then
        assertSoftly(auth) {
            schemes shouldBe listOf("oauth2")
            credentials shouldBe "some-credentials"
        }
    }
}
