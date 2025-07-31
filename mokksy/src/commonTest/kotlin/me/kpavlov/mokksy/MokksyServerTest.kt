package me.kpavlov.mokksy

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.http.HttpMethod
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Comprehensive unit tests for MokksyServer class.
 * Testing framework: kotlin.test with kotest assertions
 * Focus: Server lifecycle, HTTP method handling, configuration, and error scenarios
 */
class MokksyServerTest {

    @Test
    fun `should create server with default parameters`() {
        val server = MokksyServer()
        
        // Server should be created successfully
        assertNotNull(server)
        server.port() shouldBeGreaterThan 0
        
        server.shutdown()
    }

    @Test
    fun `should create server with custom port`() {
        val server = MokksyServer(port = 0) // Use 0 for dynamic port assignment
        
        // Should assign an available port
        server.port() shouldBeGreaterThan 0
        
        server.shutdown()
    }

    @Test
    fun `should create server with verbose logging enabled`() {
        val server = MokksyServer(verbose = true)
        
        assertNotNull(server)
        server.port() shouldBeGreaterThan 0
        
        server.shutdown()
    }

    @Test
    fun `should create server with custom configuration`() {
        val config = ServerConfiguration(verbose = true)
        val server = MokksyServer(configuration = config)
        
        assertNotNull(server)
        server.port() shouldBeGreaterThan 0
        
        server.shutdown()
    }

    @Test
    fun `should create server with custom host`() {
        val server = MokksyServer(host = "127.0.0.1")
        
        assertNotNull(server)
        server.port() shouldBeGreaterThan 0
        
        server.shutdown()
    }

    @Test
    fun `should register GET stub successfully`() {
        val server = MokksyServer()
        
        val buildingStep = server.get {
            path("/test")
        }
        
        assertNotNull(buildingStep)
        server.shutdown()
    }

    @Test
    fun `should register POST stub successfully`() {
        val server = MokksyServer()
        
        val buildingStep = server.post {
            path("/api/data")
        }
        
        assertNotNull(buildingStep)
        server.shutdown()
    }

    @Test
    fun `should register PUT stub successfully`() {
        val server = MokksyServer()
        
        val buildingStep = server.put {
            path("/api/update")
        }
        
        assertNotNull(buildingStep)
        server.shutdown()
    }

    @Test
    fun `should register DELETE stub successfully`() {
        val server = MokksyServer()
        
        val buildingStep = server.delete {
            path("/api/remove")
        }
        
        assertNotNull(buildingStep)
        server.shutdown()
    }

    @Test
    fun `should register PATCH stub successfully`() {
        val server = MokksyServer()
        
        val buildingStep = server.patch {
            path("/api/modify")
        }
        
        assertNotNull(buildingStep)
        server.shutdown()
    }

    @Test
    fun `should register HEAD stub successfully`() {
        val server = MokksyServer()
        
        val buildingStep = server.head {
            path("/api/check")
        }
        
        assertNotNull(buildingStep)
        server.shutdown()
    }

    @Test
    fun `should register OPTIONS stub successfully`() {
        val server = MokksyServer()
        
        val buildingStep = server.options {
            path("/api/options")
        }
        
        assertNotNull(buildingStep)
        server.shutdown()
    }

    @Test
    fun `should register stub with custom HTTP method`() {
        val server = MokksyServer()
        
        val buildingStep = server.method(
            httpMethod = HttpMethod.Get,
            requestType = String::class
        ) {
            path("/custom")
        }
        
        assertNotNull(buildingStep)
        server.shutdown()
    }

    @Test
    fun `should register named stub`() {
        val server = MokksyServer()
        
        val buildingStep = server.get(name = "test-stub") {
            path("/named")
        }
        
        assertNotNull(buildingStep)
        server.shutdown()
    }

    @Test
    fun `should register stub with configuration`() {
        val server = MokksyServer()
        val config = StubConfiguration(name = "configured-stub")
        
        val buildingStep = server.get(configuration = config) {
            path("/configured")
        }
        
        assertNotNull(buildingStep)
        server.shutdown()
    }

