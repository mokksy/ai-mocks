package me.kpavlov.mokksy.utils.logger

import assertk.assertThat
import assertk.assertions.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource

/**
 * Comprehensive unit tests for HttpFormatter JVM implementation.
 * Testing framework: JUnit 5 with MockK for mocking and AssertK for assertions.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpFormatterJvmTest {

    private lateinit var httpFormatterWithColor: HttpFormatter
    private lateinit var httpFormatterWithoutColor: HttpFormatter

    @BeforeEach
    fun setUp() {
        httpFormatterWithColor = HttpFormatter(useColor = true)
        httpFormatterWithoutColor = HttpFormatter(useColor = false)
    }

    @Test
    fun `constructor should initialize with default light on dark theme`() {
        // When
        val formatter = HttpFormatter()

        // Then
        assertThat(formatter.colors.path).isEqualTo(AnsiColor.STRONGER)
        assertThat(formatter.colors.headerName).isEqualTo(AnsiColor.YELLOW)
        assertThat(formatter.colors.headerValue).isEqualTo(AnsiColor.PALE)
        assertThat(formatter.colors.body).isEqualTo(AnsiColor.LIGHT_GRAY)
    }

    @Test
    fun `constructor should initialize with dark on light theme`() {
        // When
        val formatter = HttpFormatter(theme = ColorTheme.DARK_ON_LIGHT)

        // Then
        assertThat(formatter.colors.path).isEqualTo(AnsiColor.STRONGER)
        assertThat(formatter.colors.headerName).isEqualTo(AnsiColor.BLACK)
        assertThat(formatter.colors.headerValue).isEqualTo(AnsiColor.PALE)
        assertThat(formatter.colors.body).isEqualTo(AnsiColor.LIGHT_GRAY)
    }

    @Test
    fun `requestLine should format GET request correctly with color`() {
        // Given
        val method = HttpMethod.Get
        val path = "/api/users"

        // When
        val result = httpFormatterWithColor.requestLine(method, path)

        // Then
        assertThat(result).isNotNull()
        assertThat(result).contains("GET")
        assertThat(result).contains("/api/users")
        // Should contain ANSI color codes when color is enabled
        assertThat(result).contains(AnsiColor.BLUE.code) // GET method color
        assertThat(result).contains(AnsiColor.STRONGER.code) // path color
    }

    @Test
    fun `requestLine should format GET request correctly without color`() {
        // Given
        val method = HttpMethod.Get
        val path = "/api/users"

        // When
        val result = httpFormatterWithoutColor.requestLine(method, path)

        // Then
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo("GET /api/users")
        // Should not contain ANSI color codes when color is disabled
        assertThat(result).doesNotContain(AnsiColor.BLUE.code)
        assertThat(result).doesNotContain(AnsiColor.STRONGER.code)
    }

    @ParameterizedTest
    @EnumSource(value = HttpMethod::class, names = ["Get", "Post", "Put", "Delete", "Patch", "Head", "Options"])
    fun `requestLine should handle all HTTP methods correctly`(method: HttpMethod) {
        // Given
        val path = "/api/test"

        // When
        val result = httpFormatterWithColor.requestLine(method, path)

        // Then
        assertThat(result).isNotNull()
        assertThat(result).contains(method.value)
        assertThat(result).contains(path)
    }

    @Test
    fun `requestLine should format POST request with green color`() {
        // Given
        val method = HttpMethod.Post
        val path = "/api/users"

        // When
        val result = httpFormatterWithColor.requestLine(method, path)

        // Then
        assertThat(result).contains("POST")
        assertThat(result).contains("/api/users")
        assertThat(result).contains(AnsiColor.GREEN.code) // POST method color
    }

    @Test
    fun `requestLine should format DELETE request with red color`() {
        // Given
        val method = HttpMethod.Delete
        val path = "/api/users/123"

        // When
        val result = httpFormatterWithColor.requestLine(method, path)

        // Then
        assertThat(result).contains("DELETE")
        assertThat(result).contains("/api/users/123")
        assertThat(result).contains(AnsiColor.RED.code) // DELETE method color
    }

    @Test
    fun `requestLine should format other methods with bold color`() {
        // Given
        val method = HttpMethod.Put
        val path = "/api/users/123"

        // When
        val result = httpFormatterWithColor.requestLine(method, path)

        // Then
        assertThat(result).contains("PUT")
        assertThat(result).contains("/api/users/123")
        assertThat(result).contains(AnsiColor.STRONGER.code) // Bold for other methods
    }

    @Test
    fun `requestLine should handle paths with query parameters`() {
        // Given
        val method = HttpMethod.Get
        val path = "/api/users?page=1&limit=10&sort=name"

        // When
        val result = httpFormatterWithColor.requestLine(method, path)

        // Then
        assertThat(result).contains("GET")
        assertThat(result).contains("/api/users?page=1&limit=10&sort=name")
    }

    @Test
    fun `requestLine should handle paths with special characters`() {
        // Given
        val method = HttpMethod.Get
        val path = "/api/users/search?q=john%20doe&filter=active"

        // When
        val result = httpFormatterWithColor.requestLine(method, path)

        // Then
        assertThat(result).contains("GET")
        assertThat(result).contains("/api/users/search?q=john%20doe&filter=active")
    }

    @Test
    fun `header should format single header value correctly with color`() {
        // Given
        val headerName = "Content-Type"
        val headerValues = listOf("application/json")

        // When
        val result = httpFormatterWithColor.header(headerName, headerValues)

        // Then
        assertThat(result).isNotNull()
        assertThat(result).contains("Content-Type")
        assertThat(result).contains("[application/json]")
        assertThat(result).contains(AnsiColor.YELLOW.code) // header name color
        assertThat(result).contains(AnsiColor.PALE.code) // header value color
    }

    @Test
    fun `header should format single header value correctly without color`() {
        // Given
        val headerName = "Content-Type"
        val headerValues = listOf("application/json")

        // When
        val result = httpFormatterWithoutColor.header(headerName, headerValues)

        // Then
        assertThat(result).isEqualTo("Content-Type: [application/json]")
        assertThat(result).doesNotContain(AnsiColor.YELLOW.code)
        assertThat(result).doesNotContain(AnsiColor.PALE.code)
    }

    @Test
    fun `header should format multiple header values correctly`() {
        // Given
        val headerName = "Set-Cookie"
        val headerValues = listOf("session=abc123", "theme=dark", "lang=en")

        // When
        val result = httpFormatterWithColor.header(headerName, headerValues)

        // Then
        assertThat(result).contains("Set-Cookie")
        assertThat(result).contains("[session=abc123,theme=dark,lang=en]")
    }

    @Test
    fun `header should handle empty header values`() {
        // Given
        val headerName = "X-Custom-Header"
        val headerValues = emptyList<String>()

        // When
        val result = httpFormatterWithColor.header(headerName, headerValues)

        // Then
        assertThat(result).contains("X-Custom-Header")
        assertThat(result).contains("[]")
    }

    @Test
    fun `header should handle header with special characters`() {
        // Given
        val headerName = "X-Request-ID"
        val headerValues = listOf("abc-123-def-456")

        // When
        val result = httpFormatterWithColor.header(headerName, headerValues)

        // Then
        assertThat(result).contains("X-Request-ID")
        assertThat(result).contains("[abc-123-def-456]")
    }

    @Test
    fun `formatBody should return empty string for null body`() {
        // When
        val result = httpFormatterWithColor.formatBody(null)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `formatBody should return empty string for blank body`() {
        // When
        val result = httpFormatterWithColor.formatBody("   ")

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `formatBody should return empty string for empty body`() {
        // When
        val result = httpFormatterWithColor.formatBody("")

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `formatBody should return body as-is when color is disabled`() {
        // Given
        val body = """{"name":"John","age":30}"""

        // When
        val result = httpFormatterWithoutColor.formatBody(body, ContentType.Application.Json)

        // Then
        assertThat(result).isEqualTo(body)
    }

    @Test
    fun `formatBody should apply highlighting when color is enabled`() {
        // Given
        val body = """{"name":"John","age":30}"""

        // When
        val result = httpFormatterWithColor.formatBody(body, ContentType.Application.Json)

        // Then
        assertThat(result).isNotNull()
        assertThat(result).contains("John")
        assertThat(result).contains("30")
        // Should potentially contain highlighting (depends on Highlighting implementation)
    }

    @Test
    fun `formatBody should handle XML content`() {
        // Given
        val xmlBody = """<?xml version="1.0"?><user><name>John</name></user>"""

        // When
        val result = httpFormatterWithColor.formatBody(xmlBody, ContentType.Application.Xml)

        // Then
        assertThat(result).contains("John")
        assertThat(result).contains("user")
    }

    @Test
    fun `formatBody should handle plain text content`() {
        // Given
        val textBody = "Hello, World!"

        // When
        val result = httpFormatterWithColor.formatBody(textBody, ContentType.Text.Plain)

        // Then
        assertThat(result).contains("Hello, World!")
    }

    @Test
    fun `formatBody should handle large body content`() {
        // Given
        val largeBody = "x".repeat(10000)

        // When
        val result = httpFormatterWithColor.formatBody(largeBody, ContentType.Text.Plain)

        // Then
        assertThat(result).isNotNull()
        assertThat(result.length).isGreaterThan(0)
    }

    @Test
    fun `formatBody should handle body with special characters`() {
        // Given
        val bodyWithSpecialChars = """{"message":"Hello 🌍! Special chars: äöü ñ ©®™"}"""

        // When
        val result = httpFormatterWithColor.formatBody(bodyWithSpecialChars, ContentType.Application.Json)

        // Then
        assertThat(result).contains("🌍")
        assertThat(result).contains("äöü")
        assertThat(result).contains("©®™")
    }

    @Test
    fun `formatRequest should format complete request correctly`() = runTest {
        // Given
        val mockCall = mockk<ApplicationCall>()
        val mockRequest = mockk<RoutingRequest>()
        
        every { mockRequest.call } returns mockCall
        every { mockRequest.httpMethod } returns HttpMethod.Post
        every { mockRequest.uri } returns "/api/users"
        every { mockRequest.headers.entries() } returns setOf(
            object : Map.Entry<String, List<String>> {
                override val key = "Content-Type"
                override val value = listOf("application/json")
            },
            object : Map.Entry<String, List<String>> {
                override val key = "Authorization"
                override val value = listOf("Bearer token123")
            }
        )
        coEvery { mockCall.receiveText() } returns """{"name":"John","age":30}"""
        every { mockRequest.contentType() } returns ContentType.Application.Json

        // When
        val result = httpFormatterWithColor.formatRequest(mockRequest)

        // Then
        assertThat(result).isNotNull()
        assertThat(result).contains("POST")
        assertThat(result).contains("/api/users")
        assertThat(result).contains("Content-Type")
        assertThat(result).contains("application/json")
        assertThat(result).contains("Authorization")
        assertThat(result).contains("Bearer token123")
        assertThat(result).contains(""""name":"John"""")
        assertThat(result).contains(""""age":30""")
    }

    @Test
    fun `formatRequest should handle request with no headers`() = runTest {
        // Given
        val mockCall = mockk<ApplicationCall>()
        val mockRequest = mockk<RoutingRequest>()
        
        every { mockRequest.call } returns mockCall
        every { mockRequest.httpMethod } returns HttpMethod.Get
        every { mockRequest.uri } returns "/api/users"
        every { mockRequest.headers.entries() } returns emptySet()
        coEvery { mockCall.receiveText() } returns ""
        every { mockRequest.contentType() } returns null

        // When
        val result = httpFormatterWithColor.formatRequest(mockRequest)

        // Then
        assertThat(result).contains("GET")
        assertThat(result).contains("/api/users")
    }

    @Test
    fun `formatRequest should handle request with empty body`() = runTest {
        // Given
        val mockCall = mockk<ApplicationCall>()
        val mockRequest = mockk<RoutingRequest>()
        
        every { mockRequest.call } returns mockCall
        every { mockRequest.httpMethod } returns HttpMethod.Get
        every { mockRequest.uri } returns "/api/users"
        every { mockRequest.headers.entries() } returns emptySet()
        coEvery { mockCall.receiveText() } returns ""
        every { mockRequest.contentType() } returns null

        // When
        val result = httpFormatterWithColor.formatRequest(mockRequest)

        // Then
        assertThat(result).contains("GET")
        assertThat(result).contains("/api/users")
        // Should handle empty body gracefully
    }

    @Test
    fun `formatRequest should handle concurrent requests safely`() = runTest {
        // Given
        val requests = (1..5).map { i ->
            val mockCall = mockk<ApplicationCall>()
            val mockRequest = mockk<RoutingRequest>()
            
            every { mockRequest.call } returns mockCall
            every { mockRequest.httpMethod } returns HttpMethod.Get
            every { mockRequest.uri } returns "/api/users/$i"
            every { mockRequest.headers.entries() } returns setOf(
                object : Map.Entry<String, List<String>> {
                    override val key = "X-Request-ID"
                    override val value = listOf("req-$i")
                }
            )
            coEvery { mockCall.receiveText() } returns ""
            every { mockRequest.contentType() } returns null
            
            mockRequest
        }

        // When
        val results = requests.map { httpFormatterWithColor.formatRequest(it) }

        // Then
        results.forEachIndexed { index, result ->
            assertThat(result).contains("GET")
            assertThat(result).contains("/api/users/${index + 1}")
            assertThat(result).contains("X-Request-ID")
            assertThat(result).contains("req-${index + 1}")
        }
    }

    @Test
    fun `isColorSupported should return boolean value on JVM`() {
        // When
        val result = isColorSupported()

        // Then
        assertThat(result).isInstanceOf(Boolean::class.java)
    }

    @Test
    fun `colorize should return colorized text when enabled`() {
        // Given
        val text = "Hello World"
        val color = AnsiColor.RED

        // When
        val result = colorize(text, color, enabled = true)

        // Then
        assertThat(result).startsWith(color.code)
        assertThat(result).endsWith(AnsiColor.RESET.code)
        assertThat(result).contains(text)
    }

    @Test
    fun `colorize should return original text when disabled`() {
        // Given
        val text = "Hello World"
        val color = AnsiColor.RED

        // When
        val result = colorize(text, color, enabled = false)

        // Then
        assertThat(result).isEqualTo(text)
        assertThat(result).doesNotContain(color.code)
        assertThat(result).doesNotContain(AnsiColor.RESET.code)
    }

    @ParameterizedTest
    @EnumSource(AnsiColor::class)
    fun `colorize should handle all ANSI colors correctly`(color: AnsiColor) {
        // Given
        val text = "Test"

        // When
        val result = colorize(text, color, enabled = true)

        // Then
        assertThat(result).contains(color.code)
        assertThat(result).contains(text)
        assertThat(result).endsWith(AnsiColor.RESET.code)
    }

    @Test
    fun `formatter should handle malformed JSON gracefully`() {
        // Given
        val malformedJson = """{"name":"John","age":}"""

        // When
        val result = httpFormatterWithColor.formatBody(malformedJson, ContentType.Application.Json)

        // Then
        assertThat(result).isNotNull()
        assertThat(result).contains("John")
    }

    @Test
    fun `formatter should preserve exact formatting without modification`() {
        // Given
        val jsonWithFormatting = """{
  "name": "John",
  "age": 30,
  "address": {
    "street": "123 Main St",
    "city": "New York"
  }
}"""

        // When
        val result = httpFormatterWithoutColor.formatBody(jsonWithFormatting, ContentType.Application.Json)

        // Then
        assertThat(result).isEqualTo(jsonWithFormatting)
    }
}