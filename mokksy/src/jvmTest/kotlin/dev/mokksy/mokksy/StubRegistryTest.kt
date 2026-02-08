package dev.mokksy.mokksy

import dev.mokksy.mokksy.request.RequestSpecification
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import dev.mokksy.mokksy.utils.logger.HttpFormatter
import io.kotest.matchers.Matcher
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.path
import io.ktor.server.routing.RoutingRequest
import io.ktor.util.logging.Logger
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
class StubRegistryTest {
    private fun <T : Any> responseSupplier(): (
        ApplicationCall,
    ) -> AbstractResponseDefinition<T> =
        { _ ->
            object : AbstractResponseDefinition<T>(
                contentType = ContentType.Any,
                httpStatusCode = 200,
            ) {
                override suspend fun writeResponse(
                    call: ApplicationCall,
                    verbose: Boolean,
                ) {
                    // no-op for tests
                }
            }
        }

    private fun <P : Any, T : Any> stub(
        name: String? = null,
        priority: Int? = null,
        removeAfterMatch: Boolean = false,
        requestType: KClass<P>,
        path: String? = null,
    ): Stub<P, T> {
        val spec =
            RequestSpecification(
                requestType = requestType,
                priority = priority,
                path =
                    path?.let {
                        dev.mokksy.mokksy.request
                            .pathEqual(it)
                    },
            )
        return Stub(
            configuration = StubConfiguration(name = name, removeAfterMatch = removeAfterMatch),
            requestSpecification = spec,
            responseDefinitionSupplier = responseSupplier(),
        )
    }

    @MockK
    lateinit var routingRequest: RoutingRequest

    @MockK(relaxed = true)
    lateinit var logger: Logger

    @MockK(relaxed = true)
    lateinit var formatter: HttpFormatter

    @Nested
    inner class AddAndGetAll {
        @Test
        fun `should add stubs and return sorted snapshot`() =
            runTest {
                val registry = StubRegistry()

                val s1 =
                    stub<String, String>(name = "s1", priority = 10, requestType = String::class)
                val s2 =
                    stub<String, String>(name = "s2", priority = 1, requestType = String::class)
                val s3 =
                    stub<String, String>(name = "s3", priority = 10, requestType = String::class)

                registry.add(s1)
                registry.add(s2)
                registry.add(s3)

                val all = registry.getAll().toList()

                // Expect ordering by priority asc, then creation order
                all shouldBe listOf(s2, s1, s3)
            }

        @Test
        fun `should throw on duplicate stub`() =
            runTest {
                val registry = StubRegistry()
                val s1 =
                    stub<String, String>(name = "dup", priority = 5, requestType = String::class)

                registry.add(s1)

                assertFailsWith<IllegalArgumentException> {
                    registry.add(s1)
                }
            }
    }

    @Nested
    inner class FindMatchingStub {
        @Test
        fun `should return best match by priority and increment matchCount`() =
            runTest {
                val registry = StubRegistry()
                val lowPrio =
                    stub<String, String>(name = "low", priority = 100, requestType = String::class)
                val highPrio =
                    stub<String, String>(name = "high", priority = 1, requestType = String::class)

                registry.add(lowPrio)
                registry.add(highPrio)

                val matched =
                    registry.findMatchingStub(
                        request = routingRequest,
                        verbose = false,
                        logger = mockk(relaxed = true),
                        formatter =
                            HttpFormatter(),
                    )

                matched shouldBe highPrio
                highPrio.matchCount() shouldBe 1
                lowPrio.matchCount() shouldBe 0
            }

        @Test
        fun `should break ties by creation order`() =
            runTest {
                val registry = StubRegistry()
                val first =
                    stub<String, String>(name = "first", priority = 10, requestType = String::class)
                val second =
                    stub<String, String>(
                        name = "second",
                        priority = 10,
                        requestType = String::class,
                    )

                registry.add(first)
                registry.add(second)

                val matched =
                    registry.findMatchingStub(
                        request = routingRequest,
                        verbose = false,
                        logger = mockk(relaxed = true),
                        formatter =
                            HttpFormatter(),
                    )

                matched shouldBe first
            }

        @Test
        fun `should remove stub after match when configured`() =
            runTest {
                val registry = StubRegistry()
                val removable =
                    stub<String, String>(
                        name = "once",
                        priority = 5,
                        removeAfterMatch = true,
                        requestType = String::class,
                    )

                registry.add(removable)

                val matched1 =
                    registry.findMatchingStub(
                        request = routingRequest,
                        verbose = false,
                        logger = mockk(relaxed = true),
                        formatter =
                            HttpFormatter(),
                    )
                matched1 shouldBe removable
                removable.matchCount() shouldBe 1

                // Next time it should not be present
                val matched2 =
                    registry.findMatchingStub(
                        request = routingRequest,
                        verbose = false,
                        logger = mockk(relaxed = true),
                        formatter =
                            HttpFormatter(),
                    )
                matched2 shouldBe null
                registry.getAll().isEmpty() shouldBe true
            }
    }