    @Test
    fun `should register stub with custom request type`() {
        val server = MokksyServer()
        
        data class CustomRequest(val id: Int, val name: String)
        
        val buildingStep = server.post(requestType = CustomRequest::class) {
            path("/custom-type")
        }
        
        assertNotNull(buildingStep)
        server.shutdown()
    }

    @Test
    fun `should handle multiple stubs registration`() {
        val server = MokksyServer()
        
        server.get { path("/endpoint1") }
        server.post { path("/endpoint2") }
        server.put { path("/endpoint3") }
        server.delete { path("/endpoint4") }
        server.patch { path("/endpoint5") }
        server.head { path("/endpoint6") }
        server.options { path("/endpoint7") }
        
        // Should not throw any exceptions during registration
        assertEquals(7, server.findAllUnmatchedRequests().size)
        server.shutdown()
    }

    @Test
    fun `should initialize unmatched requests list as empty when no stubs registered`() {
        val server = MokksyServer()
        
        val unmatchedRequests = server.findAllUnmatchedRequests()
        
        unmatchedRequests.shouldBeEmpty()
        server.shutdown()
    }

    @Test
    fun `should track unmatched requests`() {
        val server = MokksyServer()
        
        // Register a stub that won't be matched
        server.get { path("/never-called") }
        
        val unmatchedRequests = server.findAllUnmatchedRequests()
        
        unmatchedRequests.shouldNotBeEmpty()
        assertEquals(1, unmatchedRequests.size)
        server.shutdown()
    }

    @Test
    fun `should reset match counts`() {
        val server = MokksyServer()
        
        server.get { path("/test-reset") }
        
        // Initially unmatched
        server.findAllUnmatchedRequests().shouldNotBeEmpty()
        
        // Reset should still show unmatched since no actual requests were made
        server.resetMatchCounts()
        server.findAllUnmatchedRequests().shouldNotBeEmpty()
        
        server.shutdown()
    }

    @Test
    fun `should check for unmatched requests and throw when present`() {
        val server = MokksyServer()
        
        // Register stub that won't be matched
        server.get { path("/unmatched-endpoint") }
        
        // Should throw because of unmatched requests
        shouldThrow<AssertionError> {
            server.checkForUnmatchedRequests()
        }
        
        server.shutdown()
    }

    @Test
    fun `should check for unmatched requests and pass when none present`() {
        val server = MokksyServer()
        
        // No stubs registered, so no unmatched requests
        server.checkForUnmatchedRequests() // Should not throw
        
        server.shutdown()
    }

    @Test
    fun `should handle concurrent stub registrations`() = runTest {
        val server = MokksyServer()
        
        // Register multiple stubs concurrently
        val stubRegistrations = (1..10).map { index ->
            async {
                server.get { path("/concurrent-$index") }
            }
        }
        
        // Wait for all registrations to complete
        stubRegistrations.awaitAll()
        
        // Should have all unmatched requests
        val unmatchedRequests = server.findAllUnmatchedRequests()
        assertEquals(10, unmatchedRequests.size)
        
        server.shutdown()
    }

    @Test
    fun `should handle server lifecycle correctly`() {
        var server: MokksyServer? = null
        
        try {
            server = MokksyServer()
            val port = server.port()
            
            port shouldBeGreaterThan 0
            
            // Server should be functional after creation
            server.get { path("/lifecycle-test") }
            
        } finally {
            server?.shutdown()
        }
    }

    @Test
    fun `should handle multiple shutdowns gracefully`() {
        val server = MokksyServer()
        
        // First shutdown
        server.shutdown()
        
        // Second shutdown should not throw
        server.shutdown()
    }

    @Test
    fun `should register stub with response definition`() {
        val server = MokksyServer()
        
        server.get { path("/with-response") } respondsWith {
            status(200)
            body("OK")
        }
        
        server.shutdown()
    }

    @Test
    fun `should register stub with streaming response`() {
        val server = MokksyServer()
        
        server.get { path("/stream") } respondsWithStream<String> {
            // Stream configuration would go here
        }
        
        server.shutdown()
    }

    @Test
    fun `should register stub with SSE response`() {
        val server = MokksyServer()
        
        server.get { path("/sse") } respondsWithSseStream<String> {
            // SSE stream configuration would go here
        }
        
        server.shutdown()
    }

