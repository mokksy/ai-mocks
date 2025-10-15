package dev.mokksy.mokksy

import dev.mokksy.mokksy.request.RequestSpecification
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import dev.mokksy.mokksy.utils.logger.HttpFormatter
import io.kotest.matchers.shouldBe
import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.RoutingRequest
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
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
    ): Stub<P, T> {
        val spec =
            RequestSpecification(
                // leave all matchers null/empty so it matches any request without touching it
                requestType = requestType,
                priority = priority,
            )
        return Stub(
            configuration = StubConfiguration(name = name, removeAfterMatch = removeAfterMatch),
            requestSpecification = spec,
            responseDefinitionSupplier = responseSupplier(),
        )
    }

    @MockK
    lateinit var routingRequest: RoutingRequest

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
    }
}
