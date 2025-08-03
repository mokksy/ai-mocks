package me.kpavlov.mokksy

import io.kotest.assertions.failure
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.routing.RoutingContext
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import me.kpavlov.mokksy.utils.logger.HttpFormatter
import kotlin.test.*

/**
 * Comprehensive unit tests for RequestHandler functionality.
 * Uses Kotlin Test framework with MockK for mocking dependencies.
 * Tests the handleRequest suspend function and related functionality.
 */
class RequestHandlerTest {

    @MockK
    private lateinit var context: RoutingContext

    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var call: ApplicationCall

    @MockK
    private lateinit var request: ApplicationRequest

    @MockK
    private lateinit var formatter: HttpFormatter

    @MockK
    private lateinit var stubSpecification: RequestSpecification<*>

    private lateinit var stubs: MutableSet<Stub<*, *>>
    private lateinit var configuration: ServerConfiguration

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this)
        stubs = mutableSetOf()
        configuration = ServerConfiguration(verbose = false)
        
        // Default mock setup
        every { context.call } returns call
        every { call.request } returns request
        every { formatter.formatRequest(any()) } returns "Mock Request Format"
    }

    @AfterTest
    fun tearDown() {
        clearAllMocks()
    }

    // =================================
    // Happy Path Tests
    // =================================

    @Test
    fun testHandleRequestWithMatchingStub() = runTest {
        // Given
        val mockStub = createMockStub(true, removeAfterMatch = false)
        stubs.add(mockStub)
        
        // When
        handleRequest(context, application, stubs, configuration, formatter)
        
        // Then
        verify { mockStub.incrementMatchCount() }
        verify { mockStub.respond(call, false) }
        assertEquals(1, stubs.size) // Stub should not be removed
    }

    @Test
    fun testHandleRequestWithMatchingStubRemoval() = runTest {
        // Given
        val mockStub = createMockStub(true, removeAfterMatch = true)
        stubs.add(mockStub)
        configuration = ServerConfiguration(verbose = true)
        
        // When
        handleRequest(context, application, stubs, configuration, formatter)
        
        // Then
        verify { mockStub.incrementMatchCount() }
        verify { mockStub.respond(call, true) }
        assertEquals(0, stubs.size) // Stub should be removed
    }

    @Test
    fun testHandleRequestWithMultipleStubsSelectsFirst() = runTest {
        // Given
        val firstStub = createMockStub(true, removeAfterMatch = false, priority = 1)
        val secondStub = createMockStub(true, removeAfterMatch = false, priority = 2)
        stubs.addAll(listOf(firstStub, secondStub))
        
        // When
        handleRequest(context, application, stubs, configuration, formatter)
        
        // Then
        verify { firstStub.incrementMatchCount() }
        verify { firstStub.respond(call, false) }
        verify(exactly = 0) { secondStub.incrementMatchCount() }
        verify(exactly = 0) { secondStub.respond(any(), any()) }
    }

    @Test
    fun testHandleRequestWithVerboseLogging() = runTest {
        // Given
        val mockStub = createMockStub(true, removeAfterMatch = false, verboseStub = true)
        stubs.add(mockStub)
        configuration = ServerConfiguration(verbose = true)
        
        // When
        handleRequest(context, application, stubs, configuration, formatter)
        
        // Then
        verify { mockStub.respond(call, true) } // Should pass verbose=true
    }

    // =================================
    // No Match Scenarios
    // =================================

    @Test
    fun testHandleRequestWithNoMatchingStubs() = runTest {
        // Given
        val mockStub = createMockStub(false, removeAfterMatch = false)
        stubs.add(mockStub)
        
        // When/Then
        assertFailsWith<AssertionError> {
            handleRequest(context, application, stubs, configuration, formatter)
        }
        
        verify(exactly = 0) { mockStub.incrementMatchCount() }
        verify(exactly = 0) { mockStub.respond(any(), any()) }
    }

    @Test
    fun testHandleRequestWithEmptyStubSet() = runTest {
        // Given - empty stubs set
        
        // When/Then
        assertFailsWith<AssertionError> {
            handleRequest(context, application, stubs, configuration, formatter)
        }
    }

    @Test
    fun testHandleRequestNoMatchWithVerboseLogging() = runTest {
        // Given
        val mockStub = createMockStub(false, removeAfterMatch = false)
        stubs.add(mockStub)
        configuration = ServerConfiguration(verbose = true)
        every { mockStub.toLogString() } returns "Mock Stub Log"
        
        // When/Then
        assertFailsWith<AssertionError> {
            handleRequest(context, application, stubs, configuration, formatter)
        }
        
        verify { formatter.formatRequest(request) }
        verify { mockStub.toLogString() }
    }

    // =================================
    // Error Handling Tests
    // =================================

    @Test
    fun testHandleRequestWithStubEvaluationFailure() = runTest {
        // Given
        val mockStub = createMockStub(matchResult = null, removeAfterMatch = false) // Simulation of evaluation failure
        stubs.add(mockStub)
        configuration = ServerConfiguration(verbose = true)
        
        // When/Then
        assertFailsWith<AssertionError> {
            handleRequest(context, application, stubs, configuration, formatter)
        }
    }

    @Test
    fun testHandleRequestWithStubEvaluationException() = runTest {
        // Given
        val mockStub = createMockStubWithException()
        stubs.add(mockStub)
        configuration = ServerConfiguration(verbose = true)
        
        // When/Then
        assertFailsWith<AssertionError> {
            handleRequest(context, application, stubs, configuration, formatter)
        }
    }

    // =================================
    // Configuration Tests
    // =================================

    @Test
    fun testHandleRequestWithNonVerboseConfiguration() = runTest {
        // Given
        val mockStub = createMockStub(true, removeAfterMatch = false)
        stubs.add(mockStub)
        configuration = ServerConfiguration(verbose = false)
        
        // When
        handleRequest(context, application, stubs, configuration, formatter)
        
        // Then
        verify { mockStub.respond(call, false) } // Should pass verbose=false
    }

    @Test
    fun testHandleRequestRemovalLogging() = runTest {
        // Given
        val mockStub = createMockStub(true, removeAfterMatch = true)
        stubs.add(mockStub)
        configuration = ServerConfiguration(verbose = true)
        every { mockStub.toLogString() } returns "Removed Stub Log"
        
        // When
        handleRequest(context, application, stubs, configuration, formatter)
        
        // Then
        assertEquals(0, stubs.size)
        verify { mockStub.toLogString() }
    }

    // =================================
    // Stub Comparison and Ordering Tests
    // =================================

    @Test
    fun testStubComparatorOrdering() = runTest {
        // Given
        val highPriorityStub = createMockStub(true, removeAfterMatch = false, priority = 1)
        val lowPriorityStub = createMockStub(true, removeAfterMatch = false, priority = 10)
        stubs.addAll(listOf(lowPriorityStub, highPriorityStub)) // Add in reverse order
        
        // When
        handleRequest(context, application, stubs, configuration, formatter)
        
        // Then - Should select the stub with lower priority value (higher priority)
        verify { highPriorityStub.incrementMatchCount() }
        verify { highPriorityStub.respond(call, false) }
        verify(exactly = 0) { lowPriorityStub.incrementMatchCount() }
    }

    // =================================
    // Request Specification Tests
    // =================================

    @Test
    fun testRequestSpecificationMatching() = runTest {
        // Given
        val mockStub = mockk<Stub<*, *>>()
        val mockSpec = mockk<RequestSpecification<*>>()
        val mockConfig = StubConfiguration(removeAfterMatch = false, verbose = false)
        
        every { mockStub.requestSpecification } returns mockSpec
        every { mockStub.configuration } returns mockConfig
        every { mockStub.incrementMatchCount() } just Runs
        every { mockStub.respond(any(), any()) } just Runs
        every { mockSpec.matches(request) } returns Result.success(true)
        
        stubs.add(mockStub)
        
        // When
        handleRequest(context, application, stubs, configuration, formatter)
        
        // Then
        verify { mockSpec.matches(request) }
        verify { mockStub.incrementMatchCount() }
        verify { mockStub.respond(call, false) }
    }

    // =================================
    // Concurrency and Performance Tests
    // =================================

    @Test
    fun testHandleMultipleRequestsConcurrently() = runTest {
        // Given
        val stub1 = createMockStub(true, removeAfterMatch = false)
        val stub2 = createMockStub(true, removeAfterMatch = false)
        stubs.addAll(listOf(stub1, stub2))
        
        // When - Simulate multiple concurrent requests
        repeat(5) {
            handleRequest(context, application, stubs, configuration, formatter)
        }
        
        // Then - Should handle all requests (first matching stub wins each time)
        verify(exactly = 5) { stub1.incrementMatchCount() }
        verify(exactly = 5) { stub1.respond(call, false) }
        verify(exactly = 0) { stub2.incrementMatchCount() }
    }

    @Test
    fun testStubRemovalConcurrency() = runTest {
        // Given
        val removableStub = createMockStub(true, removeAfterMatch = true)
        val persistentStub = createMockStub(true, removeAfterMatch = false, priority = 2)
        stubs.addAll(listOf(removableStub, persistentStub))
        
        // When - First request should remove the first stub
        handleRequest(context, application, stubs, configuration, formatter)
        // Second request should use the remaining stub
        handleRequest(context, application, stubs, configuration, formatter)
        
        // Then
        verify(exactly = 1) { removableStub.incrementMatchCount() }
        verify(exactly = 1) { persistentStub.incrementMatchCount() }
        assertEquals(1, stubs.size) // Only persistent stub remains
    }

    // =================================
    // Edge Cases and Boundary Tests
    // =================================

    @Test
    fun testHandleRequestWithLargeStubSet() = runTest {
        // Given
        val manyStubs = (1..100).map { i ->
            createMockStub(i == 50, removeAfterMatch = false, priority = i) // Only 50th stub matches
        }
        stubs.addAll(manyStubs)
        
        // When
        handleRequest(context, application, stubs, configuration, formatter)
        
        // Then
        val matchingStub = manyStubs[49] // 50th stub (0-indexed)
        verify { matchingStub.incrementMatchCount() }
        verify { matchingStub.respond(call, false) }
    }

    @Test
    fun testHandleRequestFormatterIntegration() = runTest {
        // Given
        val mockStub = createMockStub(false, removeAfterMatch = false)
        stubs.add(mockStub)
        every { formatter.formatRequest(request) } returns "Detailed Request Format"
        
        // When/Then
        assertFailsWith<AssertionError> {
            handleRequest(context, application, stubs, configuration, formatter)
        }
        
        verify { formatter.formatRequest(request) }
    }

    // =================================
    // Helper Methods
    // =================================

    private fun createMockStub(
        matchResult: Boolean?,
        removeAfterMatch: Boolean,
        priority: Int = 1,
        verboseStub: Boolean = false
    ): Stub<*, *> {
        val mockStub = mockk<Stub<*, *>>()
        val mockSpec = mockk<RequestSpecification<*>>()
        val stubConfig = StubConfiguration(removeAfterMatch = removeAfterMatch, verbose = verboseStub)
        
        every { mockStub.requestSpecification } returns mockSpec
        every { mockStub.configuration } returns stubConfig
        every { mockStub.incrementMatchCount() } just Runs
        every { mockStub.respond(any(), any()) } just Runs
        every { mockStub.toLogString() } returns "Mock Stub [$priority]"
        
        when (matchResult) {
            true -> every { mockSpec.matches(request) } returns Result.success(true)
            false -> every { mockSpec.matches(request) } returns Result.success(false)
            null -> every { mockSpec.matches(request) } returns Result.failure(RuntimeException("Evaluation failed"))
        }
        
        // Mock comparable behavior for stub comparison
        every { mockStub.compareTo(any()) } answers { 
            val other = firstArg<Stub<*, *>>()
            priority.compareTo((other.toLogString().substringAfter("[").substringBefore("]").toIntOrNull() ?: Int.MAX_VALUE))
        }
        
        return mockStub
    }

    private fun createMockStubWithException(): Stub<*, *> {
        val mockStub = mockk<Stub<*, *>>()
        val mockSpec = mockk<RequestSpecification<*>>()
        val stubConfig = StubConfiguration(removeAfterMatch = false, verbose = false)
        
        every { mockStub.requestSpecification } returns mockSpec
        every { mockStub.configuration } returns stubConfig
        every { mockStub.toLogString() } returns "Exception Stub"
        every { mockSpec.matches(request) } throws RuntimeException("Matching failed")
        
        return mockStub
    }

    /**
     * Data class representing server configuration for testing
     */
    data class ServerConfiguration(
        val verbose: Boolean = false
    )

    /**
     * Data class representing stub configuration for testing
     */
    data class StubConfiguration(
        val removeAfterMatch: Boolean = false,
        val verbose: Boolean = false
    )
}