    @Test
    fun `should handle custom configurer during construction`() {
        val server = MokksyServer { application ->
            // Custom application configuration
            application.environment.log.info("Custom configurer applied")
        }
        
        assertNotNull(server)
        server.shutdown()
    }

    @Test
    fun `should handle port assignment with zero port`() {
        val server = MokksyServer(port = 0) // Should assign random available port
        
        server.port() shouldBeGreaterThan 0
        server.shutdown()
    }

    @Test
    fun `should preserve stub configuration names`() {
        val server = MokksyServer()
        
        server.get(name = "named-get-stub") { path("/named-get") }
        server.post(name = "named-post-stub") { path("/named-post") }
        
        // Verify stubs are registered (indirectly through unmatched requests)
        val unmatchedRequests = server.findAllUnmatchedRequests()
        assertEquals(2, unmatchedRequests.size)
        
        server.shutdown()
    }

    @Test
    fun `should handle complex request specifications`() {
        val server = MokksyServer()
        
        server.post {
            path("/complex")
            header("Content-Type", "application/json")
            header("Authorization", "Bearer token")
        } respondsWith {
            status(201)
            header("Location", "/resource/123")
            body("""{"created": true}""")
        }
        
        server.shutdown()
    }

    @Test
    fun `should support fluent API chaining`() {
        val server = MokksyServer()
        
        // Test fluent API
        val result = server
            .get { path("/fluent") }
            .respondsWith { 
                status(200)
                body("Fluent API works")
            }
        
        // The fluent API should complete without returning a value (Unit)
        assertEquals(Unit, result)
        
        server.shutdown()
    }

    @Test
    fun `should handle edge case request types`() {
        val server = MokksyServer()
        
        // Test with different request types
        server.post(requestType = Map::class) { path("/map-request") }
        server.put(requestType = List::class) { path("/list-request") }
        server.patch(requestType = Any::class) { path("/any-request") }
        
        assertEquals(3, server.findAllUnmatchedRequests().size)
        
        server.shutdown()
    }

    @Test
    fun `should handle server with wait parameter`() {
        val server = MokksyServer(wait = false) // Non-blocking start
        
        server.port() shouldBeGreaterThan 0
        server.shutdown()
    }

    @Test
    fun `should maintain logger instance`() {
        val server = MokksyServer()
        
        // Logger should be initialized after server creation
        assertNotNull(server.logger)
        
        server.shutdown()
    }

    @Test
    fun `should handle rapid stub registration and removal cycle`() {
        repeat(5) { cycle ->
            val server = MokksyServer()
            
            // Register multiple stubs
            repeat(3) { stubIndex ->
                server.get { path("/cycle-$cycle-stub-$stubIndex") }
            }
            
            // Verify registration
            assertEquals(3, server.findAllUnmatchedRequests().size)
            
            server.shutdown()
        }
    }

    @Test
    fun `should handle all HTTP method variants`() {
        val server = MokksyServer()
        
        // Test all HTTP method convenience functions
        server.get(requestType = String::class) { path("/get-typed") }
        server.post(requestType = String::class) { path("/post-typed") }
        server.put(requestType = String::class) { path("/put-typed") }
        server.delete(requestType = String::class) { path("/delete-typed") }
        server.patch(requestType = String::class) { path("/patch-typed") }
        server.head(requestType = String::class) { path("/head-typed") }
        server.options(requestType = String::class) { path("/options-typed") }
        
        assertEquals(7, server.findAllUnmatchedRequests().size)
        
        server.shutdown()
    }

    @Test
    fun `should handle method with configuration variants`() {
        val server = MokksyServer()
        val config = StubConfiguration(name = "config-test")
        
        // Test method variants with configuration
        server.get(configuration = config, requestType = String::class) { path("/get-config") }
        server.post(configuration = config, requestType = String::class) { path("/post-config") }
        server.put(configuration = config, requestType = String::class) { path("/put-config") }
        server.delete(configuration = config, requestType = String::class) { path("/delete-config") }
        server.patch(configuration = config, requestType = String::class) { path("/patch-config") }
        server.head(configuration = config, requestType = String::class) { path("/head-config") }
        server.options(configuration = config, requestType = String::class) { path("/options-config") }
        
        assertEquals(7, server.findAllUnmatchedRequests().size)
        
        server.shutdown()
    }

