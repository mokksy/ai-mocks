package me.kpavlov.mokksy

import assertk.assertThat
import assertk.assertions.isNegative
import assertk.assertions.isPositive
import assertk.assertions.isZero
import me.kpavlov.mokksy.request.RequestSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import me.kpavlov.mokksy.response.ResponseDefinitionSupplier
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

internal class StubComparatorTest {
    lateinit var request1: RequestSpecification<Int>
    lateinit var request2: RequestSpecification<Int>
    lateinit var response: AbstractResponseDefinition<Any, String>
    lateinit var responseDefinitionSupplier: ResponseDefinitionSupplier<Int, String>

    @BeforeEach
    fun beforeEach() {
        response = mock()
        responseDefinitionSupplier = mock()
    }

    @Test
    fun `compare should compare by creationOrder when priorities are equal`() {
        request1 = RequestSpecification(priority = 1)
        request2 = RequestSpecification(priority = 1)

        val stub1 =
            Stub<Int, String>(
                requestSpecification = request1,
                responseDefinitionSupplier = responseDefinitionSupplier,
            )
        val stub2 =
            Stub(
                requestSpecification = request2,
                responseDefinitionSupplier = responseDefinitionSupplier,
            )

        val result = StubComparator.compare(stub1, stub2)

        assertThat(result).isNegative()
    }

    @Test
    fun `compare should return a negative value when the first priority is less`() {
        request1 = RequestSpecification(priority = 1)
        request2 = RequestSpecification(priority = 2)

        val stub1 =
            Stub(
                requestSpecification = request1,
                responseDefinitionSupplier = responseDefinitionSupplier,
            )
        val stub2 =
            Stub(
                requestSpecification = request2,
                responseDefinitionSupplier = responseDefinitionSupplier,
            )

        val result = StubComparator.compare(stub1, stub2)

        assertThat(result).isNegative()
    }

    @Test
    fun `compare should return a positive value when the first priority is greater`() {
        request1 = RequestSpecification(priority = 2)
        request2 = RequestSpecification(priority = 1)

        val stub1 =
            Stub(
                requestSpecification = request1,
                responseDefinitionSupplier = responseDefinitionSupplier,
            )
        val stub2 =
            Stub(
                requestSpecification = request2,
                responseDefinitionSupplier = responseDefinitionSupplier,
            )

        val result = StubComparator.compare(stub1, stub2)

        assertThat(result).isPositive()
    }

    @Test
    fun `compare should return zero when stubs are same`() {
        request1 = RequestSpecification()

        val stub1 =
            Stub(
                requestSpecification = request1,
                responseDefinitionSupplier = responseDefinitionSupplier,
            )

        val result = StubComparator.compare(stub1, stub1)

        assertThat(result).isZero()
    }
}
