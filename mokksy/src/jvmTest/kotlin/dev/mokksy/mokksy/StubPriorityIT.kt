package dev.mokksy.mokksy

import assertk.assertThat
import assertk.assertions.isEqualTo
import dev.mokksy.test.utils.runIntegrationTest
import io.kotest.matchers.equals.beEqual
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import org.junit.jupiter.api.Test

internal class StubPriorityIT : AbstractIT() {
    @Test
    fun `Should consider stub priority (desc order)`() =
        runIntegrationTest {
            val path = "/stub-priority-$seed"
            mokksy
                .get {
                    this.path = beEqual(path)
                    this.priority = 1 // higher priority
                }.respondsWith(String::class) {
                    body = "Expected response"
                }

            mokksy
                .get {
                    this.path = beEqual(path)
                    this.priority = 2 // lower priority
                }.respondsWith(String::class) {
                    body = "Unexpected response"
                }

            // when
            val result =
                client.get(path)
            // then
            assertThat(result.status).isEqualTo(HttpStatusCode.OK)
            assertThat(result.bodyAsText()).isEqualTo("Expected response")
        }

    @Test
    fun `Should consider stub priority (asc order)`() =
        runIntegrationTest {
            mokksy
                .get {
                    this.path = beEqual(path)
                    this.priority = 2 // lower priority
                }.respondsWith(String::class) {
                    body = "Unexpected response"
                }

            val path = "/stub-priority-$seed"
            mokksy
                .get {
                    this.path = beEqual(path)
                    this.priority = 1 // higher priority
                }.respondsWith(String::class) {
                    body = "Expected response"
                }

            // when
            val result =
                client.get(path)
            // then
            with(result) {
                status shouldBe HttpStatusCode.OK
                bodyAsText() shouldBe "Expected response"
            }
        }
}
