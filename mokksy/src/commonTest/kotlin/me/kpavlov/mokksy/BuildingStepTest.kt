package me.kpavlov.mokksy

import assertk.assertThat
import assertk.assertions.hasSize
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class BuildingStepTest {
    private lateinit var subject: BuildingStep<*>

    private lateinit var name: String

    private lateinit var request: RequestSpecification

    private lateinit var stubs: MutableList<Stub<*>>

    @OptIn(ExperimentalUuidApi::class)
    @BeforeTest
    fun before() {
        name = Uuid.random().toString()
        request = mockk<RequestSpecification>()
        stubs = mutableListOf()

        subject = BuildingStep(name, stubs, request)
    }

    @Test
    fun `Should handle respondsWith`() {
        subject.respondsWith<Any> {}
        assertThat(stubs).hasSize(
            1,
        )
        val stub = stubs.first()
        stub.name shouldBe name
        stub.requestSpecification shouldBe request
    }

    @Test
    fun `Should handle respondsWithStream`() {
        subject.respondsWithStream<Any> {}
        assertThat(stubs).hasSize(
            1,
        )
        val stub = stubs.first()
        stub.name shouldBe name
        stub.requestSpecification shouldBe request
    }

    @Test
    fun `Should handle respondsWithSseStream`() {
        subject.respondsWithSseStream {}
        assertThat(stubs).hasSize(
            1,
        )
        val stub = stubs.first()
        stub.name shouldBe name
        stub.requestSpecification shouldBe request
    }
}