    @Test
    fun `should handle string-based stubs without explicit type`() {
        val server = MokksyServer()
        
        // These use the convenience methods that default to String::class
        server.get { path("/string-get") }
        server.post { path("/string-post") }
        server.put { path("/string-put") }
        server.delete { path("/string-delete") }
        server.patch { path("/string-patch") }
        server.head { path("/string-head") }
        server.options { path("/string-options") }
        
        assertEquals(7, server.findAllUnmatchedRequests().size)
        
        server.shutdown()
    }

    @Test
    fun `should handle custom request types with data classes`() {
        val server = MokksyServer()
        
        data class User(val id: Long, val name: String, val email: String)
        data class Product(val sku: String, val name: String, val price: Double)
        
        server.post(requestType = User::class) { path("/users") }
        server.put(requestType = Product::class) { path("/products") }
        
        assertEquals(2, server.findAllUnmatchedRequests().size)
        
        server.shutdown()
    }

    @Test
    fun `should handle request specifications with multiple matchers`() {
        val server = MokksyServer()
        
        server.post {
            path("/api/users")
            header("Content-Type", "application/json")
            header("Accept", "application/json")
            queryParameter("version", "v1")
        } respondsWith {
            status(201)
            body("""{"id": 123, "created": true}""")
        }
        
        assertEquals(1, server.findAllUnmatchedRequests().size)
        
        server.shutdown()
    }

    @Test
    fun `should handle response with explicit type specification`() {
        val server = MokksyServer()
        
        data class ResponseData(val message: String, val timestamp: Long)
        
        server.get { path("/typed-response") }.respondsWith(ResponseData::class) {
            status(200)
            body(ResponseData("Hello", System.currentTimeMillis()))
        }
        
        server.shutdown()
    }

    @Test
    fun `should handle streaming response with explicit type`() {
        val server = MokksyServer()
        
        data class StreamItem(val id: Int, val data: String)
        
        server.get { path("/typed-stream") }.respondsWithStream(StreamItem::class) {
            // Stream configuration would go here
        }
        
        server.shutdown()
    }

    @Test
    fun `should handle SSE response with explicit type`() {
        val server = MokksyServer()
        
        data class EventData(val event: String, val payload: String)
        
        server.get { path("/typed-sse") }.respondsWithSseStream(EventData::class) {
            // SSE configuration would go here
        }
        
        server.shutdown()
    }

    @Test
    fun `should handle wait parameter correctly`() {
        // Test non-blocking server creation (default behavior)
        val server1 = MokksyServer(wait = false)
        assertTrue(server1.port() > 0)
        server1.shutdown()
        
        // Test with explicit wait parameter in full constructor
        val config = ServerConfiguration(verbose = false)
        val server2 = MokksyServer(
            port = 0,
            host = "127.0.0.1", 
            configuration = config,
            wait = false
        ) {}
        assertTrue(server2.port() > 0)
        server2.shutdown()
    }

    @Test
    fun `should maintain thread safety for concurrent operations`() = runTest {
        val server = MokksyServer()
        
        // Perform concurrent operations
        val operations = (1..20).map { index ->
            async {
                when (index % 4) {
                    0 -> server.get { path("/thread-safe-get-$index") }
                    1 -> server.post { path("/thread-safe-post-$index") }
                    2 -> server.findAllUnmatchedRequests()
                    else -> server.resetMatchCounts()
                }
            }
        }
        
        // Wait for all operations to complete
        operations.awaitAll()
        
        // Should have registered stubs without conflicts
        val unmatchedRequests = server.findAllUnmatchedRequests()
        assertTrue(unmatchedRequests.size >= 10) // At least the GET and POST operations
        
        server.shutdown()
    }

    // Helper functions for testing

    private fun createTestServer(): MokksyServer {
        return MokksyServer()
    }

    private suspend fun delayedAssert(delayMs: Long, assertion: () -> Unit) {
        delay(delayMs)
        assertion()
    }
}