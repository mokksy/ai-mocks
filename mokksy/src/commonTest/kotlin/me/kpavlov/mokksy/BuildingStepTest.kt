package me.kpavlov.mokksy

import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlin.ranges.IntRange
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class BuildingStepTest {
    private lateinit var subject: BuildingStep<Input>

    private lateinit var name: String

    private lateinit var request: RequestSpecification<Input>
    private lateinit var expectedHttpStatus: HttpStatusCode

    private lateinit var stubName: CapturingSlot<String?>
    private lateinit var requestSpecification: CapturingSlot<RequestSpecification<*>>
    private lateinit var responseDefinition: CapturingSlot<AbstractResponseDefinition<*>>

    private lateinit var registerStubCallback: (
        name: String?,
        requestSpecification: RequestSpecification<*>,
        responseDefinition: AbstractResponseDefinition<*>,
    ) -> Unit

    @OptIn(ExperimentalUuidApi::class)
    @BeforeTest
    fun before() {
        name = Uuid.random().toString()
        request = mockk()
        registerStubCallback = mockk()
        expectedHttpStatus = HttpStatusCode.fromValue(IntRange(100, 500).random())

        stubName = slot<String?>()
        requestSpecification = slot<RequestSpecification<*>>()
        responseDefinition = slot<AbstractResponseDefinition<*>>()

        every {
            registerStubCallback(
                captureNullable(stubName),
                capture(requestSpecification),
                capture(responseDefinition),
            )
        } returns
            Unit

        subject = BuildingStep(name, registerStubCallback, request)
    }

    @Test
    fun `Should handle respondsWith`() {
        subject.respondsWith<Output> {
            httpStatus = expectedHttpStatus
        }

        verifyStub()
    }

    private fun verifyStub() {
        stubName.captured shouldBe name
        requestSpecification.captured shouldBe request
        responseDefinition.captured.httpStatus shouldBe expectedHttpStatus
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
}