    @Nested
    inner class RemoveSpecificStub {
        @Test
        fun `should remove stub and report status`() =
            runTest {
                val registry = StubRegistry()
                val s = stub<String, String>(name = "s", priority = 7, requestType = String::class)

                // not present yet
                registry.remove(s) shouldBe false

                registry.add(s)
                registry.remove(s) shouldBe true
                registry.remove(s) shouldBe false
            }

        @Test
        fun `should return null when no stub matches`() =
            runTest {
                val registry = StubRegistry()
                val s1 =
                    stub<String, String>(
                        name = "mismatch",
                        requestType = String::class,
                        path = "/expected",
                    )
                registry.add(s1)

                every { routingRequest.path() } returns "/actual"

                val matched =
                    registry.findMatchingStub(
                        request = routingRequest,
                        verbose = false,
                        logger = logger,
                        formatter = formatter,
                    )

                matched shouldBe null
            }

        @Test
        fun `should log warning when condition evaluation fails and verbose is true`() =
            runTest {
                val registry = StubRegistry()
                // Use a path matcher instead of a body matcher to avoid depending on
                // Ktor's receive() extension function which cannot be mocked by MockK
                val failingStub =
                    Stub<String, String>(
                        configuration = StubConfiguration(name = "failing"),
                        requestSpecification =
                            RequestSpecification(
                                requestType = String::class,
                                path =
                                    Matcher {
                                        throw IllegalArgumentException("Boom!")
                                    },
                            ),
                        responseDefinitionSupplier = responseSupplier(),
                    )

                registry.add(failingStub)

                coEvery { formatter.formatRequest(any()) } returns "formatted request"
                every { routingRequest.path() } returns "/test"

                registry.findMatchingStub(
                    request = routingRequest,
                    verbose = true,
                    logger = logger,
                    formatter = formatter,
                )

                verify {
                    logger.warn(
                        match<String> { it.contains("Failed to evaluate condition for stub") },
                        any<Throwable>(),
                    )
                }
            }
    }

    @Nested
    inner class Concurrency {
        @Test
        fun `should handle concurrent additions`() =
            runTest {
                val registry = StubRegistry()
                val count = 100
                val stubsToAdd =
                    (1..count).map {
                        stub<String, String>(name = "stub-$it", requestType = String::class)
                    }

                val jobs =
                    stubsToAdd.map { s ->
                        async {
                            registry.add(s)
                        }
                    }
                jobs.awaitAll()

                registry.getAll().size shouldBe count
            }

        @Test
        fun `should handle concurrent add and remove`() =
            runTest {
                val registry = StubRegistry()
                val count = 100
                val stubs =
                    (1..count).map {
                        stub<String, String>(name = "stub-$it", requestType = String::class)
                    }

                // Add all first
                stubs.forEach { registry.add(it) }

                val jobs =
                    stubs.map { s ->
                        async {
                            registry.remove(s)
                        }
                    }
                jobs.awaitAll()

                registry.getAll() shouldBe emptySet()
            }

        @Test
        fun `should handle concurrent findMatchingStub with removal`() =
            runTest {
                val registry = StubRegistry()
                val count = 50
                // Use different priorities to avoid ties being the only thing tested
                val stubs =
                    (1..count).map {
                        stub<String, String>(
                            name = "stub-$it",
                            priority = it,
                            removeAfterMatch = true,
                            requestType = String::class,
                        )
                    }

                stubs.forEach { registry.add(it) }

                val jobs =
                    (1..count).map {
                        async {
                            registry.findMatchingStub(routingRequest, false, logger, formatter)
                        }
                    }
                val results = jobs.awaitAll()

                results.filterNotNull().size shouldBe count
                results.toSet().size shouldBe count // All unique
                registry.getAll().shouldBeEmpty()
            }
    }

    @Nested
    inner class Snapshot {
        @Test
        fun `getAll should return a consistent snapshot`() =
            runTest {
                val registry = StubRegistry()
                val s1 = stub<String, String>(name = "s1", requestType = String::class)
                val s2 = stub<String, String>(name = "s2", requestType = String::class)

                registry.add(s1)
                val snapshot1 = registry.getAll()
                snapshot1 shouldContainExactly listOf(s1)

                registry.add(s2)
                val snapshot2 = registry.getAll()

                snapshot1 shouldContainExactly listOf(s1)
                snapshot2 shouldContainExactly listOf(s1, s2)
            }
    }
}
