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
import io.ktor.util.reflect.TypeInfo
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class RequestSpecificationTest {
    @MockK
    lateinit var request: ApplicationRequest

    @Nested
    inner class SuccessfulMatches {
        @Test
        fun `should match when all conditions are satisfied`() =
            runTest {
                val input = Input("Alice")

                every { request.httpMethod } returns HttpMethod.Get
                every { request.path() } returns "/test"
                coEvery<Input?> { request.call.receiveNullable(any<TypeInfo>()) } returns input
                coEvery { request.call.receive(String::class) } returns Json.encodeToString(input)
                coEvery { request.headers } returns
                    Headers.build {
                        append("X-Request-ID", "RequestID")
                    }
                val headersMatcher = mockk<Matcher<Headers>>(relaxed = true)
                every { headersMatcher.test(any()).passed() } returns true

                val spec =
                    RequestSpecification<Input>(
                        method = beEqual(HttpMethod.Get),
                        path = contain("test"),
                        headers = listOf(headersMatcher),
                        body = listOf(beEqual(Input("Alice"))),
                        bodyString = listOf(contain("Alice")),
                    )

                assertThat(spec.matches(request)).isTrue()
            }

        @Test
        fun `should match when only headers are specified`() =
            runTest {
                coEvery { request.headers } returns
                    Headers.build {
                        append("X-Request-ID", "RequestID")
                    }

                val headersMatcher = mockk<Matcher<Headers>>(relaxed = true)
                val spec = RequestSpecification<Input>(headers = listOf(headersMatcher))

                every { headersMatcher.test(any()).passed() } returns true

                assertThat(spec.matches(request)).isTrue()
            }
    }

    @Nested
    inner class FailedMatches {
        @Test
        fun mismatchedMethod() =
            runTest {
                every { request.httpMethod } returns HttpMethod.Get
                val spec =
                    RequestSpecification<Input>(
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
                    RequestSpecification<Any>(
                        method = beEqual(HttpMethod.Get),
                        path = contain("differentPath"),
                    )

                assertThat(spec.matches(request)).isFalse()
            }

        @Test
        fun mismatchedHeaders() =
            runTest {
                coEvery { request.headers } returns
                    Headers.build {
                        append("X-Request-ID", "RequestID")
                    }

                val headersMatcher = mockk<Matcher<Headers>>(relaxed = true)
                val spec = RequestSpecification<Any>(headers = listOf(headersMatcher))

                every { headersMatcher.test(any()).passed() } returns false

                assertThat(spec.matches(request)).isFalse()
            }

        @Test
        fun `should not match when bodyString differs`() =
            runTest {
                coEvery { request.call.receive(String::class) } returns "Another body"

                val bodyMatcher = contain("expectedBody")
                val spec = RequestSpecification<Any>(bodyString = listOf(bodyMatcher))

                assertThat(spec.matches(request)).isFalse()
            }

        @Test
        fun `should not match when body differs`() =
            runTest {
                val input = Input("Alice")

                coEvery<Input?> { request.call.receiveNullable(any<TypeInfo>()) } returns input

                val bodyMatcher: Matcher<Input?> = beEqual(Input("Bob"))
                val spec = RequestSpecification<Input>(body = listOf(bodyMatcher))

                assertThat(spec.matches(request)).isFalse()
            }
    }
}
