package me.kpavlov.mokksy

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import io.kotest.matchers.Matcher
import io.kotest.matchers.equals.beEqual
import io.kotest.matchers.string.contain
import io.ktor.http.Headers
import io.ktor.http.HttpMethod
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class RequestSpecificationTest {
    @MockK
    lateinit var request: ApplicationRequest

    @Test
    fun matches() =
        runTest {
            every { request.httpMethod } returns HttpMethod.Get
            every { request.path() } returns "/test"
            coEvery { request.call.receive(String::class) } returns "The body problem"
            coEvery { request.headers } returns
                Headers.build {
                    append("X-Request-ID", "RequestID")
                }
            val headersMatcher = mockk<Matcher<Headers>>(relaxed = true)
            every { headersMatcher.test(any()).passed() } returns true

            val spec =
                RequestSpecification(
                    method = beEqual(HttpMethod.Get),
                    path = contain("test"),
                    headers = listOf(headersMatcher),
                    body = listOf(contain("body")),
                )

            assertThat(spec.matches(request)).isTrue()
        }

    @Test
    fun mismatchedMethod() =
        runTest {
            every { request.httpMethod } returns HttpMethod.Get
            val spec =
                RequestSpecification(
                    method = beEqual(HttpMethod.Post),
                    path = contain("test"),
                )

            assertThat(spec.matches(request)).isFalse()
        }

    @Test
    fun mismatchedPath() =
        runTest {
            every { request.httpMethod } returns HttpMethod.Get
            every { request.path() } returns "/test"

            val spec =
                RequestSpecification(
                    method = beEqual(HttpMethod.Get),
                    path = contain("differentPath"),
                )

            assertThat(spec.matches(request)).isFalse()
        }

    @Test
    fun matchingHeaders() =
        runTest {
            every { request.httpMethod } returns HttpMethod.Get
            every { request.path() } returns "/test"
            coEvery { request.call.receive(String::class) } returns "The body problem"
            coEvery { request.headers } returns
                Headers.build {
                    append("X-Request-ID", "RequestID")
                }

            val headersMatcher = mockk<Matcher<Headers>>(relaxed = true)
            val spec = RequestSpecification(headers = listOf(headersMatcher))

            every { headersMatcher.test(any()).passed() } returns true

            assertThat(spec.matches(request)).isTrue()
        }

    @Test
    fun mismatchedHeaders() =
        runTest {
            coEvery { request.headers } returns
                Headers.build {
                    append("X-Request-ID", "RequestID")
                }

            val headersMatcher = mockk<Matcher<Headers>>(relaxed = true)
            val spec = RequestSpecification(headers = listOf(headersMatcher))

            every { headersMatcher.test(any()).passed() } returns false

            assertThat(spec.matches(request)).isFalse()
        }

    @Test
    fun mismatchedBody() =
        runTest {
            coEvery { request.call.receive(String::class) } returns "Another body"

            val bodyMatcher = contain("expectedBody")
            val spec = RequestSpecification(body = listOf(bodyMatcher))

            assertThat(spec.matches(request)).isFalse()
        }
}
