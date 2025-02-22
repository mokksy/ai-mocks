package me.kpavlov.mokksy

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import me.kpavlov.mokksy.request.RequestSpecification
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class BuildingStepTest {
    private lateinit var subject: BuildingStep<Input>

    private lateinit var name: String

    private lateinit var request: RequestSpecification<Input>
    private lateinit var expectedHttpStatus: HttpStatusCode

    private lateinit var stub: CapturingSlot<Stub<*, *>>
    private lateinit var requestSpecification: CapturingSlot<RequestSpecification<*>>

    private lateinit var addStubCallback: (
        stub: Stub<*, *>,
    ) -> Unit

    @OptIn(ExperimentalUuidApi::class)
    @BeforeTest
    fun before() {
        name = Uuid.random().toString()
        request = mockk()
        addStubCallback = mockk()
        expectedHttpStatus = HttpStatusCode.fromValue(IntRange(100, 500).random())

        stub = slot<Stub<*, *>>()
        requestSpecification = slot<RequestSpecification<*>>()

        every {
            addStubCallback(capture(stub))
        } returns
            Unit

        subject =
            BuildingStep(
                name = name,
                requestSpecification = request,
                registerStub = addStubCallback,
            )
    }

    @Test
    fun `Should handle respondsWith`() {
        subject.respondsWith<Output> {
            httpStatus = expectedHttpStatus
        }

        verifyStub()
    }

    @Test
    fun `Should handle respondsWithStream`() {
        subject.respondsWithStream<OutputChunk> {
            httpStatus = expectedHttpStatus
        }
        verifyStub()
    }

    @Test
    fun `Should handle respondsWithSseStream`() {
        subject.respondsWithSseStream<Output> {
            httpStatus = expectedHttpStatus
        }
        verifyStub()
    }

    private fun verifyStub() {
        stub.captured.apply {
            this.name shouldBe name
            this.requestSpecification shouldBe request
            this.responseDefinitionSupplier shouldNotBeNull { }
        }
    }
}
