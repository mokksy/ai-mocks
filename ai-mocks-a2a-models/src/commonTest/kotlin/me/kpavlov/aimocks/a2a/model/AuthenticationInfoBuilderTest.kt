package me.kpavlov.aimocks.a2a.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class AuthenticationInfoBuilderTest {
    @Test
    fun `should build AuthenticationInfo with required parameters`() {
        // when
        val authInfo =
            AuthenticationInfoBuilder()
                .schemes(listOf("basic", "oauth2"))
                .build()

        // then
        authInfo.schemes shouldBe listOf("basic", "oauth2")
        authInfo.credentials shouldBe null
    }

    @Test
    fun `should build AuthenticationInfo with all parameters`() {
        // when
        val authInfo =
            AuthenticationInfoBuilder()
                .schemes(listOf("basic", "oauth2"))
                .credentials("token123")
                .build()

        // then
        authInfo.schemes shouldBe listOf("basic", "oauth2")
        authInfo.credentials shouldBe "token123"
    }

    @Test
    fun `should throw exception when schemes is empty`() {
        // when/then
        assertFailsWith<IllegalArgumentException> {
            AuthenticationInfoBuilder()
                .schemes(emptyList())
                .build()
        }
    }

    @Test
    fun `should build using top-level DSL function`() {
        // when
        val authInfo =
            authenticationInfo {
                schemes = listOf("basic", "oauth2")
                credentials = "token123"
            }

        // then
        authInfo.schemes shouldBe listOf("basic", "oauth2")
        authInfo.credentials shouldBe "token123"
    }

    @Test
    fun `should build using companion object create function`() {
        // when
        val authInfo =
            AuthenticationInfo.create {
                schemes = listOf("basic", "oauth2")
                credentials = "token123"
            }

        // then
        authInfo.schemes shouldBe listOf("basic", "oauth2")
        authInfo.credentials shouldBe "token123"
    }
}
