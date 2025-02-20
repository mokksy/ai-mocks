package me.kpavlov.mokksy

import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class BuildingStepTest {
    private lateinit var subject: BuildingStep<Input>

    private lateinit var name: String

    private lateinit var request: RequestSpecification<Input>

    private lateinit var stub: Stub<Input, *>

    private fun addStub(stub: Stub<Input, *>) {
        this.stub = stub
    }

    @OptIn(ExperimentalUuidApi::class)
    @BeforeTest
    fun before() {
        name = Uuid.random().toString()
        request = mockk()

        subject = BuildingStep(name, this::addStub, request)
    }

    @Test
    fun `Should handle respondsWith`() {
        subject.respondsWith<Output> {}
        stub.name shouldBe name
        stub.requestSpecification shouldBe request
    }

    @Test
    fun `Should handle respondsWithStream`() {
        subject.respondsWithStream<OutputChunk> {}
        stub.name shouldBe name
        stub.requestSpecification shouldBe request
    }

    @Test
    fun `Should handle respondsWithSseStream`() {
        subject.respondsWithSseStream<Any> {}
        stub.name shouldBe name
        stub.requestSpecification shouldBe request
    }
}
